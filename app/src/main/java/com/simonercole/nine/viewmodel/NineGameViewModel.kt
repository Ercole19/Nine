package com.simonercole.nine.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.simonercole.nine.db.GameDB
import com.simonercole.nine.db.GameRepository
import com.simonercole.nine.model.GameSession
import com.simonercole.nine.model.GameTile
import com.simonercole.nine.model.KeyboardTile
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.EndRequest
import com.simonercole.nine.utils.GameStatus
import com.simonercole.nine.utils.Routes

/**
 * ViewModel responsible for managing the game session and updating UI states accordingly.
 * @param application The application context.
 */
class NineGameViewModel(application: Application): AndroidViewModel(application) {
    private var gameSession = GameSession(this)

    // LiveData for user guesses
    private var userGuessesLiveData : MutableLiveData<SnapshotStateList<HashMap<Int, Pair<String, Char>>>> = MutableLiveData(gameSession.userInput.allGuesses)
    val observableUserGuesses : LiveData<SnapshotStateList<HashMap<Int, Pair<String, Char>>>> = userGuessesLiveData

    // LiveData for current input
    private var currentInput : MutableLiveData<SnapshotStateList<GameTile>> = MutableLiveData(gameSession.userInput.currentInput)
    val observableCurrentInput : LiveData<SnapshotStateList<GameTile>> = currentInput

    // LiveData for current keyboard
    private var currentKeyboard : MutableLiveData<SnapshotStateList<KeyboardTile>> = MutableLiveData(gameSession.userInput.gameKeyboard)
    val observableCurrentKeyBoard : LiveData<SnapshotStateList<KeyboardTile>> = currentKeyboard

    // LiveData for current attempts
    private var currentAttemptsLiveData :MutableLiveData<Int> = MutableLiveData(gameSession.game.attempts)
    val observableAttempts : LiveData<Int> = currentAttemptsLiveData

    // LiveData for timer value
    private var timerValueLiveData : MutableLiveData<MutableState<Int>> = MutableLiveData(gameSession.game.timerValue)
    val observableTimerValue : LiveData<MutableState<Int>> = timerValueLiveData

    // LiveData for new best time
    private var newBestTimeLiveData : MutableLiveData<Boolean> = MutableLiveData(gameSession.newBestTime)
    val observableNewBestTime : LiveData<Boolean> = newBestTimeLiveData

    // LiveData for game status
    private var gameStatusLiveData : MutableLiveData<GameStatus> = MutableLiveData(gameSession.game.gameStatus)
    val observableGameStatus : LiveData<GameStatus> = gameStatusLiveData

    /**
     * Retrieves the game repository.
     * @return The game repository.
     */
    fun getRepo() : GameRepository {
        return GameRepository(GameDB.getInstance(getApplication() as Context).getDAO())
    }

    /**
     * Sets up the game with the specified difficulty.
     * @param diff The difficulty level.
     */
    fun setUpGame(diff: Difficulty) {
        gameSession.setUpGame(diff)
        hardUpdateStates(gameSession)
    }

    /**
     * Handles user guess.
     */
    fun makeGuess() {
        gameSession.makeGuess()
        hardUpdateStates(gameSession)
    }

    /**
     * Updates input at the specified index with the provided character.
     * @param index The index of input to update.
     * @param char The character to update.
     */
    fun updateInput(index: Int, char: Char) {
        gameSession.updateInput(index, char)
        hardUpdateStates(gameSession)
    }

    /**
     * Deletes the character at the specified index.
     * @param index The index of the character to delete.
     */
    fun deleteChar(index: Int) {
        gameSession.deleteChar(index)
        hardUpdateStates(gameSession)
    }

    /**
     * Updates focus based on user touch at the specified index.
     * @param index The index touched by the user.
     */
    fun updateFocusByTouch(index: Int) {
        gameSession.updateFocusByTouch(index)
        hardUpdateStates(gameSession)
    }

    /**
     * Resets the game.
     */
    fun resetGame() {
        gameSession.resetGame()
        hardUpdateStates(gameSession)
    }

    /**
     * Handles user activity change mid-game.
     */
    fun userChangeActivityMidGame() {
        gameSession.userChangeActivityMidGame()
        hardUpdateStates(gameSession)
    }

    /**
     * Handles quit request.
     */
    fun quitRequest() {
        gameSession.quitRequestFromUser()
        hardUpdateStates(gameSession)
    }

    /**
     * Handles refresh request.
     */
    fun refreshRequest() {
        gameSession.refreshRequestFromUser()
        hardUpdateStates(gameSession)
    }

    /**
     * Resumes the game.
     */
    fun resumeGame() {
        gameSession.resumeGame()
        hardUpdateStates(gameSession)
    }

    /**
     * Retrieves the maximum attempts allowed for the game.
     * @return The maximum attempts allowed.
     */
    fun getMaxAttempts(): Int {
        return gameSession.game.maxAttempts
    }

    /**
     * Retrieves the difficulty level of the game.
     * @return The difficulty level.
     */
    fun getDifficulty() : Difficulty {
        return gameSession.game.difficulty
    }

    /**
     * Retrieves the sequence to guess in the game.
     * @return The sequence to guess.
     */
    fun getSequenceToGuess(): CharArray {
        return gameSession.sequenceToGuess
    }

    /**
     * Retrieves the end request from the user.
     * @return The end request from the user.
     */
    fun getEndRequest(): EndRequest {
        return gameSession.endRequestFromUser
    }

    /**
     * Handles quitting the game.
     */
    fun handleQuitGame() {
        gameSession.handleQuitGame()
    }

    /**
     * Retrieves the game status.
     * @return The game status.
     */
    fun getGameStatus(): GameStatus {
        return gameSession.game.gameStatus
    }

    /**
     * Retrieves the user game time.
     * @return The user game time.
     */
    fun getUserGameTime() : String {
        return gameSession.game.userGameTime
    }

    /**
     * Checks if the user input is full.
     * @return True if input is full, false otherwise.
     */
    fun isInputFull() : Boolean {
        return gameSession.isInputFull()
    }

    /**
     * Retrieves the index of the currently focused input.
     * @return The index of the currently focused input.
     */
    fun getCurrentFocus() : Int {
        return gameSession.getCurrentFocus()
    }

    /**
     * Navigates to the second screen.
     * @param navController The navigation controller.
     */
    fun navigateToSecondScreen(navController : NavHostController) {
        navController.navigate(Routes.SECOND_SCREEN + "/${getDifficulty()}")
    }

    /**
     * Navigates to the main menu.
     * @param navController The navigation controller.
     */
    fun navigateToMainMenu(navController: NavHostController) {
        navController.navigate(Routes.NINE_START)
    }

    /**
     * Gets the label for the timer value.
     * @param timerValue The timer value.
     * @return The label for the timer value.
     */
    fun getTimerLabel(timerValue : Int) : String {
        return gameSession.getTimerLabel(timerValue)
    }

    /**
     * Handles timer expiration.
     */
    fun timerExpired() {
        hardUpdateStates(gameSession)
    }

    /**
     * Retrieves the application context.
     * @return The application context.
     */
    fun getContext(): Context? {
        return getApplication<Application>().applicationContext
    }

    /**
     * Updates the LiveData states based on the game session.
     * @param session The game session.
     */
    private fun hardUpdateStates(session : GameSession) {
        userGuessesLiveData.value = session.userInput.allGuesses
        currentInput.value = session.userInput.currentInput
        currentKeyboard.value = session.userInput.gameKeyboard

        currentAttemptsLiveData.value = session.game.attempts
        timerValueLiveData.value  = session.game.timerValue
        newBestTimeLiveData.value  = session.newBestTime
        gameStatusLiveData.value  = session.game.gameStatus
    }
}

/**
 * ViewModel Factory for creating NineGameViewModel instances.
 * @param application The application context.
 */
@Suppress("UNCHECKED_CAST")
class NineGameViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return NineGameViewModel(application) as T
    }
}
