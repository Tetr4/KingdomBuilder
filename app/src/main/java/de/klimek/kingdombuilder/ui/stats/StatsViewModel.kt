package de.klimek.kingdombuilder.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.klimek.kingdombuilder.model.Stats
import de.klimek.kingdombuilder.service.StatsDao
import de.klimek.kingdombuilder.util.combineWith
import de.klimek.kingdombuilder.util.distinct
import de.klimek.kingdombuilder.util.mutableLiveDataOf
import de.klimek.kingdombuilder.util.observeOnce
import kotlinx.coroutines.launch

class StatsViewModel(
    private val month: Int,
    private val statsDao: StatsDao
) : ViewModel() {

    val stats = statsDao.getByMonth(month)
    val editable =
        stats.combineWith(statsDao.getLast()) { current, last -> current.month == last.month }
            .distinct()

    val economy = mutableLiveDataOf("")
    val loyalty = mutableLiveDataOf("")
    val stability = mutableLiveDataOf("")
    val unrest = mutableLiveDataOf("")
    val consumption = mutableLiveDataOf("")
    val treasury = mutableLiveDataOf("")
    val size = mutableLiveDataOf("")
    val income = mutableLiveDataOf("")

    init {
        stats.observeOnce {
            loadData(it)
            enableAutoSave()
        }
    }

    private fun loadData(stats: Stats) {
        stats.let {
            economy.value = it.economy.toString()
            loyalty.value = it.loyalty.toString()
            stability.value = it.stability.toString()
            unrest.value = it.unrest.toString()
            consumption.value = it.consumption.toString()
            treasury.value = it.treasury.toString()
            size.value = it.size.toString()
            income.value = it.income.toString()
        }
    }

    private fun enableAutoSave() {
        val fields = setOf(economy, loyalty, stability, unrest, consumption, treasury, size, income)
        fields
            .map { it.distinct() }
            .forEach { it.observeForever { save() } }
    }


    private fun save() = viewModelScope.launch {
        val stats = Stats(
            month = month,
            economy = economy.value?.toIntOrNull() ?: 0,
            loyalty = loyalty.value?.toIntOrNull() ?: 0,
            stability = stability.value?.toIntOrNull() ?: 0,
            unrest = unrest.value?.toIntOrNull() ?: 0,
            consumption = consumption.value?.toIntOrNull() ?: 0,
            treasury = treasury.value?.toIntOrNull() ?: 0,
            size = size.value?.toIntOrNull() ?: 0,
            income = income.value?.toIntOrNull() ?: 0
        )
        statsDao.save(stats)
    }
}

