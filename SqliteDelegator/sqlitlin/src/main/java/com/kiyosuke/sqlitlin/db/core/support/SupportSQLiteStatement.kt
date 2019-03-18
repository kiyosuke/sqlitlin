package com.kiyosuke.sqlitlin.db.core.support

/**
 * SQLiteStatementを委譲するためのinterface
 */
interface SupportSQLiteStatement : SupportSQLiteProgram {

    fun execute()

    fun executeUpdateDelete(): Int

    fun executeInsert(): Long

    fun simpleQueryForLong(): Long

    fun simpleQueryForString(): String
}