package de.klimek.kingdombuilder.ui

import androidx.lifecycle.ViewModel
import com.shopify.livedataktx.combineWith
import com.shopify.livedataktx.distinct
import com.shopify.livedataktx.map
import de.klimek.kingdombuilder.model.Stats
import de.klimek.kingdombuilder.service.StatsDao
import de.klimek.kingdombuilder.util.mutableLiveDataOf
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class MainViewModel(private val statsDao: StatsDao) : ViewModel() {
    val stats = statsDao.getAll().map {
        if (it == null || it.isEmpty()) {
            listOf(Stats())
        } else {
            it
        }
    }
    val selectedStatsIndex = mutableLiveDataOf(0)
    val isLastSelected = selectedStatsIndex.combineWith(stats) { index, stats -> index == stats?.lastIndex }

    init {
        stats.map { it?.size }.distinct().observeForever { goToEndOfList() }
    }

    fun save() =
        GlobalScope.launch(Dispatchers.IO) {
            val last = stats.value?.lastOrNull()
            val new = last?.let { it.copy(month = it.month + 1) } ?: Stats()
            statsDao.save(new)
        }

    fun delete() {
        GlobalScope.launch(Dispatchers.IO) {
            val last = stats.value?.lastOrNull()
            last?.let { statsDao.delete(it) }
        }
    }

    private fun goToEndOfList() {
        selectedStatsIndex.value = stats.value?.lastIndex
    }
}
