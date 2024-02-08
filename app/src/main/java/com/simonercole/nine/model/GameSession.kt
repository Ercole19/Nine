package com.simonercole.nine.model

import android.content.Context
import com.simonercole.nine.R
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.EndRequest
import com.simonercole.nine.utils.GameStatus
import com.simonercole.nine.viewmodel.NineGameViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameSession(private val viewModel: NineGameViewModel) {
    var newBestTime : Boolean = false
    private var job: Job? = null
    var game : OnGoingGame = OnGoingGame(viewModel.getRepo())
    var userInput : UserGameInput = UserGameInput()
    var endRequestFromUser  : EndRequest =  EndRequest.None
    var sequenceToGuess: CharArray = CharArray(9) {' '}


    fun setUpGame(diff: Difficulty) {
        game.setGameInfo(diff)
        sequenceToGuess = createSequenceToGuess()
        sequenceToGuess.shuffle()
        userInput.setKeyboard(sequenceToGuess)
        userInput.initInput()
        game.gameStatus = GameStatus.FirstGuess
        userInput.allGuesses.removeAt(0)
    }

    fun makeGuess() {
        if (game.gameStatus == GameStatus.FirstGuess) {
            startTimer()
            game.gameStatus = GameStatus.OnGoing
        }
        userInput.calculateDistance(sequenceToGuess)
        game.attempts++
        userInput.updateUserGuesses(game.attempts, game.difficulty)
        userInput.clearInput()
        userInput.updateFocusByWrite(0)
        checkGameStatus()
    }

    fun updateFocusByTouch(index : Int) { userInput.updateFocusByTouch(index) }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startTimer() {
        if (game.timerValue.value == 0) game.timerValue.value = 0
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Main) {
                delay(timeMillis = 200)
                while (isActive) {
                    if (game.timerValue.value <= 0) {
                        job?.cancel()
                        timerExpired()
                        return@withContext
                    }
                    delay(timeMillis = 1000)
                    game.timerValue.value = game.timerValue.value - 1
                }
            }

        }
    }

    fun updateInput(index: Int, char: Char) {userInput.updateInput(index, char)}
    fun deleteChar(index :Int) { userInput.clearGameTile(index) }

    private fun checkGameStatus() {
        game.userGameTime = getTimerLabel(game.totalTime - game.timerValue.value)
        if (game.attempts == game.maxAttempts) {
            if (userInput.isInputAllCorrect()) {
                pauseTimer()
                if (game.parseTimerValueToIntValue(game.bestTime!!) > game.parseTimerValueToIntValue(game.userGameTime)) {
                    game.bestTime = game.userGameTime
                    newBestTime = true
                }
                game.gameStatus = GameStatus.Won
                game.saveGameToDB()
            } else {
                pauseTimer()
                game.gameStatus = GameStatus.Lost
                game.saveGameToDB()
            }
        } else {
            if (userInput.isInputAllCorrect()) {
                pauseTimer()
                if (game.parseTimerValueToIntValue(game.bestTime!!) > game.parseTimerValueToIntValue(game.userGameTime)) {
                    game.bestTime = game.userGameTime
                    newBestTime = true
                }
                game.gameStatus = GameStatus.Won
                game.saveGameToDB()
            }
        }
    }

    private fun pauseTimer() {
        job?.cancel()
    }
    private fun createSequenceToGuess(): CharArray {
        val charArray = CharArray(9)
        val context: Context? = viewModel.getContext()
        val inputStream = context?.resources?.openRawResource(R.raw.symbols)?.bufferedReader()?.readLines()
        val selectedSymbols = inputStream!!.shuffled().take(9)
        selectedSymbols.forEachIndexed { index, s ->
            charArray[index] = s.substring(2).toInt(16).toChar()
        }
        return charArray
    }

    private fun timerExpired() {
        game.gameStatus = GameStatus.Lost
        game.userGameTime = getTimerLabel(game.totalTime - game.timerValue.value)
        game.saveGameToDB()
        viewModel.timerExpired()
    }

    fun resetGame() {
        game.resetGameStatus()
    }

    fun userChangeActivityMidGame() {
        pauseTimer()
        game.pauseGameStatus()
    }

    fun quitRequestFromUser() {
        pauseTimer()
        endRequestFromUser = EndRequest.Quit
        game.pauseGameStatus()
    }
    fun refreshRequestFromUser() {
        pauseTimer()
        endRequestFromUser = EndRequest.Refresh
        game.pauseGameStatus()
    }

    fun resumeGame() {
        endRequestFromUser = EndRequest.None
        game.resumeGameStatus()
        startTimer()
    }
    fun handleQuitGame() {
        game.userGameTime = getTimerLabel(game.totalTime - game.timerValue.value)
        game.saveGameToDB()
    }


    private fun padding(value: Int) = if (value < 10) ("0$value") else "" + value

    fun getTimerLabel(value: Int): String {
        return "${padding(value / 60)} : ${padding(value % 60)}"
    }

    fun isInputFull() : Boolean {
        return userInput.isInputFull()
    }

    fun getCurrentFocus() : Int {
         return userInput.getCurrentFocusIndex()
    }
}