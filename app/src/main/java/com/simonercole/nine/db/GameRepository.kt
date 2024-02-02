package com.simonercole.nine.db

import com.simonercole.nine.utils.NineGameUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GameRepository(private val gameDAO: GameDAO) {
        fun saveToDB(game : Game) {
            val job = CoroutineScope(Dispatchers.IO).launch {
                gameDAO.insert(game)
            }
            runBlocking {
                job.join()
            }
        }

        fun getUserBestTime(diff : NineGameUtils.Difficulty): String? {
            var bestTime : String? = null
            val job = CoroutineScope(Dispatchers.IO).launch {
                bestTime = gameDAO.getBestTime(diff) ?: "99 : 99"
            }
            runBlocking { job.join() }
            return bestTime
        }

        fun getAllGames(): List<Game>? {
            var totalGames: List<Game>? = null
            val job = CoroutineScope(Dispatchers.IO).launch {
                totalGames = if (gameDAO.getAllGames().isNotEmpty()) {
                    gameDAO.getAllGames()
                } else emptyList()
            }
            runBlocking { job.join() }
            return totalGames
        }

        fun deleteGame(game : Game) {
            val job = CoroutineScope(Dispatchers.IO).launch {
                gameDAO.delete(game)
            }
            runBlocking {
                job.join()
            }
        }

}