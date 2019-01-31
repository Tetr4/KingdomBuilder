package de.klimek.kingdombuilder.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import de.klimek.kingdombuilder.model.Stats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [Stats::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun statsDao(): StatsDao

    companion object {
        fun create(context: Context, initialize: () -> Unit) = Room.databaseBuilder(
            context, AppDatabase::class.java, AppDatabase::class.java.simpleName
        ).addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                GlobalScope.launch(Dispatchers.IO) { initialize() }
            }
        }).build()
    }
}