package com.simonercole.nine.db


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simonercole.nine.utils.NineGameUtils.Difficulty

@Entity(tableName = "Game_Classic")
data class Game(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "difficulty")
    var difficulty : Difficulty,
    @ColumnInfo(name = "attempts")
    var attempts : Int = 0,
    @ColumnInfo(name = "time")
    var time : String,
    @ColumnInfo(name = "date")
    var dateTime: String,
    @ColumnInfo(name = "win")
    var win : Boolean,
)

data class PlayedGame(
    var timeValue :Int,
    var showElementByResult : Boolean,
    var showElementByDifficulty: Boolean,
    var deleted : Boolean,
    var game : Game
)