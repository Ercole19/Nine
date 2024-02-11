package com.simonercole.nine.model

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.simonercole.nine.R
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.EndRequest
import com.simonercole.nine.utils.GameStatus
import com.simonercole.nine.viewmodel.NineGameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Represents a game session, managing game mechanics and interactions.
 * @param viewModel The view model associated with the game.
 */
class GameSession(private val viewModel: NineGameViewModel) {
    // Indicates whether a new best time has been achieved.
    var newBestTime: Boolean = false
    // The job associated with the timer.
    private var job: Job? = null
    // The ongoing game instance.
    var game: OnGoingGame = OnGoingGame(viewModel.getRepo())
    // The user's game input.
    var userInput: UserGameInput = UserGameInput()
    // The request to end the game from the user.
    var endRequestFromUser: EndRequest = EndRequest.None
    // The sequence of characters to be guessed.
    var sequenceToGuess: CharArray = CharArray(9) { ' ' }

    /**
     * Sets up the game with the specified difficulty.
     * @param diff The difficulty level of the game.
     */
    fun setUpGame(diff: Difficulty) {
        // Initializes game information.
        game.setGameInfo(diff)
        // Creates and shuffles the sequence to guess.
        sequenceToGuess = createSequenceToGuess()
        sequenceToGuess.shuffle()
        // Sets up user input and game status.
        userInput.setKeyboard(sequenceToGuess)
        userInput.initInput()
        game.gameStatus = GameStatus.FirstGuess
        userInput.allGuesses.removeAt(0)
    }

    /**
     * Processes a user's guess.
     */
    fun makeGuess() {
        if (game.gameStatus == GameStatus.FirstGuess) {
            // Starts the timer on the first guess.
            startTimer()
            game.gameStatus = GameStatus.OnGoing
        }
        // Calculates distance, updates attempts and user guesses, and checks game status.
        userInput.calculateDistance(sequenceToGuess)
        game.attempts++
        userInput.updateUserGuesses(game.attempts, game.difficulty)
        userInput.clearInput()
        userInput.updateFocusByWrite(0)
        checkGameStatus()
    }

    /**
     * Updates the focus based on user touch.
     * @param index The index of the user's touch.
     */
    fun updateFocusByTouch(index: Int) {
        userInput.updateFocusByTouch(index)
    }

    /**
     * Starts the game timer.
     */
    private fun startTimer() {
        if (game.timerValue.value == 0) game.timerValue.value = 0
        job?.cancel()
        job = viewModel.viewModelScope.launch(Dispatchers.Main) {
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

    /**
     * Updates the user's input at a specified index.
     * @param index The index where the input should be updated.
     * @param char The character to be updated.
     */
    fun updateInput(index: Int, char: Char) {
        userInput.updateInput(index, char)
    }

    /**
     * Deletes a character at the specified index.
     * @param index The index of the character to be deleted.
     */
    fun deleteChar(index: Int) {
        userInput.clearGameTile(index)
    }

    /**
     * Checks the current status of the game.
     */
    private fun checkGameStatus() {
        // Updates user game time and checks for game completion.
        game.userGameTime = getTimerLabel(game.totalTime - game.timerValue.value)
        if (game.attempts == game.maxAttempts) {
            if (userInput.isInputAllCorrect()) {
                // Handles winning scenario.
                pauseTimer()
                if (game.parseTimerValueToIntValue(game.bestTime!!) > game.parseTimerValueToIntValue(game.userGameTime)) {
                    game.bestTime = game.userGameTime
                    newBestTime = true
                }
                game.gameStatus = GameStatus.Won
                game.saveGameToDB()
            } else {
                // Handles losing scenario.
                pauseTimer()
                game.gameStatus = GameStatus.Lost
                game.saveGameToDB()
            }
        } else {
            // Handles game still in progress.
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

    /**
     * Pauses the game timer.
     */
    private fun pauseTimer() {
        job?.cancel()
    }

    /**
     * Creates the sequence of characters to be guessed.
     * @return The array of characters representing the sequence.
     */
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

    /**
     * Handles the scenario when the timer expires.
     */
    private fun timerExpired() {
        game.gameStatus = GameStatus.Lost
        game.userGameTime = getTimerLabel(game.totalTime - game.timerValue.value)
        game.saveGameToDB()
        viewModel.timerExpired()
    }

    /**
     * Resets the game status.
     */
    fun resetGame() {
        game.resetGameStatus()
    }

    /**
     * Pauses the game when the user changes activity mid-game.
     */
    fun userChangeActivityMidGame() {
        pauseTimer()
        game.pauseGameStatus()
    }

    /**
     * Initiates a quit request from the user.
     */
    fun quitRequestFromUser() {
        pauseTimer()
        endRequestFromUser = EndRequest.Quit
        game.pauseGameStatus()
    }

    /**
     * Initiates a refresh request from the user.
     */
    fun refreshRequestFromUser() {
        pauseTimer()
        endRequestFromUser = EndRequest.Refresh
        game.pauseGameStatus()
    }

    /**
     * Resumes the game after a user-initiated action.
     */
    fun resumeGame() {
        endRequestFromUser = EndRequest.None
        game.resumeGameStatus()
        startTimer()
    }

    /**
     * Handles the scenario when the user quits the game.
     */
    fun handleQuitGame() {
        game.userGameTime = getTimerLabel(game.totalTime - game.timerValue.value)
        game.saveGameToDB()
    }

    /**
     * Pads the value with zero if it's less than 10.
     * @param value The value to be padded.
     * @return The padded string representation of the value.
     */
    private fun padding(value: Int) = if (value < 10) ("0$value") else "" + value

    /**
     * Formats the timer value into a human-readable string.
     * @param value The timer value in seconds.
     * @return The formatted time string (MM:SS).
     */
    fun getTimerLabel(value: Int): String {
        return "${padding(value / 60)} : ${padding(value % 60)}"
    }

    /**
     * Checks if the user input is full.
     * @return True if user input is full, false otherwise.
     */
    fun isInputFull(): Boolean {
        return userInput.isInputFull()
    }

    /**
     * Gets the index of the current focus.
     * @return The index of the current focus.
     */
    fun getCurrentFocus(): Int {
        return userInput.getCurrentFocusIndex()
    }
}
