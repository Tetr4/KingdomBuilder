package de.klimek.kingdombuilder.service

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.klimek.kingdombuilder.model.Stats

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE 'stats' ADD COLUMN 'income' INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(entities = [Stats::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun statsDao(): StatsDao

    companion object {
        val fileName: String = AppDatabase::class.java.simpleName

        fun create(context: Context, initialize: () -> Unit) = Room
            .databaseBuilder(context, AppDatabase::class.java, fileName)
            .setJournalMode(JournalMode.TRUNCATE) // disable write-ahead-log for backup/restore
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) = initialize()
            })
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @SuppressLint("RestrictedApi", "VisibleForTests")
    fun invalidate() {
        // TODO This is kinda hacky and won't apply migrations.
        // We should close and reopen the somehow database, e.g. with the openHelper.
        val tablesCursor = query("SELECT name FROM sqlite_master WHERE type = 'table'", null)
        val tablesNames = generateSequence { if (tablesCursor.moveToNext()) tablesCursor else null }
            .map { it.getString(0) }
            .toList()
        invalidationTracker.notifyObserversByTableNames(*tablesNames.toTypedArray())
    }
}