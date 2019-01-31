package de.klimek.kingdombuilder.ui

import androidx.lifecycle.ViewModel
import com.shopify.livedataktx.*
import de.klimek.kingdombuilder.model.Stats
import de.klimek.kingdombuilder.service.StatsDao
import de.klimek.kingdombuilder.util.mutableLiveDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(private val statsDao: StatsDao) : ViewModel() {
    val stats = statsDao.getAll().nonNull().distinct()
    val selectedStatsIndex = mutableLiveDataOf(0)
    val isLastSelected = selectedStatsIndex.combineWith(stats) { index, stats -> index == stats.lastIndex }.distinct()
    val isFirstSelected = selectedStatsIndex.map { it == 0 }.distinct()

    init {
        stats
            .map { it.size }
            .distinct()
            .observe { goToEndOfList() }
    }

    fun save() = GlobalScope.launch(Dispatchers.IO) {
        val last = stats.value?.lastOrNull()
        val new = last?.let { it.copy(month = it.month + 1) } ?: Stats()
        statsDao.save(new)
    }

    fun delete() = GlobalScope.launch(Dispatchers.IO) {
        val last = stats.value?.lastOrNull()
        last?.let { statsDao.delete(it) }
    }

    private fun goToEndOfList() {
        selectedStatsIndex.value = stats.value?.lastIndex
    }
}
