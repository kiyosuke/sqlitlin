package com.kiyosuke.sqlitedelegator

import android.app.Application
import android.util.Log
import com.kiyosuke.sqlitedelegator.db.Users
import com.kiyosuke.sqlitlin.db.core.Migration
import com.kiyosuke.sqlitlin.db.core.Sqlitlin
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteDatabase
import com.kiyosuke.sqlitlin.db.ext.addColumn

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        sqlitlin = Sqlitlin.builder(applicationContext, "sqlitlin.db", 2)
            .addTables(Users)
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    companion object {
        lateinit var sqlitlin: Sqlitlin

        private const val TAG = "Migration"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migration(database: SupportSQLiteDatabase) {
                Log.d(TAG, "running migration 1 to 2")
                database.addColumn(Users.job)
            }
        }
    }
}