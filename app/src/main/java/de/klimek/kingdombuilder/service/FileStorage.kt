package de.klimek.kingdombuilder.service

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

class FileStorage(
    private val context: Context,
    private val database: AppDatabase
) {

    suspend fun exportDatabase(uri: Uri) = withContext(Dispatchers.IO) {
        val dbPath = context.getDatabasePath(AppDatabase.fileName)
        val source = FileInputStream(dbPath).channel
        source.use {
            val descriptor = context.contentResolver.openFileDescriptor(uri, "w")
                ?: throw FileNotFoundException()
            val destination = FileOutputStream(descriptor.fileDescriptor).channel
            destination.use {
                destination.transferFrom(source, 0, source.size())
            }
        }
    }

    suspend fun restoreDatabase(uri: Uri) = withContext(Dispatchers.IO) {
        val descriptor = context.contentResolver.openFileDescriptor(uri, "r")
            ?: throw FileNotFoundException()
        val source = FileInputStream(descriptor.fileDescriptor).channel
        source.use {
            val dbPath = context.getDatabasePath(AppDatabase.fileName)
            val destination = FileOutputStream(dbPath).channel
            destination.use {
                destination.transferFrom(source, 0, source.size())
            }
        }
        database.invalidate()
    }
}