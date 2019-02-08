package com.kiyosuke.sqlitedelegator

import android.app.Application
import com.kiyosuke.sqlitedelegator.db.AppDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        db = AppDatabase(applicationContext, "sqlitlin.db", 1)
    }

    companion object {
        lateinit var db: AppDatabase
    }
}