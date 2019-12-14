package de.klimek.kingdombuilder.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.klimek.kingdombuilder.R
import de.klimek.kingdombuilder.model.Stats
import de.klimek.kingdombuilder.service.FileStorage
import de.klimek.kingdombuilder.service.StatsDao
import de.klimek.kingdombuilder.util.*
import kotlinx.coroutines.launch

private val TAG = MainViewModel::class.java.simpleName

class MainViewModel(
    private val statsDao: StatsDao,
    private val storage: FileStorage
) : ViewModel() {

    val stats = statsDao.getAll()
    val statsSize = stats.map { it.size }.distinct()
    val selectedStatsIndex = mutableLiveDataOf(0)
    val isLastSelected = selectedStatsIndex
        .combineWith(stats) { index, stats -> index == stats.lastIndex }
        .distinct()
    val isFirstSelected = selectedStatsIndex
        .map { it == 0 }
        .distinct()
    val message = SingleLiveEvent<Int?>()

    fun newMonth() = viewModelScope.launch {
        val last = stats.value?.lastOrNull()
        val new = last?.let { it.copy(month = it.month + 1) } ?: Stats()
        statsDao.save(new)
    }

    fun deleteMonth() = viewModelScope.launch {
        val last = stats.value?.lastOrNull()
        last?.let { statsDao.delete(it) }
    }

    fun createBackup(uri: Uri) = viewModelScope.launch {
        try {
            storage.exportDatabase(uri)
            message.value = R.string.backup_success
        } catch (e: Exception) {
            Log.e(TAG, "Could not create backup", e)
            message.value = R.string.backup_error
        }
    }

    fun restoreBackup(uri: Uri) = viewModelScope.launch {
        try {
            storage.restoreDatabase(uri)
            message.value = R.string.restore_success
        } catch (e: Exception) {
            Log.e(TAG, "Could not restore backup", e)
            message.value = R.string.restore_error
        }
    }
}
