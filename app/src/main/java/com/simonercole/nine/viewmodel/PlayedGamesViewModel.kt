package com.simonercole.nine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simonercole.nine.db.Game
import com.simonercole.nine.db.GameDB
import com.simonercole.nine.db.GameRepository
import com.simonercole.nine.utils.NineGameUtils.Companion.parseIt
import com.simonercole.nine.utils.NineGameUtils.Difficulty
import com.simonercole.nine.utils.NineGameUtils.GameResult
import com.simonercole.nine.db.PlayedGame

class PlayedGamesViewModel(application: Application): AndroidViewModel(application) {
    var chosenDifficulty : MutableLiveData<Difficulty> = MutableLiveData(Difficulty.All)
    var showBestTimes : MutableLiveData<Boolean> = MutableLiveData(false)
    var gameResult : MutableLiveData<GameResult> = MutableLiveData(GameResult.ALL)
    var playedGames = MutableLiveData<MutableList<PlayedGame>>()
    private var totalPlayedGames : MutableList<PlayedGame>
    val errorFromDB : MutableLiveData<Boolean> = MutableLiveData(false)
    val changesMade = MutableLiveData(false)

    private val gameRepository : GameRepository


    init {
        val gameDB = GameDB.getInstance(application).getDAO()
        gameRepository = GameRepository(gameDB)
        playedGames.value = emptyList<PlayedGame>().toMutableList()
        var totalGames: List<Game> = emptyList()
        if (gameRepository.getAllGames() == null) errorFromDB.value = errorFromDB.value!!.not()
        else { totalGames = gameRepository.getAllGames()!!}
        totalGames.forEach{ game -> playedGames.value!!.add(PlayedGame(parseIt(game.time), true, true, false, game ) ) }
        totalPlayedGames = playedGames.value!!
    }

    fun setChosenDiff(difficulty: Difficulty) {
        chosenDifficulty.value = difficulty
    }

    fun setChosenGameResult(filters: GameResult) {
        gameResult.value = filters
    }

    fun filterByBestTime() {
        showBestTimes.value = showBestTimes.value!!.not()
    }
    fun changesMade(){
        changesMade.value = changesMade.value!!.not()
    }

    fun removeGame(game: Game) {
        playedGames.value = totalPlayedGames.toMutableList()
        playedGames.value!!.forEach { playedGame ->
            if (playedGame.game == game) {
                playedGame.deleted = true
            }
        }
        gameRepository.deleteGame(game)
        changesMade()
    }

    fun applyFiltersToList() {
        playedGames.value = totalPlayedGames.toMutableList()

        if (showBestTimes.value == true) {
            sortByBestTime()
            changesMade()
            return
        }
        applyDifficultyFilter(chosenDifficulty.value!!)
        applyResultFilter(gameResult.value!!)
        changesMade()
    }

    private fun sortByBestTime() {
        applyResultFilter(GameResult.ONLY_WIN)
        applyDifficultyFilter(chosenDifficulty.value!!)
        gameResult.value = GameResult.ALL
        playedGames.value!!.sortBy{it.timeValue}
    }

    private fun applyDifficultyFilter(difficulty: Difficulty) {
        playedGames.value!!.forEach { playedGame ->
            if (difficulty != Difficulty.All) {
                playedGame.showElementByDifficulty = playedGame.game.difficulty == difficulty
            } else  playedGame.showElementByDifficulty = true
        }

    }

    private fun applyResultFilter(status: GameResult) {
        playedGames.value!!.forEach { playedGame -> if (status == GameResult.ONLY_WIN ) {
            playedGame.showElementByResult = playedGame.game.win
        }
        else if (status == GameResult.ALL) {
            playedGame.showElementByResult = true
        }
            else {
            playedGame.showElementByResult = !playedGame.game.win

        }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class DoneGamesFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        return PlayedGamesViewModel(application) as T
    }
}