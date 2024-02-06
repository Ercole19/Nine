package com.simonercole.nine.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import com.simonercole.nine.db.GameEntity
import com.simonercole.nine.db.GameRepository
import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameStatus
import java.time.LocalDateTime

class OnGoingGame(private var gameRepository: GameRepository) {

    var maxAttempts = 0
    var totalTime = 0
    var bestTime : String? = null
    val timerValue : MutableState<Int> = mutableIntStateOf(0)
    var userGameTime : String  = " "
    var attempts : Int = 0
    var difficulty = Difficulty.Easy
    var gameStatus : GameStatus = GameStatus.NotStarted


    fun setGameInfo(difficulty : Difficulty) {
        this.difficulty = difficulty
        bestTime = gameRepository.getUserBestTime(difficulty)

        timerValue.value = when (difficulty) {
            Difficulty.Easy -> 100
            Difficulty.Medium -> 80
            else -> 50
        }

        totalTime = timerValue.value
        maxAttempts = when (difficulty) {
            Difficulty.Easy -> 4
            Difficulty.Medium -> 4
            else -> 3
        }
    }

    fun saveGameToDB() {
        val gameEntity = GameEntity(
            difficulty = difficulty,
            attempts = attempts,
            time = userGameTime,
            dateTime = LocalDateTime.now().toString(),
            win = gameStatus == GameStatus.Won,
        )
        gameRepository.saveToDB(gameEntity)
    }

    fun resetGame() {
        gameStatus = GameStatus.NotStarted
    }

    fun userChangeActivityMidGame() {
        gameStatus = GameStatus.Paused
    }

    fun quitRequest() {
        gameStatus = GameStatus.Paused
    }
    fun refreshRequest() {
        gameStatus = GameStatus.Paused
    }

    fun resumeGame() {
        gameStatus = GameStatus.OnGoing
    }

}