package com.simonercole.nine.ui.model

class NineGameUtils {
    companion object {
        const val symbols = "ABCDEFGHIJKLMNOPQRSTUVXYZ"
        fun getAttempts(difficulty : Difficulty) : Int {
            return when(difficulty.toString()) {
                "Easy" -> 4
                "Medium" -> 4
                else -> 3
            }
        }

        fun getTime(difficulty : Difficulty) : String {
            return when(difficulty.toString()) {
                "Easy" -> "01:40"
                "Medium" -> "01:20"
                else -> "00:50"
            }
        }
        fun getTimerLabel(value: Int): String {
            return "${padding(value / 60)} : ${padding(value % 60)}"
        }

        fun padding(value: Int) = if (value < 10) ("0$value") else "" + value

        fun parseIt(time: String): Int {
            val firstOne = time.substring(0, 2)
            val secondOne = time.substring(5, 7)
            val result = firstOne + secondOne
            return result.toInt()
        }
    }
    data class GameSort(
        var value :Int,
        var game : Game
    )
    enum class EndRequest {
        None, Quit, Refresh
    }


}