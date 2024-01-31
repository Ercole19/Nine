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
    }
}