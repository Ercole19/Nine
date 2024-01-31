package com.simonercole.nine.db

import com.simonercole.nine.ui.model.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DBRepo(private val dao : GameDAO) {
    fun insertGame(game: Game){
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertClassic(game)
        }
    }
    fun getMinTime(diff: String) :String?{
        return dao.getMinTime(diff)
    }
    fun getAllGames(): List<Game> {
        return dao.getGames()
    }

    fun deleteGame(game: Game) {
        dao.delete(game)
    }

}
