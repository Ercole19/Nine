package com.simonercole.nine.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.simonercole.nine.ui.model.GameClassic
import com.simonercole.nine.ui.model.GameGauntlet

class DBRepo(private val dao : GameDAO) {
    fun insertGameClassic(game: GameClassic){
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertClassic(game)
        }
    }

    fun insertGameGauntlet(game: GameGauntlet){
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertGauntlet(game)
        }
    }

    fun getMinTime(diff: String) :String?{
        return dao.getMinTime(diff)
    }
    fun getMaxScore() : Int? {
        return dao.getBestScore()
    }
    fun getAllGamesClassic(): List<GameClassic> {
        return dao.getGames()
    }

    fun getAllGamesGauntlet(): List<GameGauntlet> {
        return dao.getGamesGauntlet()
    }

    fun deleteGame(game: GameClassic) {
        dao.delete(game)
    }

    fun deleteGameGauntlet(game: GameGauntlet) {
        dao.deleteGauntlet(game)
    }
}
