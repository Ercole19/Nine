package com.simonercole.nine.ui.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
