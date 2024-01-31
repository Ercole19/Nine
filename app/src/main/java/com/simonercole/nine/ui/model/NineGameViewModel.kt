package com.simonercole.nine.ui.model

import android.app.Application
import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simonercole.nine.db.DBRepo
import com.simonercole.nine.db.GameDB
import com.simonercole.nine.ui.screens.getTimerLabel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class NineGameViewModel(application: Application): AndroidViewModel(application) {

    lateinit var sequenceToGuess: CharArray
    var gameStatus : GameStatus = GameStatus.Started
    var firstGuess = MutableLiveData(true)
    var musicStarted = MutableLiveData(false)
    private val gameDB = GameDB.getInstance(application.baseContext)
    open val repository = DBRepo(gameDB.getDAO())
    val inputs =  MutableLiveData(mutableListOf(HashMap<Int, Pair<String, Char>>()))
    var liveInput = MutableLiveData(CharArray(9) { ' ' })
    var focusArray = MutableLiveData(IntArray(9) { 1 * it })
    var distanceArray = IntArray(9) { -1 }
    var showConfirm = MutableLiveData(false)
    var startingKeyboard = CharArray(9) { ' ' }
    var sequenceStatus = MutableLiveData(IntArray(9) { 0 })
    var guessedChars = CharArray(9) { ' ' }
    lateinit var difficulty  : String
    var attempts by Delegates.notNull<Int>()
    var currentAttempts = MutableLiveData(0)
    val gameWon = MutableLiveData(false)
    val gameLost = MutableLiveData(false)
    val timerExpired = MutableLiveData(mutableStateOf(false))
    var totalTime by Delegates.notNull<Int>()
    val attemptsFinished = MutableLiveData(mutableStateOf(false))
    var job: Job? = null
    val _timerValue = MutableLiveData(mutableStateOf(0))
    val _play = MutableLiveData(false)
    private var bestTime = ""
    val newTime = MutableLiveData("")
    val newBestTime = MutableLiveData(false)

    fun setUpGame(diff: String) {
        val backgroundJob = CoroutineScope(Dispatchers.IO).launch {
            bestTime = if (repository.getMinTime(diff) != null) {
                repository.getMinTime(diff)!!
            } else "99 : 99"
        }
        runBlocking {
            backgroundJob.join()
        }
        difficulty = diff

        _timerValue.value!!.value = when (diff) {
            "Easy" -> 100
            "Medium" -> 80
            else -> 50
        }
        totalTime = _timerValue.value!!.value
        attempts = when(diff) {
            "Easy" -> 4
            "Medium" -> 4
            else -> 3
        }
        sequenceToGuess = createSequenceToGuess()
        sequenceToGuess.shuffle()
        createKeyboard()
        focusArray.value!![0] =0
        gameStatus = GameStatus.OnGoing
        inputs.value!!.removeAt(0)
    }
    fun makeGuess() {
        if (firstGuess.value == true) {
            startTimer()
            firstGuess.value = false
            musicStarted.value = true
        }
        musicStarted.value = true
        calculateDistance()
        currentAttempts.value = currentAttempts.value!! + 1
        updateInputs()
        clearInput()
        updateFocusByWrite(0)
        checkGameStatus()
    }
    fun startTimer() {
        if (_timerValue.value!!.value == 0) _timerValue.value!!.value = 0
        job?.cancel()
        job = GlobalScope.launch (Dispatchers.Main) {
            withContext(Dispatchers.Main){
                delay(timeMillis = 200)
                while (isActive) {
                    if (_timerValue.value!!.value <= 0) {
                        job?.cancel()
                        _play.value = false
                        timerExpired()
                        return@withContext
                    }
                    delay(timeMillis = 1000)
                    _timerValue.value!!.value = _timerValue.value!!.value - 1
                    _play.value = true
                }}

        }
    }
    fun checkGameStatus() {
        newTime.value = getTimerLabel(totalTime - _timerValue.value!!.value)
        if (currentAttempts.value == attempts) {
            if (sequenceStatus.value!!.contains(0).not()) {
                pause()
                if (parseIt(bestTime) > parseIt(newTime.value!!)) {
                    bestTime = newTime.value!!
                    newBestTime.value = true
                }
                gameWon.value = true
                saveGameToDB()
            }
            else {
                pause()
                gameLost.value = true
                attemptsFinished.value!!.value = true
                saveGameToDB()
            }
        }
        else {
            if (sequenceStatus.value!!.contains(0).not()) {
                pause()
                if (parseIt(bestTime) > parseIt(newTime.value!!)) {
                    bestTime = newTime.value!!
                    newBestTime.value = true
                }
                gameWon.value = true
                saveGameToDB()
            }
        }
    }
    fun saveGameToDB() {
        val game = currentAttempts.value?.let {
            Game(
                difficulty = difficulty,
                attempts = it,
                time = newTime.value!!,
                dateTime = LocalDateTime.now().toString(),
                win = gameWon.value == true,
            )
        }
        if (game != null) {
            repository.insertGame(game)
        }
    }
    fun timerExpired() {
        gameLost.value = true
        timerExpired.value!!.value = true
        saveGameToDB()
    }

    fun createKeyboard() {
        for ((i) in sequenceToGuess.withIndex()) {
            startingKeyboard[i] = sequenceToGuess[i]
        }
        startingKeyboard.shuffle()
    }

    fun createSequenceToGuess(): CharArray {
        return NineGameUtils.symbols.toList().shuffled().take(9).joinToString("").toCharArray()
    }

    fun updateInput(index: Int, char: Char) {
        val newInput = CharArray(9) { ' ' }
        liveInput.value?.forEachIndexed { i, _ ->
            if (index == i) newInput[i] = char
            else newInput[i] = liveInput.value!![i]
        }
        liveInput.value = newInput
        val focusIndex = focusArray.value?.indexOf(0)
        if (focusIndex != null) {
            for (idx in focusIndex ..8) {
                if (sequenceStatus.value!![idx] == 0) {
                    updateFocusByWrite(idx)
                    break
                }

            }
        }
        showConfirm.value = liveInput.value?.contains(' ') == false

    }

     fun updateInputs() {
         val map =  HashMap<Int, Pair<String, Char>>()
         val newList = mutableListOf(HashMap<Int, Pair<String, Char>>())
         newList.removeAt(0)

         inputs.value!!.forEach { hashMap ->
             newList.add(hashMap)
         }

         for (i in 0..8) {
             if( currentAttempts.value == 1 && difficulty == "Hard") {
                 if (i%2!=0 || sequenceStatus.value!![i] == 1) {map[i]  = Pair(distanceArray[i].toString(), liveInput.value!![i])}
                 else {map[i]  = Pair("?", liveInput.value!![i])}
             }
             else if( currentAttempts.value == 2 && difficulty == "Hard") {
                 if (i%2 ==0 || sequenceStatus.value!![i] == 1) {map[i]  = Pair(distanceArray[i].toString(), liveInput.value!![i])}
                 else {map[i]  = Pair("?", liveInput.value!![i])}
             }
             else {map[i]  = Pair(distanceArray[i].toString(), liveInput.value!![i])}

         }
         newList.add(map)
         inputs.value  = newList
     }

    fun resetMusic(mediaPlayer: MediaPlayer) {
        musicStarted.value = false
        mediaPlayer.stop()
    }

    fun clearInput() {
        val newInput = CharArray(9) { ' ' }
        for (i in 0..8) {
            if (sequenceStatus.value?.get(i) == 1) newInput[i] = liveInput.value!![i]
            else newInput[i] = ' '
        }
        liveInput.value = newInput
        showConfirm.value = false
    }

    fun calculateDistance() {
        liveInput.value?.forEachIndexed { index, c ->
            val correctIndex = sequenceToGuess.indexOf(c)
            val finalDistance: Int
            val rawDistance = kotlin.math.abs(correctIndex - index)
            finalDistance = when (rawDistance) {
                in 0..4 -> rawDistance
                else -> 9 - rawDistance
            }
            distanceArray[index] = finalDistance
            if (finalDistance == 0) sequenceStatus.value?.set(index, 1)
        }
    }

    fun deleteChar(index: Int) {
        val newInput = CharArray(9) { ' ' }
        liveInput.value?.forEachIndexed { i, _ ->
            if (index == i) newInput[i] = ' '
            else newInput[i] = liveInput.value!![i]
        }
        liveInput.value = newInput
        if (showConfirm.value == true) showConfirm.value = false
    }

    fun updateFocusByTouch(index:Int) {
        val newInput = IntArray(9) { 1 }
        var newIndex: Int = index
        if (sequenceStatus.value!![index] == 0) {
            newInput[newIndex] = 0
            focusArray.value = newInput
        }
        else {updateFocusByWrite(index)}

    }

    fun updateFocusByWrite(index: Int) {
        val newInput = IntArray(9) { 1 }
        var newIndex: Int = index
        while (true) {
            if (liveInput.value?.contains(' ')?.not() == true) break
            if (liveInput.value?.get(newIndex) != ' ' || sequenceStatus.value?.get(newIndex) == 1) {
                if (newIndex + 1 > 8) {
                    newIndex = 0
                } else {
                    newIndex++
                }
            } else {
                newInput[newIndex] = 0
                break
            }
        }
        focusArray.value = newInput
    }
    private fun parseIt(time:  String) : Int {
        val firstOne = time.substring(0,2)
        val secondOne = time.substring(5,7)
        val result = firstOne + secondOne
        return result.toInt()
    }

    fun pause() {
        job?.cancel()
        _play.value = false
    }

    fun getTime() {
        newTime.value = getTimerLabel(totalTime - _timerValue.value!!.value)
    }

    fun stop() {
        job?.cancel()
        _timerValue.value!!.value = 0
        _play.value = true
    }
}

object TimerFormat {
    private const val FORMAT = "%02d:%02d"

    fun Long.timeFormat(): String = String.format(
        FORMAT,
        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
        TimeUnit.MILLISECONDS.toSeconds(this) % 60
    )
}

@Suppress("UNCHECKED_CAST")
class NineGameViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return NineGameViewModel(application) as T
    }
}