package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.column.Column
import com.kiyosuke.sqlitlin.db.table.Table

abstract class Join {
    abstract fun toSql(): String
}

class InnerJoin(
    private val joinTable: Table,
    private val onColumn: Column<*>,
    private val joinColumn: Column<*>
) : Join() {
    fun innerJoin(
        joinTable: Table,
        onColumn: Column<*>,
        joinColumn: Column<*>
    ): MultiJoin = MultiJoin(this, InnerJoin(joinTable, onColumn, joinColumn))

    override fun toSql(): String = buildString {
        append(" INNER JOIN ")
        append(joinTable.tableName)
        append(" ON ")
        append("${onColumn.tableName}.${onColumn.name}")
        append(" = ")
        append("${joinColumn.tableName}.${joinColumn.name}")
    }
}

class MultiJoin(private val join: Join, private val nextJoin: Join) : Join() {
    override fun toSql(): String = buildString {
        append(join.toSql())
        append(nextJoin.toSql())
    }
}
