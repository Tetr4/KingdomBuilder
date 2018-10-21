package de.klimek.kingdombuilder

import android.app.Application
import de.klimek.kingdombuilder.service.AppDatabase
import de.klimek.kingdombuilder.ui.MainViewModel
import de.klimek.kingdombuilder.ui.stats.StatsViewModel
import org.koin.android.ext.android.startKoin
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

private val module = module {
    viewModel { (month: Int) -> StatsViewModel(month, get()) }
    viewModel { MainViewModel(get()) }
    single { AppDatabase.create(get()) }
    single { get<AppDatabase>().statsDao() }
}

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(module))
    }
}