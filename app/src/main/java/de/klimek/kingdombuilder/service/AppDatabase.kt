package de.klimek.kingdombuilder.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.klimek.kingdombuilder.model.Stats

@Database(entities = [Stats::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun statsDao(): StatsDao

    companion object {
        fun create(context: Context) = Room.databaseBuilder(
            context, AppDatabase::class.java, AppDatabase::class.java.simpleName
        ).build()
    }
}