package com.kiyosuke.sqlitlin.db.column

sealed class Column<T> {

    abstract val name: String
    abstract val tableName: String
    var nullable: Boolean = false
    var primaryKey: Boolean = false
    var unique: Boolean = false
    var default: T? = null

    data class Text(override val name: String, override val tableName: String) : Column<String>()

    data class Integer(override val name: String, override val tableName: String) : Column<Int>() {
        var autoIncrement = false
    }

    data class Real(override val name: String, override val tableName: String) : Column<Double>()

    data class Blob(override val name: String, override val tableName: String) : Column<ByteArray>()

}