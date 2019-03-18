package com.kiyosuke.sqlitlin.db.core.support

import android.content.Context

interface SupportSQLiteOpenHelper {
    fun getDatabaseName(): String

    fun getWritableDatabase(): SupportSQLiteDatabase

    fun getReadableDatabase(): SupportSQLiteDatabase

    fun close()

    abstract class Callback(val version: Int) {
        abstract fun onCreate(db: SupportSQLiteDatabase)

        abstract fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int)

        abstract fun onDowngrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int)
    }

    data class Configure(
        val context: Context,
        val name: String,
        val callback: Callback
    )

    interface Factory {
        fun create(configure: Configure): SupportSQLiteOpenHelper
    }
}