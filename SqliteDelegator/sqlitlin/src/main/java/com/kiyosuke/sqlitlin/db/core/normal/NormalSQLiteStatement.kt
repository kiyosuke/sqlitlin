package com.kiyosuke.sqlitlin.db.core.normal

import android.database.sqlite.SQLiteStatement
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteStatement

/**
 * Delegate to a [SQLiteStatement]
 */
class NormalSQLiteStatement internal constructor(private val delegate: SQLiteStatement) :
    NormalSQLiteProgram(delegate),
    SupportSQLiteStatement {

    override fun execute() {
        delegate.execute()
    }

    override fun executeUpdateDelete(): Int = delegate.executeUpdateDelete()


    override fun executeInsert(): Long = delegate.executeInsert()


    override fun simpleQueryForLong(): Long = delegate.simpleQueryForLong()

    override fun simpleQueryForString(): String = delegate.simpleQueryForString()
}