package de.klimek.kingdombuilder.ui.stats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopify.livedataktx.*
import de.klimek.kingdombuilder.model.Stats
import de.klimek.kingdombuilder.service.StatsDao
import de.klimek.kingdombuilder.util.mutableLiveDataOf
import de.klimek.kingdombuilder.util.observeOnce
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class StatsViewModel(
    private val month: Int,
    private val statsDao: StatsDao
) : ViewModel() {
    val stats = statsDao.getByMonth(month)
    val isEnabled = stats.combineWith(statsDao.getLast()) { current, last -> current == last }

    var economy = mutableLiveDataOf("")
    var loyalty = mutableLiveDataOf("")
    var stability = mutableLiveDataOf("")
    var unrest = mutableLiveDataOf("")
    var consumption = mutableLiveDataOf("")
    var treasury = mutableLiveDataOf("")
    var size = mutableLiveDataOf("")

    init {
        stats.nonNull().observeOnce {
            economy.value = it.economy.toString()
            loyalty.value = it.loyalty.toString()
            stability.value = it.stability.toString()
            unrest.value = it.unrest.toString()
            consumption.value = it.consumption.toString()
            treasury.value = it.treasury.toString()
            size.value = it.size.toString()
        }
        val fields = setOf(economy, loyalty, stability, unrest, consumption, treasury, size)
        fields.forEach(this::saveAfterDelay)
    }

    private fun saveAfterDelay(data: MutableLiveData<String>) {
        data.distinct()
            .debounce(100)
            .observe { save() }
    }

    private fun save() {
        val stats = Stats(
            month = month,
            economy = economy.value?.toIntOrNull() ?: 0,
            loyalty = loyalty.value?.toIntOrNull() ?: 0,
            stability = stability.value?.toIntOrNull() ?: 0,
            unrest = unrest.value?.toIntOrNull() ?: 0,
            consumption = consumption.value?.toIntOrNull() ?: 0,
            treasury = treasury.value?.toIntOrNull() ?: 0,
            size = size.value?.toIntOrNull() ?: 0
        )
        GlobalScope.launch(Dispatchers.IO) {
            statsDao.save(stats)
        }
    }
}
