package com.simonercole.nine.utils

enum class EndRequest {
    None, Quit, Refresh
}

enum class Difficulty {
    Easy, Medium, Hard, All
}

enum class GameResult {
    ONLY_WIN, ONLY_LOSE, ALL
}

enum class GameStatus {
    NotStarted, FirstGuess, OnGoing, Paused, Won, Lost,
}