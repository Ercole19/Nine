package com.simonercole.nine.ui.model

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.simonercole.nine.ui.model.NineGameUtils.Companion.getTimerLabel
import com.simonercole.nine.ui.model.NineGameUtils.Companion.parseIt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import kotlin.properties.Delegates

class NineGameViewModel(application: Application): AndroidViewModel(application) {
    lateinit var sequenceToGuess: CharArray
    val userGuesses = MutableLiveData(mutableListOf(HashMap<Int, Pair<String, Char>>())) //This map saves user past guesses that will be displayed to him
    var liveInput = MutableLiveData(CharArray(9) { ' ' }) //Input user create by clicking the tiles he has in his keyboard
    var focusArray = MutableLiveData(IntArray(9) { 1 * it }) //It is used to understand in which tile the "focus" is, the focus is where the next symbol is gonna be put
    private var distanceArray = IntArray(9) { -1 }
    var maxAttempts by Delegates.notNull<Int>()
    var currentAttempts = MutableLiveData(0)
    var sequenceStatus = MutableLiveData(IntArray(9) { 0 }) // This array traces which symbols of the sequence are placed correctly by user
    var guessedChars = CharArray(9) { ' ' }
    var startingKeyboard = CharArray(9) { ' ' } //KeyBoard given to user when started the game
    val timerValue = MutableLiveData(mutableIntStateOf(0)) // Value of the countDown timer, so it will change constantly until 0 and be on screen during all the game
    private var bestTime = "" //The game before starting a game saves user best time in the specific difficulty chosen, so that will be able to understand if user made a new record
    val userGameTime = MutableLiveData("")  //How many time user took to finish his game
    val newBestTime = MutableLiveData(false) //If user make a record, so a new best time we save it here and we update the DB with this new best time
    var gameStatus = MutableLiveData(GameStatus.NotStarted)
    private var totalTime by Delegates.notNull<Int>()
    lateinit var difficulty: String //needed to set up a game
    var endRequestFromUser = NineGameUtils.EndRequest.None
    private var job: Job? = null
    private lateinit var gameModel : GameModel


    fun setUpGame(diff: String) {
        gameModel = GameModel(application = getApplication())
        viewModelScope.launch {
            bestTime = gameModel.getUserBestTime(diff)
        }
        difficulty = diff

        timerValue.value!!.intValue = when (diff) {
            "Easy" -> 100
            "Medium" -> 80
            else -> 50
        }
        totalTime = timerValue.value!!.intValue
        maxAttempts = when (diff) {
            "Easy" -> 4
            "Medium" -> 4
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
        if (timerValue.value!!.intValue == 0) timerValue.value!!.intValue = 0
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Main) {
                delay(timeMillis = 200)
                while (isActive) {
                    if (timerValue.value!!.intValue <= 0) {
                        job?.cancel()
                        return@withContext
                    }
                    delay(timeMillis = 1000)
                    timerValue.value!!.intValue = timerValue.value!!.intValue - 1
                }
            }

        }
    }

    private fun checkGameStatus() {
        userGameTime.value = getTimerLabel(totalTime - timerValue.value!!.intValue)
        if (currentAttempts.value == maxAttempts) {
            if (sequenceStatus.value!!.contains(0).not()) {
                pauseTimer()
                if (parseIt(bestTime) > parseIt(userGameTime.value!!)) {
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
                if (parseIt(bestTime) > parseIt(userGameTime.value!!)) {
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
            viewModelScope.launch {
                gameModel.saveToDB(game)
            }
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
            if (currentAttempts.value == 1 && difficulty == "Hard") {
                if (i % 2 != 0 || sequenceStatus.value!![i] == 1) {
                    map[i] = Pair(distanceArray[i].toString(), liveInput.value!![i])
                } else {
                    map[i] = Pair("?", liveInput.value!![i]) //Hard mode 1st try blurs odd distances by replace them with a '?'
                }
            } else if (currentAttempts.value == 2 && difficulty == "Hard") {
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
        userGameTime.value = getTimerLabel(totalTime - timerValue.value!!.intValue)
    }
}

@Suppress("UNCHECKED_CAST")
class NineGameViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return NineGameViewModel(application) as T
    }
}