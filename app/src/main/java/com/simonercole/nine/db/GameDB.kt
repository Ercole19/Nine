package com.simonercole.nine.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [
    Game::class]
    , version = 1)
abstract class GameDB: RoomDatabase() {
    companion object{

        private var db: GameDB? = null

        fun getInstance(context: Context): GameDB{
            if(db == null){
                db = Room.databaseBuilder(
                    context,
                    GameDB::class.java,
                    "nine_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return db as GameDB
        }
    }
    abstract fun getDAO(): GameDAO
}