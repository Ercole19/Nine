package com.simonercole.nine.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.simonercole.nine.ui.model.GameClassic
import com.simonercole.nine.ui.model.GameGauntlet

@Dao
interface GameDAO {

    @Insert
    fun insertClassic(game: GameClassic)

    @Insert
    fun insertGauntlet(game: GameGauntlet)

    @Query("select * from Game_Classic  order by id DESC")  //ordinando i risultati per id decrescente appariranno per prime le partite più recenti
    fun getGames(): List<GameClassic>

    @Query("select * from Game_Gauntlet  order by id DESC")  //ordinando i risultati per id decrescente appariranno per prime le partite più recenti
    fun getGamesGauntlet(): List<GameGauntlet>

    @Query("select min(time) from Game_Classic where difficulty = :diff and win = 1")  //ordinando i risultati per id decrescente appariranno per prime le partite più recenti
    fun getMinTime(diff: String): String?

    @Query("select max(score) from Game_Gauntlet")  //ordinando i risultati per id decrescente appariranno per prime le partite più recenti
    fun getBestScore(): Int?


    @Delete
    fun delete(game: GameClassic)

    @Delete
    fun deleteGauntlet(game: GameGauntlet)

    /*@Query("select * from Game where difficulty = 1 order by id DESC")
    fun getMediumGames(): List<Game>

    @Query("select * from Game where difficulty = 2 order by id DESC")
    fun getHardGames(): List<Game>*/

}
