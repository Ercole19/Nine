package com.simonercole.nine.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.simonercole.nine.utils.Difficulty

@Dao
interface GameDAO {

    @Insert
    fun insert(gameEntity: GameEntity)

    @Query("select * from Game_Classic  order by id DESC")  //order by id desc grants that most recent games will be shown first and old ones after
    fun getAllGames(): List<GameEntity>

    @Query("select min(time) from Game_Classic where difficulty = :diff and win = 1")
    fun getBestTime(diff: Difficulty): String?

    @Delete
    fun delete(gameEntity: GameEntity)

}
