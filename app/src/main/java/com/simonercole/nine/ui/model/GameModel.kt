package com.simonercole.nine.ui.model


import android.app.Application
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simonercole.nine.db.DBRepo
import com.simonercole.nine.db.GameDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Entity(tableName = "Game_Classic")
data class Game(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "difficulty")
    var difficulty : String,
    @ColumnInfo(name = "attempts")
    var attempts : Int = 0,
    @ColumnInfo(name = "time")
    var time : String,
    @ColumnInfo(name = "date")
    var dateTime: String,
    @ColumnInfo(name = "win")
    var win : Boolean,
)

class GameModel(application: Application) {
    private var gameDB: GameDB
    private var repository : DBRepo
    init {
        gameDB = GameDB.getInstance(application.baseContext)
        repository = DBRepo(gameDB.getDAO())
    }
    fun saveToDB(game : Game) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            repository.insertGame(game)
        }
        runBlocking {
            job.join()
        }
    }

    fun getUserBestTime(diff : String): String {
        var bestTime = ""
        val job = CoroutineScope(Dispatchers.IO).launch {
            bestTime = repository.getMinTime(diff) ?: "99 : 99"
        }
        runBlocking {
            job.join()
        }
        return bestTime
    }

    fun getAllGames(): List<Game> {
        var totalGames: List<Game> = emptyList()
        val job = CoroutineScope(Dispatchers.IO).launch {
            if (repository.getAllGames().isNotEmpty()) {
                totalGames = repository.getAllGames()
            }
        }
        runBlocking {
            job.join()
        }
        return totalGames
    }

    fun deleteGame(game : Game) {
        val act = CoroutineScope(Dispatchers.IO).launch {
            repository.deleteGame(game)
        }
        runBlocking {
            act.join()
        }
    }
}




