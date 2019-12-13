package de.klimek.kingdombuilder

import android.app.Application
import de.klimek.kingdombuilder.model.Stats
import de.klimek.kingdombuilder.service.AppDatabase
import de.klimek.kingdombuilder.service.FileStorage
import de.klimek.kingdombuilder.service.StatsDao
import de.klimek.kingdombuilder.ui.MainViewModel
import de.klimek.kingdombuilder.ui.stats.StatsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

private fun initializeDatabase(statsDao: StatsDao) {
    statsDao.save(Stats())
}

private val module = module {
    viewModel { (month: Int) -> StatsViewModel(month, get()) }
    viewModel { MainViewModel(get(), get()) }
    single { AppDatabase.create(get()) { initializeDatabase(get()) } }
    single { get<AppDatabase>().statsDao() }
    single { FileStorage() }
}


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(module)
        }
    }
}