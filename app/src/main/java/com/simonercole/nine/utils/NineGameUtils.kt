package com.simonercole.nine.utils

import androidx.compose.ui.res.stringResource
import com.simonercole.nine.db.Game
import java.io.Serializable

class NineGameUtils {
    companion object {
        const val symbols = "ABCDEFGHIJKLMNOPQRSTUVXYZ"
        fun getAttempts(difficulty: String, easy : String, medium : String) : Int {
            return when(difficulty) {
                easy -> 4
                medium -> 4
                else -> 3
            }
        }

        fun getTime(difficulty: String, easy: String, medium: String) : String {
            return when(difficulty) {
                easy -> "01:40"
                medium -> "01:20"
                else -> "00:50"
            }
        }
        fun getTimerLabel(value: Int): String {
            return "${padding(value / 60)} : ${padding(value % 60)}"
        }

        private fun padding(value: Int) = if (value < 10) ("0$value") else "" + value

        fun parseIt(time: String): Int {
            val firstOne = time.substring(0, 2)
            val secondOne = time.substring(5, 7)
            val result = firstOne + secondOne
            return result.toInt()
        }
    }
    enum class EndRequest {
        None, Quit, Refresh
    }

    enum class Difficulty : Serializable {
        Easy, Medium, Hard, All
    }

    enum class GameResult {
        ONLY_WIN, ONLY_LOSE, ALL
    }

    enum class GameStatus {
        NotStarted, FirstGuess, OnGoing, Paused, Won, Lost,
    }
}