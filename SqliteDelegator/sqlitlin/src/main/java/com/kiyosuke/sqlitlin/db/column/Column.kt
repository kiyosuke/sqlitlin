package com.kiyosuke.sqlitlin.db.column

sealed class Column<T> {

    abstract val name: String
    var nullable: Boolean = false
    var primaryKey: Boolean = false
    var default: T? = null

    data class Text(override val name: String) : Column<String>()

    data class Integer(override val name: String) : Column<Int>() {
        var autoIncrement = false
    }

    data class Real(override val name: String) : Column<Double>()

    class Blob(override val name: String) : Column<ByteArray>()

}