package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.StageProject

@Database(entities = [StageProject::class], version = 1, exportSchema = false)
abstract class StageDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: StageDatabase? = null

        fun getDatabase(context: Context): StageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StageDatabase::class.java,
                    "stage_designer_db"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
