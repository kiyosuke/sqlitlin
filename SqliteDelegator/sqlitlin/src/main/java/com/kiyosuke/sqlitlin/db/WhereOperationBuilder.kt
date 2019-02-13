package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.column.Column

object WhereOperationBuilder {

    infix fun <T> Column<T>.eq(data: T): Op =
        if (this is Column.Text) Eq<String>(this, "'$data'") else Eq(this, data)

    infix fun <T> Column<T>.neq(data: T): Op =
        if (this is Column.Text) Neq<String>(this, "'$data'") else Neq(this, data)

    infix fun <T> Column<T>.less(data: T): Op = Less(this, data)

    infix fun <T> Column<T>.lessEq(data: T): Op = LessEq(this, data)

    infix fun <T> Column<T>.greater(data: T): Op = Greater(this, data)

    infix fun <T> Column<T>.greaterEq(data: T): Op = GreaterEq(this, data)

    infix fun Column.Text.like(text: String): Op = Like(this, text)

    infix fun <T> Column<T>.between(between: Pair<T, T>): Op = Between(this, between)

    infix fun <T> Column<T>.inList(list: List<T>): Op = InList(this, list)

    infix fun <T> Column<T>.notInList(list: List<T>): Op = NotInList(this, list)

    infix fun Op.or(operation: Op): Op = Or(this, operation)

    infix fun Op.and(operation: Op): Op = And(this, operation)

}

class Eq<T>(private val column: Column<T>, private val data: T) : Op() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} = $data")
    }
}

class Neq<T>(private val column: Column<T>, private val data: T) : Op() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} != $data")
    }
}

class Less<T>(private val column: Column<T>, private val data: T) : Op() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} < $data")
    }
}

class LessEq<T>(private val column: Column<T>, private val data: T) : Op() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} <= $data")
    }
}

class Greater<T>(private val column: Column<T>, private val data: T) : Op() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} > $data")
    }
}

class GreaterEq<T>(private val column: Column<T>, private val data: T) : Op() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} >= $data")
    }
}

class Like(private val column: Column.Text, private val data: String) : Op() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} LIKE '$data'")
    }
}

class Between<T>(private val column: Column<T>, private val data: Pair<T, T>) : Op() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name} BETWEEN ${data.first} AND ${data.second}")
    }
}

class InList<T>(private val column: Column<T>, private val list: List<T>) : Op() {
    override fun toSql(): String {
        if (list.isEmpty()) throw IllegalArgumentException("list is empty")
        return buildString {
            append("${column.tableName}.${column.name} IN(")
            append(list.joinToString(", "))
            append(")")
        }
    }
}

class NotInList<T>(private val column: Column<T>, private val list: List<T>) : Op() {
    override fun toSql(): String {
        if (list.isEmpty()) throw IllegalArgumentException("list is empty")
        return buildString {
            append("${column.tableName}.${column.name} NOT IN(")
            append(list.joinToString(", "))
            append(")")
        }
    }
}

class Or(private val op1: Op, private val op2: Op) : Op() {
    override fun toSql(): String = buildString {
        append(op1.toSql())
        append(" OR ")
        append(op2.toSql())
    }
}

class And(private val op1: Op, private val op2: Op) : Op() {
    override fun toSql(): String = buildString {
        append(op1.toSql())
        append(" AND ")
        append(op2.toSql())
    }
}
