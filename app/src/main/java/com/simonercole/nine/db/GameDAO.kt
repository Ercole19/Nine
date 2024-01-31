package com.simonercole.nine.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.simonercole.nine.ui.model.Game

@Dao
interface GameDAO {

    @Insert
    fun insertClassic(game: Game)

    @Query("select * from Game_Classic  order by id DESC")  //ordinando i risultati per id decrescente appariranno per prime le partite più recenti
    fun getGames(): List<Game>

    @Query("select min(time) from Game_Classic where difficulty = :diff and win = 1")  //ordinando i risultati per id decrescente appariranno per prime le partite più recenti
    fun getMinTime(diff: String): String?

    @Delete
    fun delete(game: Game)

}
