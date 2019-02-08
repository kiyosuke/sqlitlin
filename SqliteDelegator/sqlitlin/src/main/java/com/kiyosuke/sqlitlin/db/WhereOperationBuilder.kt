package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.column.Column

class WhereOperationBuilder {

    private val whereOperations: MutableList<WhereOp> = mutableListOf()

    infix fun <T> Column<T>.eq(data: T): WhereOp {
        val op = if (this is Column.Text) WhereOp("${this.name} = '$data'") else WhereOp("${this.name} = $data")
        whereOperations.add(op)
        return op
    }

    infix fun Column.Text.like(text: String): WhereOp {
        val op = WhereOp("${this.name} LIKE '$text'")
        whereOperations.add(op)
        return op
    }

    infix fun <T> Column<T>.between(between: Pair<T, T>): WhereOp {
        val op = WhereOp("${this.name} BETWEEN ${between.first} AND ${between.second}")
        whereOperations.add(op)
        return op
    }

    infix fun WhereOp.or(operation: WhereOp): WhereOp {
        val op = WhereOp("OR ${operation.operation}")
        whereOperations.add(op)
        return op
    }

    infix fun WhereOp.and(operation: WhereOp): WhereOp {
        val op = WhereOp("AND ${operation.operation}")
        whereOperations.add(op)
        return op
    }

    fun build(): WhereOp {
        return WhereOp(whereOperations.map(WhereOp::operation).joinToString(" "))
    }
}