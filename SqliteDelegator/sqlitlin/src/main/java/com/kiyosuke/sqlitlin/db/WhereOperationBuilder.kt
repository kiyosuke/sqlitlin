package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.column.Column

object WhereOperationBuilder {

    infix fun <T> Column<T>.eq(data: T): WhereOp =
        if (this is Column.Text) Eq<String>(this, "'$data'") else Eq(this, data)

    infix fun <T> Column<T>.neq(data: T): WhereOp =
        if (this is Column.Text) Neq<String>(this, "'$data'") else Neq(this, data)

    infix fun <T> Column<T>.less(data: T): WhereOp = Less(this, data)

    infix fun <T> Column<T>.lessEq(data: T): WhereOp = LessEq(this, data)

    infix fun <T> Column<T>.greater(data: T): WhereOp = Greater(this, data)

    infix fun <T> Column<T>.greaterEq(data: T): WhereOp = GreaterEq(this, data)

    infix fun Column.Text.like(text: String): WhereOp = Like(this, text)

    infix fun <T> Column<T>.between(between: Pair<T, T>): WhereOp = Between(this, between)

    infix fun <T> Column<T>.inList(list: List<T>): WhereOp = InList(this, list)

    infix fun <T> Column<T>.notInList(list: List<T>): WhereOp = NotInList(this, list)

    infix fun WhereOp.or(operation: WhereOp): WhereOp = Or(this, operation)

    infix fun WhereOp.and(operation: WhereOp): WhereOp = And(this, operation)

}

class Eq<T>(private val column: Column<T>, private val data: T) : WhereOp() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} = $data")
    }
}

class Neq<T>(private val column: Column<T>, private val data: T) : WhereOp() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} != $data")
    }
}

class Less<T>(private val column: Column<T>, private val data: T) : WhereOp() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} < $data")
    }
}

class LessEq<T>(private val column: Column<T>, private val data: T) : WhereOp() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} <= $data")
    }
}

class Greater<T>(private val column: Column<T>, private val data: T) : WhereOp() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} > $data")
    }
}

class GreaterEq<T>(private val column: Column<T>, private val data: T) : WhereOp() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} >= $data")
    }
}

class Like(private val column: Column.Text, private val data: String) : WhereOp() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} LIKE '$data'")
    }
}

class Between<T>(private val column: Column<T>, private val data: Pair<T, T>) : WhereOp() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} BETWEEN ${data.first} AND ${data.second}")
    }
}

class InList<T>(private val column: Column<T>, private val list: List<T>) : WhereOp() {
    override fun toSql(): String {
        if (list.isEmpty()) throw IllegalArgumentException("list is empty")
        return buildString {
            append("${column.tableName}.${column.name} IN(")
            append(list.joinToString(", "))
            append(")")
        }
    }
}

class NotInList<T>(private val column: Column<T>, private val list: List<T>) : WhereOp() {
    override fun toSql(): String {
        if (list.isEmpty()) throw IllegalArgumentException("list is empty")
        return buildString {
            append("${column.tableName}.${column.name} NOT IN(")
            append(list.joinToString(", "))
            append(")")
        }
    }
}

class Or(private val op1: WhereOp, private val op2: WhereOp) : WhereOp() {
    override fun toSql(): String = buildString {
        append(op1.toSql())
        append(" OR ")
        append(op2.toSql())
    }
}

class And(private val op1: WhereOp, private val op2: WhereOp) : WhereOp() {
    override fun toSql(): String = buildString {
        append(op1.toSql())
        append(" AND ")
        append(op2.toSql())
    }
}
