package com.simonercole.nine.model

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.simonercole.nine.db.GameEntity
import com.simonercole.nine.db.GameRepository
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameResult
import com.simonercole.nine.viewmodel.PlayedGamesViewModel

/**
 * Represents a played game with associated details.
 * @param timeValue The time value of the played game.
 * @param showElementByResult Indicates whether to show the game element based on game result.
 * @param showElementByDifficulty Indicates whether to show the game element based on difficulty.
 * @param deleted Indicates whether the game has been deleted.
 * @param game The entity representing the game.
 */
data class PlayedGame(
    var timeValue: Int,
    var showElementByResult: Boolean,
    var showElementByDifficulty: Boolean,
    var deleted: Boolean,
    var game: GameEntity
)

/**
 * Manages a container of played games, including filtering and manipulation.
 * @param viewModel The view model associated with played games.
 */
class PlayedGameContainer(viewModel: PlayedGamesViewModel) {
    // The repository for played games.
    private var playedGamesRepository: GameRepository = viewModel.getRepo()
    // The total list of played games.
    private var totalPlayedGames: SnapshotStateList<PlayedGame>
    // The current list of played games after applying filters.
    var playedGames: SnapshotStateList<PlayedGame> = emptyList<PlayedGame>().toMutableStateList()
    // The filter to apply on played games.
    var filter: Filter = Filter()

    init {
        // Initialize the container with games fetched from the repository.
        val tempList: List<GameEntity> = playedGamesRepository.getAllGames()!!
        tempList.forEach { game ->
            playedGames.add(
                PlayedGame(
                    game.timerValue,
                    true,
                    true,
                    false,
                    game
                )
            )
        }
        totalPlayedGames = playedGames
    }

    /**
     * Sets the chosen difficulty filter.
     * @param difficulty The chosen difficulty level.
     */
    fun setChosenDiff(difficulty: Difficulty) {
        filter.setChosenDiff(difficulty)
    }

    /**
     * Sets the chosen game result filter.
     * @param filters The chosen game result.
     */
    fun setChosenGameResult(filters: GameResult) {
        filter.setChosenGameResult(filters)
    }

    /**
     * Filters the list by best time.
     */
    fun filterByBestTime() {
        filter.filterByBestTime()
    }

    /**
     * Removes a game from the container.
     * @param gameEntity The game entity to be removed.
     */
    fun removeGame(gameEntity: GameEntity) {
        playedGames = totalPlayedGames.toMutableStateList()
        playedGames.forEach { playedGame ->
            if (playedGame.game == gameEntity) {
                playedGame.deleted = true
            }
        }
        playedGamesRepository.deleteGame(gameEntity)
    }

    /**
     * Applies filters to the list of played games.
     */
    fun applyFiltersToList() {
        playedGames = totalPlayedGames.toMutableStateList()

        if (filter.showBestTimes) {
            sortByBestTime()
            return
        }
        applyDifficultyFilter(filter.chosenDifficulty)
        applyResultFilter(filter.gameResult)
    }

    /**
     * Sorts the list by best time.
     */
    private fun sortByBestTime() {
        applyResultFilter(GameResult.ONLY_WIN)
        applyDifficultyFilter(filter.chosenDifficulty)
        filter.gameResult = GameResult.ALL
        playedGames.sortBy { it.timeValue }
    }

    /**
     * Applies the difficulty filter to the list of played games.
     * @param difficulty The chosen difficulty level.
     */
    private fun applyDifficultyFilter(difficulty: Difficulty) {
        playedGames.forEach { playedGame ->
            if (difficulty != Difficulty.All) {
                playedGame.showElementByDifficulty = playedGame.game.difficulty == difficulty
            } else playedGame.showElementByDifficulty = true
        }

    }

    /**
     * Applies the game result filter to the list of played games.
     * @param status The chosen game result.
     */
    private fun applyResultFilter(status: GameResult) {
        playedGames.forEach { playedGame ->
            playedGame.showElementByResult = when (status) {
                GameResult.ONLY_WIN -> playedGame.game.win
                GameResult.ALL -> true
                else -> !playedGame.game.win
            }
        }
    }
}


