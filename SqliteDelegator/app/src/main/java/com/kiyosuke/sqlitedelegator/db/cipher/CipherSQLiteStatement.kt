package com.kiyosuke.sqlitedelegator.db.cipher

import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteStatement
import net.sqlcipher.database.SQLiteStatement

class CipherSQLiteStatement(private val delegate: SQLiteStatement) : CipherSQLiteProgram(delegate),
    SupportSQLiteStatement {
    override fun execute() {
        delegate.execute()
    }

    override fun executeUpdateDelete(): Int = delegate.executeUpdateDelete()


    override fun executeInsert(): Long = delegate.executeInsert()


    override fun simpleQueryForLong(): Long = delegate.simpleQueryForLong()

    override fun simpleQueryForString(): String = delegate.simpleQueryForString()
}