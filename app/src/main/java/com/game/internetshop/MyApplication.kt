package com.game.internetshop

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.game.internetshop.di.AppModule
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
            modules(AppModule().appModule)
        }
    }
}