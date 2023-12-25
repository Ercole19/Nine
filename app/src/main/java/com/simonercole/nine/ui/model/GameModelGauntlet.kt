package com.simonercole.nine.ui.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Game_Gauntlet")
data class GameGauntlet(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "time")
    var time : String,
    @ColumnInfo(name = "date")
    var dateTime: String,
    @ColumnInfo(name = "score")
    var score: Int,

)
