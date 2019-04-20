package com.kiyosuke.sqlitlin.db.core.normal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteDatabase
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteOpenHelper

class NormalSQLiteOpenHelper internal constructor(
    context: Context,
    name: String,
    callback: SupportSQLiteOpenHelper.Callback
) : SupportSQLiteOpenHelper {

    private val delegate =
        OpenHelper(context, name, callback)

    override fun getDatabaseName(): String {
        return delegate.databaseName
    }

    override fun getWritableDatabase(): SupportSQLiteDatabase {
        return delegate.getWritableSupportDatabase()
    }

    override fun getReadableDatabase(): SupportSQLiteDatabase {
        return delegate.getReadableSupportDatabase()
    }

    override fun close() {
        delegate.close()
    }

    class OpenHelper(context: Context, name: String, private val callback: SupportSQLiteOpenHelper.Callback) :
        SQLiteOpenHelper(context, name, null, callback.version) {

        private var migrated: Boolean = false

        private fun SQLiteDatabase.wrap(): SupportSQLiteDatabase {
            return NormalSQLiteDatabase(this)
        }

        override fun onCreate(db: SQLiteDatabase) {
            callback.onCreate(db.wrap())
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            migrated = true
            callback.onUpgrade(db.wrap(), oldVersion, newVersion)
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            migrated = true
            callback.onDowngrade(db.wrap(), oldVersion, newVersion)
        }

        @Synchronized
        fun getWritableSupportDatabase(): SupportSQLiteDatabase {
            migrated = false
            val db = super.getWritableDatabase()
            if (migrated) {
                close()
                return getWritableSupportDatabase()
            }
            return db.wrap()
        }

        @Synchronized
        fun getReadableSupportDatabase(): SupportSQLiteDatabase {
            migrated = false
            val db = super.getReadableDatabase()
            if (migrated) {
                close()
                return getReadableSupportDatabase()
            }
            return db.wrap()
        }
    }
}