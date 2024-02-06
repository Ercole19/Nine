package com.simonercole.nine.db


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simonercole.nine.utils.Difficulty

@Entity(tableName = "Game_Classic")
data class GameEntity(
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