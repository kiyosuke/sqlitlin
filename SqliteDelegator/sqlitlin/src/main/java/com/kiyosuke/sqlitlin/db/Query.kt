package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.column.Column
import com.kiyosuke.sqlitlin.db.table.Table


class Select<T : Table>(val from: T) {

    var where: Where? = null
        private set

    var orderBy: OrderBy? = null
        private set

    var limit: Limit? = null
        private set

    infix fun where(whereOperation: WhereOperationBuilder.(T) -> Unit) {
        val where = Where(WhereOperationBuilder().apply {
            whereOperation(from)
        }.build())
        this@Select.where = where
    }

    infix fun orderBy(sort: Pair<Column<*>, OrderBy.SortOrder>) {
        this.orderBy = OrderBy(sort.first, sort.second)
    }

    infix fun limit(limit: Int) {
        this.limit = Limit(limit)
    }

    fun toSql(): String = buildString {
        append("SELECT * FROM ${from.tableName}")
        where?.let {
            append(" WHERE ${it.whereOp.operation}")
        }
        orderBy?.let {
            append(" ORDER BY ${it.column.name} ${it.sortOrder.name}")
        }
        limit?.let {
            append(" LIMIT ${it.limit}")
        }
    }

}

data class Where(val whereOp: WhereOp)

data class OrderBy(val column: Column<*>, val sortOrder: SortOrder) {
    enum class SortOrder {
        ASC,
        DESC
    }
}

data class Limit(val limit: Int)

