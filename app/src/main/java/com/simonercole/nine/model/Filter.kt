package com.simonercole.nine.model

import com.simonercole.nine.utils.Difficulty
import com.simonercole.nine.utils.GameResult
class Filter {
    var chosenDifficulty : Difficulty = Difficulty.All
    var showBestTimes : Boolean = false
    var gameResult : GameResult = GameResult.ALL

    fun setChosenDiff(difficulty: Difficulty) {
        chosenDifficulty = difficulty
    }

    fun setChosenGameResult(filters : GameResult) {
        gameResult = filters
    }

    fun filterByBestTime() {
        showBestTimes = !showBestTimes
    }
}