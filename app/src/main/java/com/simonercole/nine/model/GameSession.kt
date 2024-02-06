package com.simonercole.nine.model

import com.simonercole.nine.db.GameRepository
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.EndRequest
import com.simonercole.nine.utils.GameStatus
import com.simonercole.nine.utils.NineGameUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameSession(gameRepository: GameRepository) {
    var newBestTime : Boolean = false
    private var job: Job? = null
    var game : OnGoingGame = OnGoingGame(gameRepository)
    var userInput : UserInput = UserInput()
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
    fun deleteChar(index :Int) { userInput.deleteChar(index) }

    private fun checkGameStatus() {
        game.userGameTime = NineGameUtils.getTimerLabel(game.totalTime - game.timerValue.value)
        if (game.attempts == game.maxAttempts) {
            if (userInput.isInputAllCorrect()) {
                pauseTimer()
                if (NineGameUtils.parseIt(game.bestTime!!) > NineGameUtils.parseIt(game.userGameTime)) {
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
                if (NineGameUtils.parseIt(game.bestTime!!) > NineGameUtils.parseIt(game.userGameTime)) {
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
        return NineGameUtils.symbols.toList().shuffled().take(9).joinToString("").toCharArray()
    }

    private fun timerExpired() {
        game.gameStatus = GameStatus.Lost
        game.userGameTime = NineGameUtils.getTimerLabel(game.totalTime - game.timerValue.value)
        game.saveGameToDB()
    }

    fun resetGame() {
        game.resetGame()
    }

    fun userChangeActivityMidGame() {
        pauseTimer()
        game.userChangeActivityMidGame()
    }

    fun quitRequest() {
        pauseTimer()
        endRequestFromUser = EndRequest.Quit
        game.quitRequest()
    }
    fun refreshRequest() {
        pauseTimer()
        endRequestFromUser = EndRequest.Refresh
        game.refreshRequest()
    }

    fun resumeGame() {
        endRequestFromUser = EndRequest.None
        game.resumeGame()
        startTimer()
    }
    fun handleQuitGame() {
        game.userGameTime = NineGameUtils.getTimerLabel(game.totalTime - game.timerValue.value)
        game.saveGameToDB()
    }

    fun isInputFull() : Boolean {
        return userInput.isInputFull()
    }

    fun getCurrentFocus() : Int {
         return userInput.getCurrentFocusIndex()
    }
}