package com.simonercole.nine.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.simonercole.nine.db.GameEntity
import com.simonercole.nine.db.GameDB
import com.simonercole.nine.db.GameRepository
import com.simonercole.nine.model.PlayedGameContainer
import com.simonercole.nine.model.PlayedGame
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameResult
import com.simonercole.nine.utils.Routes

/**
 * ViewModel responsible for managing the played games screen of the application.
 * @param application The application context.
 */
class PlayedGamesViewModel(application: Application): AndroidViewModel(application) {

    // Instance of PlayedGameContainer to manage played games
    private var playedGameContainer = PlayedGameContainer(this)

    // LiveData for the list of played games
    private var playedGamesList : MutableLiveData<SnapshotStateList<PlayedGame>> = MutableLiveData(playedGameContainer.playedGames)
    val observableList : LiveData<SnapshotStateList<PlayedGame>> = playedGamesList

    // LiveData for the chosen difficulty filter
    private var chosenDifficulty : MutableLiveData<Difficulty> = MutableLiveData(playedGameContainer.filter.chosenDifficulty)
    val observableDifficulty : LiveData<Difficulty> = chosenDifficulty

    // LiveData for the chosen game result filter
    private var chosenGameResult : MutableLiveData<GameResult> = MutableLiveData(playedGameContainer.filter.gameResult)
    val observableGameResult : LiveData<GameResult> = chosenGameResult

    // LiveData for indicating if the sorting by best time is chosen
    private var sortByBestTimeIsChosen : MutableLiveData<Boolean> = MutableLiveData(playedGameContainer.filter.showBestTimes)
    val observableSortByBestTimeIsChosen : LiveData<Boolean>  = sortByBestTimeIsChosen

    /**
     * Retrieves the repository for game data.
     * @return GameRepository instance.
     */
    fun getRepo(): GameRepository {
        return GameRepository(GameDB.getInstance(getApplication() as Context).getDAO())
    }

    /**
     * Sets the chosen difficulty filter.
     * @param difficulty The selected difficulty.
     */
    fun setChosenDiff(difficulty: Difficulty) {
        playedGameContainer.setChosenDiff(difficulty)
        hardStateChange()
    }

    /**
     * Sets the chosen game result filter.
     * @param filters The selected game result filter.
     */
    fun setChosenGameResult(filters: GameResult) {
        playedGameContainer.setChosenGameResult(filters)
        hardStateChange()
    }

    /**
     * Filters the list of played games by best time.
     */
    fun filterByBestTime() {
        playedGameContainer.filterByBestTime()
        hardStateChange()
    }

    /**
     * Removes a game from the list of played games.
     * @param gameEntity The game entity to be removed.
     */
    fun removeGame(gameEntity: GameEntity) {
        playedGameContainer.removeGame(gameEntity)
        hardStateChange()
    }

    /**
     * Applies the chosen filters to the list of played games.
     */
    fun applyFiltersToList() {
        playedGameContainer.applyFiltersToList()
        hardStateChange()
    }

    /**
     * Navigates to the main screen.
     * @param navHostController The navigation controller.
     */
    fun navigateToMainScreen(navHostController: NavHostController) {
        navHostController.navigate(Routes.NINE_START)
    }

    /**
     * Performs a hard state change by updating LiveData values.
     */
    private fun hardStateChange() {
        playedGamesList.value  = playedGameContainer.playedGames
        chosenDifficulty.value = playedGameContainer.filter.chosenDifficulty
        chosenGameResult.value = playedGameContainer.filter.gameResult
        sortByBestTimeIsChosen.value = playedGameContainer.filter.showBestTimes
    }
}

/**
 * ViewModel Factory for creating PlayedGamesViewModel instances.
 * @param application The application context.
 */
@Suppress("UNCHECKED_CAST")
class DoneGamesFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return PlayedGamesViewModel(application) as T
    }
}
