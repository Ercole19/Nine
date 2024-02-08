package com.simonercole.nine.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import com.simonercole.nine.db.GameEntity
import com.simonercole.nine.db.GameRepository
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameStatus
import java.time.LocalDateTime

/**
 * Represents an ongoing game session with its status and details.
 * @param gameRepository The repository for game-related data.
 */
class OnGoingGame(private var gameRepository: GameRepository) {

    // The maximum number of attempts allowed for the game.
    var maxAttempts = 0
    // The total time allowed for the game.
    var totalTime = 0
    // The best time achieved by the user.
    var bestTime: String? = null
    // The current value of the game timer.
    val timerValue: MutableState<Int> = mutableIntStateOf(0)
    // The user's game time.
    var userGameTime: String = " "
    // The number of attempts made by the user.
    var attempts: Int = 0
    // The difficulty level of the game.
    var difficulty = Difficulty.Easy
    // The status of the game.
    var gameStatus: GameStatus = GameStatus.NotStarted

    /**
     * Sets up the game with the specified difficulty.
     * @param difficulty The difficulty level of the game.
     */
    fun setGameInfo(difficulty: Difficulty) {
        this.difficulty = difficulty
        bestTime = gameRepository.getUserBestTime(difficulty)

        // Set timer value and total time based on difficulty.
        timerValue.value = when (difficulty) {
            Difficulty.Easy -> 100
            Difficulty.Medium -> 80
            else -> 50
        }
        totalTime = timerValue.value

        // Set max attempts based on difficulty.
        maxAttempts = when (difficulty) {
            Difficulty.Easy -> 4
            Difficulty.Medium -> 4
            else -> 3
        }
    }

    /**
     * Saves the current game status to the database.
     */
    fun saveGameToDB() {
        val gameEntity = GameEntity(
            difficulty = difficulty,
            attempts = attempts,
            time = userGameTime,
            dateTime = LocalDateTime.now().toString(),
            win = gameStatus == GameStatus.Won,
            timerValue = parseTimerValueToIntValue(userGameTime)
        )
        gameRepository.saveToDB(gameEntity)
    }

    /**
     * Resets the game status to 'NotStarted'.
     */
    fun resetGameStatus() {
        gameStatus = GameStatus.NotStarted
    }

    /**
     * Pauses the game status.
     */
    fun pauseGameStatus() {
        gameStatus = GameStatus.Paused
    }

    /**
     * Resumes the game status.
     */
    fun resumeGameStatus() {
        gameStatus = GameStatus.OnGoing
    }

    /**
     * Parses the user game time from string to integer value.
     * @param time The time string to be parsed.
     * @return The integer value representation of the time.
     */
    fun parseTimerValueToIntValue(time: String): Int {
        val firstOne = time.substring(0, 2)
        val secondOne = time.substring(5, 7)
        val result = firstOne + secondOne
        return result.toInt()
    }
}
