package com.kiyosuke.sqlitlin.db.core.normal

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteDatabase
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteStatement

class NormalSQLiteDatabase internal constructor(private val delegate: SQLiteDatabase) :
    SupportSQLiteDatabase {

    override fun compileStatement(sql: String): SupportSQLiteStatement {
        return NormalSQLiteStatement(delegate.compileStatement(sql))
    }

    override fun query(query: String): Cursor {
        return this.query(query, emptyArray())
    }

    override fun query(query: String, bindArgs: Array<Any?>): Cursor {
        return delegate.rawQuery(query, bindArgs.map(Any?::toString).toTypedArray())
    }

    override fun execSQL(sql: String) {
        delegate.execSQL(sql)
    }

    override fun beginTransaction() {
        delegate.beginTransaction()
    }

    override fun endTransaction() {
        delegate.endTransaction()
    }

    override fun setTransactionSuccessful() {
        delegate.setTransactionSuccessful()
    }

    override fun inTransaction(): Boolean {
        return delegate.inTransaction()
    }
}