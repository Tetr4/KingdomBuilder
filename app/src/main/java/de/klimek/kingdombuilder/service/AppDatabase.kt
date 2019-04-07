package de.klimek.kingdombuilder.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.klimek.kingdombuilder.model.Stats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE 'stats' ADD COLUMN 'income' INTEGER NOT NULL DEFAULT 0");
    }
}

@Database(entities = [Stats::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun statsDao(): StatsDao

    companion object {
        fun create(context: Context, initialize: () -> Unit) = Room.databaseBuilder(
            context, AppDatabase::class.java, AppDatabase::class.java.simpleName
        )
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    GlobalScope.launch(Dispatchers.IO) { initialize() }
                }
            })
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}