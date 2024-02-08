package com.simonercole.nine.db

import com.simonercole.nine.utils.Difficulty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GameRepository(private val gameDAO: GameDAO) {
        fun saveToDB(gameEntity : GameEntity) {
            val job = CoroutineScope(Dispatchers.IO).launch {
                gameDAO.insert(gameEntity)
            }
            runBlocking {
                job.join()
            }
        }

        fun getUserBestTime(diff : Difficulty): String? {
            var bestTime : String? = null
            val job = CoroutineScope(Dispatchers.IO).launch {
                bestTime = gameDAO.getBestTime(diff) ?: "99 : 99"
            }
            runBlocking { job.join() }
            return bestTime
        }

        fun getAllGames(): List<GameEntity>? {
            var totalGameEntities: List<GameEntity>? = null
            val job = CoroutineScope(Dispatchers.IO).launch {
                totalGameEntities = gameDAO.getAllGames().ifEmpty { emptyList() }
            }
            runBlocking { job.join() }
            return totalGameEntities
        }

        fun deleteGame(gameEntity : GameEntity) {
            val job = CoroutineScope(Dispatchers.IO).launch {
                gameDAO.delete(gameEntity)
            }
            runBlocking {
                job.join()
            }
        }

}