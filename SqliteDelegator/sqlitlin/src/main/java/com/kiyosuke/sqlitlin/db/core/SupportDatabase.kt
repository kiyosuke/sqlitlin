package com.kiyosuke.sqlitlin.db.core

import android.database.Cursor
import android.database.sqlite.SQLiteStatement

interface SupportDatabase {
    fun compileStatement(sql: String): SQLiteStatement

    fun query(query: String): Cursor

    fun query(query: String, bindArgs: Array<Any?>): Cursor

    fun assertMainThread()

    fun beginTransaction()

    fun endTransaction()

    fun setTransactionSuccessful()

    fun inTransaction(): Boolean

    fun runInTransaction(body: () -> Unit)

    fun <V> runInTransaction(body: () -> V): V
}