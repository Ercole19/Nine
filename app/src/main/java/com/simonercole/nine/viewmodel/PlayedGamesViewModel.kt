package com.simonercole.nine.viewmodel

import android.app.Application
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

class PlayedGamesViewModel(application: Application): AndroidViewModel(application) {
    private var playedGameContainer = PlayedGameContainer(GameRepository(GameDB.getInstance(application).getDAO()))

    private var playedGamesList : MutableLiveData<SnapshotStateList<PlayedGame>> = MutableLiveData(playedGameContainer.playedGames)
    val observableList : LiveData<SnapshotStateList<PlayedGame>> = playedGamesList

    private var chosenDifficulty : MutableLiveData<Difficulty> = MutableLiveData(playedGameContainer.filter.chosenDifficulty)
    val observableDifficulty : LiveData<Difficulty> = chosenDifficulty

    private var chosenGameResult : MutableLiveData<GameResult> = MutableLiveData(playedGameContainer.filter.gameResult)
    val observableGameResult : LiveData<GameResult> = chosenGameResult

    private var sortByBestTimeIsChosen : MutableLiveData<Boolean> = MutableLiveData(playedGameContainer.filter.showBestTimes)
    val observableSortByBestTimeIsChosen : LiveData<Boolean>  = sortByBestTimeIsChosen

    fun setChosenDiff(difficulty: Difficulty) {
        playedGameContainer.setChosenDiff(difficulty)
        hardStateChange()
    }

    fun setChosenGameResult(filters: GameResult) {
        playedGameContainer.setChosenGameResult(filters)
        hardStateChange()
    }

    fun filterByBestTime() {
        playedGameContainer.filterByBestTime()
        hardStateChange()
    }
    fun removeGame(gameEntity: GameEntity) {
        playedGameContainer.removeGame(gameEntity)
        hardStateChange()
    }

    fun applyFiltersToList() {
        playedGameContainer.applyFiltersToList()
        hardStateChange()
    }

    fun navigateToMainScreen(navHostController: NavHostController) {
        navHostController.navigate(Routes.NINE_START)
    }
    private fun hardStateChange() {
        playedGamesList.value  = playedGameContainer.playedGames
        chosenDifficulty.value = playedGameContainer.filter.chosenDifficulty
        chosenGameResult.value = playedGameContainer.filter.gameResult
        sortByBestTimeIsChosen.value = playedGameContainer.filter.showBestTimes
    }
}

@Suppress("UNCHECKED_CAST")
class DoneGamesFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return PlayedGamesViewModel(application) as T
    }
}