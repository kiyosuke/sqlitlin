package com.kiyosuke.sqlitlin.db.core.support

import android.database.Cursor

interface SupportSQLiteDatabase {
    fun compileStatement(sql: String): SupportSQLiteStatement

    fun query(query: String): Cursor

    fun query(query: String, bindArgs: Array<Any?>): Cursor

    fun execSQL(sql: String)

    fun beginTransaction()

    fun endTransaction()

    fun setTransactionSuccessful()

    fun inTransaction(): Boolean
}