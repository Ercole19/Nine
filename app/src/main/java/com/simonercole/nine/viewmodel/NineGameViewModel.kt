package com.simonercole.nine.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simonercole.nine.db.Game
import com.simonercole.nine.db.GameDB
import com.simonercole.nine.db.GameRepository
import com.simonercole.nine.utils.NineGameUtils
import com.simonercole.nine.utils.NineGameUtils.Companion.getTimerLabel
import com.simonercole.nine.utils.NineGameUtils.Companion.parseIt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import kotlin.properties.Delegates
import com.simonercole.nine.utils.NineGameUtils.GameStatus
import kotlinx.coroutines.DelicateCoroutinesApi
import com.simonercole.nine.utils.NineGameUtils.Difficulty

class NineGameViewModel(application: Application): AndroidViewModel(application) {

    lateinit var sequenceToGuess: CharArray
    val userGuesses : MutableLiveData<MutableList<HashMap<Int, Pair<String, Char>>>>
    var liveInput : MutableLiveData<CharArray>
    var focusArray : MutableLiveData<IntArray>
    private var distanceArray : IntArray
    var maxAttempts by Delegates.notNull<Int>()

    var currentAttempts :MutableLiveData<Int>
    var sequenceStatus : MutableLiveData<IntArray>
    var guessedChars : CharArray
    var startingKeyboard : CharArray

    val timerValue : MutableLiveData<MutableState<Int>>
    private var bestTime : String? = null
    val userGameTime : MutableLiveData<String>
    val newBestTime : MutableLiveData<Boolean>
    var gameStatus : MutableLiveData<GameStatus>

    private var totalTime by Delegates.notNull<Int>()
    lateinit var difficulty: Difficulty
    var errorFromDB : MutableLiveData<Boolean>

    var endRequestFromUser  : NineGameUtils.EndRequest
    private var job: Job? = null
    private val gameRepository : GameRepository

    init {
        val gameDB = GameDB.getInstance(application).getDAO()
        gameRepository = GameRepository(gameDB)
        timerValue = MutableLiveData(mutableIntStateOf(0))
        userGuesses = MutableLiveData(mutableListOf(HashMap()))
        liveInput = MutableLiveData(CharArray(9) { ' ' })
        focusArray = MutableLiveData(IntArray(9) { 1 * it })
        distanceArray = IntArray(9) { -1 }
        currentAttempts = MutableLiveData(0)
        sequenceStatus =  MutableLiveData(IntArray(9) { 0 })
        guessedChars = CharArray(9) { ' ' }
        startingKeyboard = CharArray(9) { ' ' }
        userGameTime = MutableLiveData("")
        newBestTime = MutableLiveData(false)
        gameStatus = MutableLiveData(GameStatus.NotStarted)
        endRequestFromUser = NineGameUtils.EndRequest.None
        errorFromDB = MutableLiveData(false)
    }

    fun setUpGame(diff: Difficulty) {
        bestTime = gameRepository.getUserBestTime(diff)
        if (bestTime == null) {errorFromDB.value = errorFromDB.value!!.not()}
        difficulty = diff

        timerValue.value!!.value = when (diff) {
            Difficulty.Easy -> 100
            Difficulty.Medium -> 80
            else -> 50
        }
        totalTime = timerValue.value!!.value
        maxAttempts = when (diff) {
            Difficulty.Easy -> 4
            Difficulty.Medium -> 4
            else -> 3
        }
        sequenceToGuess = createSequenceToGuess()
        sequenceToGuess.shuffle()
        createKeyboard()
        focusArray.value!![0] = 0
        gameStatus.value = GameStatus.FirstGuess
        userGuesses.value!!.removeAt(0)
    }

    fun makeGuess() {
        if (gameStatus.value == GameStatus.FirstGuess) {
            startTimer()
            gameStatus.value = GameStatus.OnGoing
        }
        calculateDistance()
        currentAttempts.value = currentAttempts.value!! + 1
        updateUserGuesses()
        clearInput()
        updateFocusByWrite(0)
        checkGameStatus()
    }

    private fun startTimer() {
        if (timerValue.value!!.value == 0) timerValue.value!!.value = 0
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Main) {
                delay(timeMillis = 200)
                while (isActive) {
                    if (timerValue.value!!.value <= 0) {
                        job?.cancel()
                        timerExpired()
                        return@withContext
                    }
                    delay(timeMillis = 1000)
                    timerValue.value!!.value = timerValue.value!!.value - 1
                }
            }

        }
    }
    private fun timerExpired() {
        gameStatus.value = GameStatus.Lost
        getTime()
        saveGameToDB()
    }

    private fun checkGameStatus() {
        userGameTime.value = getTimerLabel(totalTime - timerValue.value!!.value)
        if (currentAttempts.value == maxAttempts) {
            if (sequenceStatus.value!!.contains(0).not()) {
                pauseTimer()
                if (parseIt(bestTime!!) > parseIt(userGameTime.value!!)) {
                    bestTime = userGameTime.value!!
                    newBestTime.value = true
                }
                gameStatus.value = GameStatus.Won
                saveGameToDB()
            } else {
                pauseTimer()
                gameStatus.value = GameStatus.Lost
                saveGameToDB()
            }
        } else {
            if (sequenceStatus.value!!.contains(0).not()) {
                pauseTimer()
                if (parseIt(bestTime!!) > parseIt(userGameTime.value!!)) {
                    bestTime = userGameTime.value!!
                    newBestTime.value = true
                }
                gameStatus.value = GameStatus.Won
                saveGameToDB()
            }
        }
    }

    fun saveGameToDB() {
        val game = currentAttempts.value?.let {
            Game(
                difficulty = difficulty,
                attempts = it,
                time = userGameTime.value!!,
                dateTime = LocalDateTime.now().toString(),
                win = gameStatus.value == GameStatus.Won,
            )
        }
        if (game != null) {
           gameRepository.saveToDB(game)
        }
    }

    private fun createKeyboard() {
        for ((i) in sequenceToGuess.withIndex()) {
            startingKeyboard[i] = sequenceToGuess[i]
        }
        startingKeyboard.shuffle()
    }

    private fun createSequenceToGuess(): CharArray {
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
            for (idx in focusIndex..8) {
                if (sequenceStatus.value!![idx] == 0) {
                    updateFocusByWrite(idx)
                    break
                }

            }
        }
    }

    //These fun updates the user past guesses that will be displayed on a lazy column in the UI, the guess comprehend user input and distances
    private fun updateUserGuesses() {
        val map = HashMap<Int, Pair<String, Char>>()
        val newList = mutableListOf(HashMap<Int, Pair<String, Char>>())
        newList.removeAt(0)

        userGuesses.value!!.forEach { hashMap ->
            newList.add(hashMap)
        }

        for (i in 0..8) {
            if (currentAttempts.value == 1 && difficulty == Difficulty.Hard) {
                if (i % 2 != 0 || sequenceStatus.value!![i] == 1) {
                    map[i] = Pair(distanceArray[i].toString(), liveInput.value!![i])
                } else {
                    map[i] = Pair("?", liveInput.value!![i]) //Hard mode 1st try blurs odd distances by replace them with a '?'
                }
            } else if (currentAttempts.value == 2 && difficulty == Difficulty.Hard) {
                if (i % 2 == 0 || sequenceStatus.value!![i] == 1) {
                    map[i] = Pair(distanceArray[i].toString(), liveInput.value!![i])
                } else {
                    map[i] = Pair("?", liveInput.value!![i]) //Hard mode 2nd try blurs even distances
                }
            } else {
                map[i] = Pair(distanceArray[i].toString(), liveInput.value!![i])
            }

        }
        newList.add(map)
        userGuesses.value = newList
    }
    //After the user make a guess, all the char placed incorrectly will be placed in the starting keyboard. The correct ones will permanently remain on the input tiles
    private fun clearInput() {
        val newInput = CharArray(9) { ' ' }
        for (i in 0..8) {
            if (sequenceStatus.value?.get(i) == 1) newInput[i] = liveInput.value!![i]
            else newInput[i] = ' '
        }
        liveInput.value = newInput
    }

    private fun calculateDistance() {
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
    //A user can touch a tile in which there is a char and delete it in order to change the char
    fun deleteChar(index: Int) {
        val newInput = CharArray(9) { ' ' }
        liveInput.value?.forEachIndexed { i, _ ->
            if (index == i) newInput[i] = ' '
            else newInput[i] = liveInput.value!![i]
        }
        liveInput.value = newInput
    }

    fun updateFocusByTouch(index: Int) {
        val newInput = IntArray(9) { 1 }
        val newIndex: Int = index
        if (sequenceStatus.value!![index] == 0) {
            newInput[newIndex] = 0
            focusArray.value = newInput
        } else {
            updateFocusByWrite(index)
        }

    }

    private fun updateFocusByWrite(index: Int) {
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

    fun resetGame() {
        gameStatus.value = GameStatus.NotStarted
    }

    fun userChangeActivityMidGame() {
        pauseTimer()
        gameStatus.value = GameStatus.Paused
    }

    fun quitRequest() {
        pauseTimer()
        endRequestFromUser = NineGameUtils.EndRequest.Quit
        gameStatus.value = GameStatus.Paused
    }
    fun refreshRequest() {
        pauseTimer()
        endRequestFromUser = NineGameUtils.EndRequest.Refresh
        gameStatus.value = GameStatus.Paused
    }

    private fun pauseTimer() {
        job?.cancel()
    }
    fun resumeGame() {
        endRequestFromUser = NineGameUtils.EndRequest.None
        gameStatus.value = GameStatus.OnGoing
        startTimer()
    }

    fun getTime() {
        userGameTime.value = getTimerLabel(totalTime - timerValue.value!!.value)
    }
}

@Suppress("UNCHECKED_CAST")
class NineGameViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return NineGameViewModel(application) as T
    }
}