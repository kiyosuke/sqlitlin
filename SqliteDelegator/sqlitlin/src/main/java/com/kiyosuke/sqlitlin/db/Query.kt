package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.column.Column
import com.kiyosuke.sqlitlin.db.table.Table


class Select<T : Table>(private val columns: List<Column<*>>, private val from: T) {

    constructor(columns: List<Column<*>>, from: T, join: Join) : this(columns, from) {
        this.join = join
    }

    private var join: Join? = null

    private var where: Where? = null

    private var orderBy: OrderBy? = null

    private var limit: Limit? = null

    infix fun where(whereOperation: WhereOperationBuilder.(T) -> WhereOp) {
        val where = Where(whereOperation(WhereOperationBuilder, from))
        this@Select.where = where
    }

    infix fun orderBy(sort: Pair<Column<*>, OrderBy.SortOrder>) {
        this.orderBy = OrderBy(sort.first, sort.second)
    }

    infix fun limit(limit: Int) {
        this.limit = Limit(limit)
    }

    fun toSql(): String = buildString {
        append("SELECT ")
        append(columns.joinToString(",") { "${it.tableName}.${it.name} AS ${it.cursorKey}" })
        append(" FROM ${from.tableName}")
        join?.let {
            append(it.toSql())
        }
        where?.let {
            append(" WHERE ${it.whereOp.toSql()}")
        }
        orderBy?.let {
            append(" ORDER BY ${it.column.tableName}.${it.column.name} ${it.sortOrder.name}")
        }
        limit?.let {
            append(" LIMIT ${it.limit} OFFSET ${it.offset}")
        }
    }
}

val Column<*>.cursorKey: String get() = this.tableName + "_" + this.name

data class Where(val whereOp: WhereOp)

data class OrderBy(val column: Column<*>, val sortOrder: SortOrder) {
    enum class SortOrder {
        ASC,
        DESC
    }
}

data class Limit(val limit: Int, val offset: Int = 0)

