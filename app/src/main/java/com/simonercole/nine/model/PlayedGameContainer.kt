package com.simonercole.nine.model

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.simonercole.nine.db.GameEntity
import com.simonercole.nine.db.GameRepository
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameResult
import com.simonercole.nine.utils.NineGameUtils
import com.simonercole.nine.utils.NineGameUtils.Companion.parseIt

data class PlayedGame(
    var timeValue :Int,
    var showElementByResult : Boolean,
    var showElementByDifficulty: Boolean,
    var deleted : Boolean,
    var game : GameEntity
)

class PlayedGameContainer(gameRepository: GameRepository)  {
    private var playedGamesRepository : GameRepository = gameRepository
    private var totalPlayedGames : SnapshotStateList<PlayedGame>
    var playedGames: SnapshotStateList<PlayedGame> = emptyList<PlayedGame>().toMutableStateList()
    var filter: Filter = Filter()

    init {
        val tempList: List<GameEntity> = playedGamesRepository.getAllGames()!!
        tempList.forEach { game ->
            playedGames.add(
                PlayedGame(
                    parseIt(game.time),
                    true,
                    true,
                    false,
                    game
                )
            )
        }
        totalPlayedGames = playedGames
    }

    fun setChosenDiff(difficulty: Difficulty) {
        filter.setChosenDiff(difficulty)
    }

    fun setChosenGameResult(filters: GameResult) {
        filter.setChosenGameResult(filters)
    }

    fun filterByBestTime() {
        filter.filterByBestTime()
    }

    fun removeGame(gameEntity: GameEntity) {
        playedGames = totalPlayedGames.toMutableStateList()
        playedGames.forEach { playedGame ->
            if (playedGame.game == gameEntity) {
                playedGame.deleted = true
            }
        }
        playedGamesRepository.deleteGame(gameEntity)
    }

    fun applyFiltersToList() {
        playedGames = totalPlayedGames.toMutableStateList()

        if (filter.showBestTimes) {
            sortByBestTime()
            return
        }
        applyDifficultyFilter(filter.chosenDifficulty)
        applyResultFilter(filter.gameResult)
    }

    private fun sortByBestTime() {
        applyResultFilter(GameResult.ONLY_WIN)
        applyDifficultyFilter(filter.chosenDifficulty)
        filter.gameResult = GameResult.ALL
        playedGames.sortBy{it.timeValue}
    }

    private fun applyDifficultyFilter(difficulty: Difficulty) {
        playedGames.forEach { playedGame ->
            if (difficulty != Difficulty.All) {
                playedGame.showElementByDifficulty = playedGame.game.difficulty == difficulty
            } else  playedGame.showElementByDifficulty = true
        }

    }

    private fun applyResultFilter(status: GameResult) {
        playedGames.forEach { playedGame -> if (status == GameResult.ONLY_WIN ) {
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

