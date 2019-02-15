package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.column.Column
import com.kiyosuke.sqlitlin.db.table.Table


abstract class Query<T : Table>(protected val columns: List<Column<*>>, protected val from: T) {

    protected var join: Join? = null

    protected var where: Where? = null

    protected var groupBy: GroupBy? = null
    protected var having: Having? = null

    protected var orderBy: OrderBy? = null

    protected var limit: Limit? = null

    infix fun where(whereOperation: WhereOperationBuilder.(T) -> Op) {
        val where = Where(whereOperation(WhereOperationBuilder, from))
        this@Query.where = where
    }

    infix fun orderBy(sort: Pair<Column<*>, OrderBy.SortOrder>) {
        this.orderBy = OrderBy(sort.first, sort.second)
    }

    fun groupBy(vararg columns: Column<*>): GroupBy {
        this.groupBy = GroupBy(listOf(*columns))
        return groupBy!!
    }

    infix fun GroupBy.having(op: WhereOperationBuilder.(T) -> Op) {
        val having = Having(op(WhereOperationBuilder, from))
        this@Query.having = having
    }

    infix fun groupBy(columns: List<Column<*>>): GroupBy {
        this.groupBy = GroupBy(columns)
        return groupBy!!
    }

    infix fun limit(limit: Int) {
        this.limit = Limit(limit)
    }

    abstract fun toSql(): String
}

class Select<T : Table>(columns: List<Column<*>>, from: T) : Query<T>(columns, from) {
    constructor(columns: List<Column<*>>, from: T, join: Join) : this(columns, from) {
        this.join = join
    }

    override fun toSql(): String = buildString {
        append("SELECT ")
        append(columns.joinToString(",") { "${it.tableName}.${it.name} AS ${it.cursorKey}" })
        append(" FROM ${from.tableName}")
        join?.let {
            append(it.toSql())
        }
        where?.let {
            append(" WHERE ${it.whereOp.toSql()}")
        }
        groupBy?.let {
            append(" GROUP BY ")
            append(it.columns.joinToString(",") { column -> column.tableName + "." + column.name })
        }
        having?.let {
            append(" HAVING ${it.op.toSql()}")
        }
        orderBy?.let {
            append(" ORDER BY ${it.column.tableName}.${it.column.name} ${it.sortOrder.name}")
        }
        limit?.let {
            append(" LIMIT ${it.limit} OFFSET ${it.offset}")
        }
    }
}

class Count<T : Table>(columns: List<Column<*>>, from: T) : Query<T>(columns, from) {
    override fun toSql(): String = buildString {
        append("SELECT COUNT")
        if (columns.isEmpty()) {
            append("(*)")
        } else {
            append("(${columns.first().tableName}.${columns.first().name})")
        }
        append(" FROM ${from.tableName}")
        join?.let {
            append(it.toSql())
        }
        where?.let {
            append(" WHERE ${it.whereOp.toSql()}")
        }
        groupBy?.let {
            append(" GROUP BY ")
            append(it.columns.joinToString(",") { column -> column.tableName + "." + column.name })
        }
        having?.let {
            append(" HAVING ${it.op.toSql()}")
        }
        orderBy?.let {
            append(" ORDER BY ${it.column.tableName}.${it.column.name} ${it.sortOrder.name}")
        }
        limit?.let {
            append(" LIMIT ${it.limit} OFFSET ${it.offset}")
        }
    }
}

class Max<T : Table>(columns: List<Column<*>>, from: T) : Query<T>(columns, from) {
    override fun toSql(): String = buildString {
        append("SELECT MAX")
        val column = columns.first()
        append("(${column.tableName}.${column.name}) AS ${column.cursorKey}")
        append(" FROM ${from.tableName}")
        join?.let {
            append(it.toSql())
        }
        where?.let {
            append(" WHERE ${it.whereOp.toSql()}")
        }
        groupBy?.let {
            append(" GROUP BY ")
            append(it.columns.joinToString(",") { column -> column.tableName + "." + column.name })
        }
        having?.let {
            append(" HAVING ${it.op.toSql()}")
        }
        orderBy?.let {
            append(" ORDER BY ${it.column.tableName}.${it.column.name} ${it.sortOrder.name}")
        }
        limit?.let {
            append(" LIMIT ${it.limit} OFFSET ${it.offset}")
        }
    }
}

class Min<T : Table>(columns: List<Column<*>>, from: T) : Query<T>(columns, from) {
    override fun toSql(): String = buildString {
        append("SELECT MIN")
        val column = columns.first()
        append("(${column.tableName}.${column.name}) AS ${column.cursorKey}")
        append(" FROM ${from.tableName}")
        join?.let {
            append(it.toSql())
        }
        where?.let {
            append(" WHERE ${it.whereOp.toSql()}")
        }
        groupBy?.let {
            append(" GROUP BY ")
            append(it.columns.joinToString(",") { column -> column.tableName + "." + column.name })
        }
        having?.let {
            append(" HAVING ${it.op.toSql()}")
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

data class Where(val whereOp: Op)

data class GroupBy(val columns: List<Column<*>>)

data class Having(val op: Op)

data class OrderBy(val column: Column<*>, val sortOrder: SortOrder) {
    enum class SortOrder {
        ASC,
        DESC
    }
}

data class Limit(val limit: Int, val offset: Int = 0)

