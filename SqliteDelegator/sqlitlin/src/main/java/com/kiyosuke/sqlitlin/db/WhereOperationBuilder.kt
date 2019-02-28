package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.column.Column

object WhereOperationBuilder {

    infix fun <T> Column<T>.eq(data: T): Op =
        if (this is Column.Text) Eq<String>(this, "'$data'") else Eq(this, data)

    infix fun <T> Column<T>.neq(data: T): Op =
        if (this is Column.Text) Neq<String>(this, "'$data'") else Neq(this, data)

    infix fun <T> Column<T>.less(data: T): Op =
        if (this is Column.Text) Less<String>(this, "'$data'") else Less(this, data)

    infix fun <T> Column<T>.lessEq(data: T): Op =
        if (this is Column.Text) LessEq<String>(this, "'$data'") else LessEq(this, data)

    infix fun <T> Column<T>.greater(data: T): Op =
        if (this is Column.Text) Greater<String>(this, "'$data'") else Greater(this, data)

    infix fun <T> Column<T>.greaterEq(data: T): Op =
        if (this is Column.Text) GreaterEq<String>(this, "'$data'") else GreaterEq(this, data)

    fun Column.Text.like(text: String, escape: String? = null): Op = Like(this, text, escape)

    infix fun <T> Column<T>.between(between: Pair<T, T>): Op =
        if (this is Column.Text) Between<String>(this, "'${between.first}'" to "'${between.second}'") else Between(this, between)

    infix fun <T> Column<T>.inList(list: List<T>): Op =
        if (this is Column.Text) InList<String>(this, list.map { "'$it'" }) else InList(this, list)

    infix fun <T> Column<T>.notInList(list: List<T>): Op =
        if (this is Column.Text) NotInList<String>(this, list.map { "'$it'" }) else NotInList(this, list)

    infix fun <T> max(column: Column<T>): Op = OpMax(column)

    infix fun <T> min(column: Column<T>): Op = OpMin(column)

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

class Like(private val column: Column.Text, private val data: String, private val escape: String? = null) : Op() {
    override fun toSql(): String = buildString {
        append("${column.tableName}.${column.name}")
        append(" LIKE ")
        append("'$data'")
        if (escape != null) append(" ESCAPE '$escape'")
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

class OpMax<T>(private val column: Column<T>) : Op() {
    override fun toSql(): String {
        return buildString {
            append("MAX(")
            append("${column.tableName}.${column.name}")
            append(")")
        }
    }
}

class OpMin<T>(private val column: Column<T>) : Op() {
    override fun toSql(): String {
        return buildString {
            append("MIN(")
            append("${column.tableName}.${column.name}")
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
