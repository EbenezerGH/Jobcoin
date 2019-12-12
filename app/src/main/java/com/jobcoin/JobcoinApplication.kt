package com.jobcoin

import android.app.Application
import com.jobcoin.di.jobcoinViewerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import remoteDatasourceModule

class JobcoinApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@JobcoinApplication)
            properties(mapOf(DataSourceProperties.SERVER_URL to BuildConfig.BASE_URL))
            modules(
                    listOf(
                            remoteDatasourceModule,
                            jobcoinViewerModule))
        }
    }
}