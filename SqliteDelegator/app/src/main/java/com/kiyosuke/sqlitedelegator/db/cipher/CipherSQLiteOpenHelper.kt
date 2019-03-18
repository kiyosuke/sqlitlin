package com.kiyosuke.sqlitedelegator.db.cipher

import android.content.Context
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteDatabase
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteOpenHelper
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

class CipherSQLiteOpenHelper(
    context: Context,
    name: String,
    callback: SupportSQLiteOpenHelper.Callback
) : SupportSQLiteOpenHelper {
    private val delegate =
        OpenHelper(context, name, callback)

    override fun getDatabaseName(): String {
        throw RuntimeException("Not supported getDatabaseName")
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

        init {
            // 暗号化するために呼び出す必要あり
            SQLiteDatabase.loadLibs(context)
        }

        private var migrated: Boolean = false

        private fun SQLiteDatabase.wrap(): SupportSQLiteDatabase {
            return CipherSQLiteDatabase(this)
        }

        override fun onCreate(db: SQLiteDatabase) {
            callback.onCreate(db.wrap())
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            migrated = true
            callback.onUpgrade(db.wrap(), oldVersion, newVersion)
        }

        @Synchronized
        fun getWritableSupportDatabase(): SupportSQLiteDatabase {
            migrated = false
            val db = super.getWritableDatabase("password")
            if (migrated) {
                close()
                return getWritableSupportDatabase()
            }
            return db.wrap()
        }

        @Synchronized
        fun getReadableSupportDatabase(): SupportSQLiteDatabase {
            migrated = false
            val db = super.getReadableDatabase("password")
            if (migrated) {
                close()
                return getReadableSupportDatabase()
            }
            return db.wrap()
        }
    }
}