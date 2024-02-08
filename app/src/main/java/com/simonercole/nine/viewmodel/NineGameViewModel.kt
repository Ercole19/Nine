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

class NineGameViewModel(application: Application): AndroidViewModel(application) {
    private var gameSession = GameSession(this)

    private var userGuessesLiveData : MutableLiveData<SnapshotStateList<HashMap<Int, Pair<String, Char>>>> = MutableLiveData(gameSession.userInput.allGuesses)
    val observableUserGuesses : LiveData<SnapshotStateList<HashMap<Int, Pair<String, Char>>>> = userGuessesLiveData

    private var currentInput : MutableLiveData<SnapshotStateList<GameTile>> = MutableLiveData(gameSession.userInput.currentInput)
    val observableCurrentInput : LiveData<SnapshotStateList<GameTile>> = currentInput

    private var currentKeyboard : MutableLiveData<SnapshotStateList<KeyboardTile>> = MutableLiveData(gameSession.userInput.gameKeyboard)
    val observableCurrentKeyBoard : LiveData<SnapshotStateList<KeyboardTile>> = currentKeyboard

    private var currentAttemptsLiveData :MutableLiveData<Int> = MutableLiveData(gameSession.game.attempts)
    val observableAttempts : LiveData<Int> = currentAttemptsLiveData

    private var timerValueLiveData : MutableLiveData<MutableState<Int>> = MutableLiveData(gameSession.game.timerValue)
    val observableTimerValue : LiveData<MutableState<Int>> = timerValueLiveData

    private var newBestTimeLiveData : MutableLiveData<Boolean> = MutableLiveData(gameSession.newBestTime)
    val observableNewBestTime : LiveData<Boolean> = newBestTimeLiveData

    private var gameStatusLiveData : MutableLiveData<GameStatus> = MutableLiveData(gameSession.game.gameStatus)
    val observableGameStatus : LiveData<GameStatus> = gameStatusLiveData

    fun getRepo() : GameRepository {
        return GameRepository(GameDB.getInstance(getApplication() as Context).getDAO())
    }

    fun setUpGame(diff: Difficulty) {
        gameSession.setUpGame(diff)
        hardUpdateStates(gameSession)
    }

    fun makeGuess() {
        gameSession.makeGuess()
        hardUpdateStates(gameSession)
    }


    fun updateInput(index: Int, char: Char) {
        gameSession.updateInput(index, char)
        hardUpdateStates(gameSession)
    }

    fun deleteChar(index: Int) {
        gameSession.deleteChar(index)
        hardUpdateStates(gameSession)
    }

    fun updateFocusByTouch(index: Int) {
        gameSession.updateFocusByTouch(index)
        hardUpdateStates(gameSession)
    }

    fun resetGame() {
        gameSession.resetGame()
        hardUpdateStates(gameSession)
    }

    fun userChangeActivityMidGame() {
        gameSession.userChangeActivityMidGame()
        hardUpdateStates(gameSession)
    }

    fun quitRequest() {
        gameSession.quitRequestFromUser()
        hardUpdateStates(gameSession)
    }
    fun refreshRequest() {
        gameSession.refreshRequestFromUser()
        hardUpdateStates(gameSession)
    }

    fun resumeGame() {
        gameSession.resumeGame()
        hardUpdateStates(gameSession)

    }

    fun getMaxAttempts(): Int {
        return gameSession.game.maxAttempts
    }

    fun getDifficulty() : Difficulty {
        return gameSession.game.difficulty
    }

    fun getSequenceToGuess(): CharArray {
        return gameSession.sequenceToGuess
    }
    fun getEndRequest(): EndRequest {
        return gameSession.endRequestFromUser
    }

    fun handleQuitGame() {
        gameSession.handleQuitGame()
    }

    fun getGameStatus(): GameStatus {
        return gameSession.game.gameStatus
    }
    fun getUserGameTime() : String {
        return gameSession.game.userGameTime
    }

    fun isInputFull() : Boolean {
        return gameSession.isInputFull()
    }

    fun getCurrentFocus() : Int {
        return gameSession.getCurrentFocus()
    }

    fun navigateToSecondScreen(navController : NavHostController) {
        navController.navigate(Routes.SECOND_SCREEN + "/${getDifficulty()}")
    }

    fun navigateToMainMenu(navController: NavHostController) {
        navController.navigate(Routes.NINE_START)
    }

    fun getTimerLabel(timerValue : Int) : String {
        return gameSession.getTimerLabel(timerValue)
    }

    fun timerExpired() {
        hardUpdateStates(gameSession)
    }

    fun getContext(): Context? {
        return getApplication<Application>().applicationContext
    }


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

@Suppress("UNCHECKED_CAST")
class NineGameViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return NineGameViewModel(application) as T
    }
}