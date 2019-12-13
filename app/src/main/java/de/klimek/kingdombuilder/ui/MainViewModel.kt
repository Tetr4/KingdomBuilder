package de.klimek.kingdombuilder.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.klimek.kingdombuilder.model.Stats
import de.klimek.kingdombuilder.service.FileStorage
import de.klimek.kingdombuilder.service.StatsDao
import de.klimek.kingdombuilder.util.combineWith
import de.klimek.kingdombuilder.util.distinct
import de.klimek.kingdombuilder.util.map
import de.klimek.kingdombuilder.util.mutableLiveDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val statsDao: StatsDao,
    private val storage: FileStorage
) : ViewModel() {

    val stats = statsDao.getAll()
    val statsSize = stats.map { it.size }.distinct()
    val selectedStatsIndex = mutableLiveDataOf(0)
    val isLastSelected =
        selectedStatsIndex.combineWith(stats) { index, stats -> index == stats.lastIndex }
            .distinct()
    val isFirstSelected = selectedStatsIndex.map { it == 0 }.distinct()

    fun newMonth() {
        val last = stats.value?.lastOrNull()
        val new = last?.let { it.copy(month = it.month + 1) } ?: Stats()
        viewModelScope.launch(Dispatchers.IO) {
            statsDao.save(new)
        }
    }

    fun deleteMonth() {
        val last = stats.value?.lastOrNull()
        if (last != null) {
            viewModelScope.launch(Dispatchers.IO) {
                statsDao.delete(last)
            }
        }
    }

    fun createBackup(uri: Uri) {

    }

    fun restoreBackup(uri: Uri) {

    }
}
