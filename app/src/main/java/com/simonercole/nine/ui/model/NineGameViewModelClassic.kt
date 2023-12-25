package com.simonercole.nine.ui.model

import android.app.Application
import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simonercole.nine.db.DBRepo
import com.simonercole.nine.db.GameDB
import com.simonercole.nine.ui.model.TimerFormat.timeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
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
import kotlin.properties.Delegates

class NineGameViewModelClassic(application: Application) : NineGameViewModel(application = application) {
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
    private val gameDB = GameDB.getInstance(application.baseContext)
    private val repository = DBRepo(gameDB.getDAO())
    val newBestTime = MutableLiveData(false)




    override fun setUpGame(diff: String) {
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
        sessionStarted = true
        inputs.value!!.removeAt(0)
    }

    override fun makeGuess() {
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

    override fun checkGameStatus() {
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

    override fun saveGameToDB() {
        val game = currentAttempts.value?.let {
            GameClassic(
                difficulty = difficulty,
                attempts = it,
                time = newTime.value!!,
                dateTime = LocalDateTime.now().toString(),
                win = gameWon.value == true,
            )
        }
        if (game != null) {
            repo.insertGameClassic(game)
        }
    }

    override fun timerExpired() {
        gameLost.value = true
        timerExpired.value!!.value = true
        saveGameToDB()
    }

    override fun updateInputs() {
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
    fun pause() {
        job?.cancel()
        _play.value = false
    }

    fun stop() {
        job?.cancel()
        _timerValue.value!!.value = 0
        _play.value = true
    }


    @OptIn(DelicateCoroutinesApi::class)
    override fun startTimer() {
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

    fun restart() {
        stop()
        startTimer()
    }
    private fun parseIt(time:  String) : Int {
        val firstOne = time.substring(0,2)
        val secondOne = time.substring(5,7)
        val result = firstOne + secondOne
        return result.toInt()
    }


    fun getTimerLabel(value: Int): String {
        return "${padding(value / 60)} : ${padding(value % 60)}"
    }

    fun padding(value: Int) = if (value < 10) ("0$value") else "" + value


}


@Suppress("UNCHECKED_CAST")
class NineGameViewModelFactoryClassic(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return NineGameViewModelClassic(application) as T
    }
}