package com.kiyosuke.sqlitlin.db.table

import android.util.Log
import com.kiyosuke.sqlitlin.db.column.Column

open class Table(name: String = "") {
    open val tableName = if (name.isNotEmpty()) name else this.javaClass.simpleName.removeSuffix("Table")

    val columns = ArrayList<Column<*>>()

    private fun <T : Column<*>> registerColumn(column: T) = column.apply {
        columns.add(column)
    }

    private fun <T : Column<*>> replaceColumn(oldColumn: T, newColumn: T) = newColumn.apply {
        columns.remove(oldColumn)
        columns.add(newColumn)
    }

    fun text(name: String, default: String? = null) =
        registerColumn(Column.Text(name, tableName).apply { this.default = default })

    fun integer(name: String, default: Int? = null) =
        registerColumn(Column.Integer(name, tableName).apply { this.default = default })

    fun real(name: String, default: Double? = null) =
        registerColumn(Column.Real(name, tableName).apply { this.default = default })

    fun blob(name: String, default: ByteArray? = null) =
        registerColumn(Column.Blob(name, tableName).apply { this.default = default })

    fun <T : Column<*>> T.primaryKey() = replaceColumn(this, this.apply {
        this.primaryKey = true
    })

    fun <T : Column<*>> T.nullable() = replaceColumn(this, this.apply {
        this.nullable = true
    })

    fun Column.Integer.autoIncrement() = replaceColumn(this, this.apply {
        this.autoIncrement = true
    })

    val createSql: String
        get() = createStatement()

    val insertSql: String
        get() = createInsertStatement()

    val updateSql: String
        get() = createUpdateStatement()

    val deleteSql: String
        get() = createDeleteStatement()

    private fun createStatement(): String {
        val sql = buildString {
            append("CREATE TABLE IF NOT EXISTS $tableName(")
            columns.forEachIndexed { index, c ->
                when (c) {
                    is Column.Text -> {
                        append("${c.name} TEXT")
                        if (!c.nullable) append(" NOT NULL")
                        if (c.primaryKey) append(" PRIMARY KEY")
                        if (c.default != null) append(" DEFAULT ${c.default}")
                    }

                    is Column.Integer -> {
                        append("${c.name} INTEGER")
                        if (!c.nullable) append(" NOT NULL")
                        if (c.primaryKey) append(" PRIMARY KEY")
                        if (c.autoIncrement) append(" AUTOINCREMENT")
                        if (c.default != null) append(" DEFAULT ${c.default}")
                    }

                    is Column.Real -> {
                        append("${c.name} REAL")
                        if (!c.nullable) append(" NOT NULL")
                        if (c.primaryKey) append(" PRIMARY KEY")
                        if (c.default != null) append(" DEFAULT ${c.default}")
                    }

                    is Column.Blob -> {
                        append("${c.name} BLOB")
                        if (!c.nullable) append(" NOT NULL")
                        if (c.primaryKey) append(" PRIMARY KEY")
                        if (c.default != null) append(" DEFAULT ${c.default}")
                    }
                }
                if (index != columns.lastIndex) append(",")
            }
            append(")")
        }
        println("createStatement: $sql")
        return sql
    }

    private fun createInsertStatement(): String {
        val sql = buildString {
            append("INSERT OR REPLACE INTO $tableName")
            append("(")
            append(columns.map(Column<*>::name).joinToString(","))
            append(")")
            append(" VALUES ")
            append("(")
            append(columns.joinToString(",") { "?" })
            append(")")
        }
        Log.d("Table", "createInsertStatement: $sql")
        return sql
    }

    private fun createUpdateStatement(): String {
        val sql = buildString {
            append("UPDATE OR ABORT $tableName SET ")
            append(columns.joinToString(",") { column -> "${column.name} = ?" })
            append(" WHERE ")
            append(columns.filter(Column<*>::primaryKey).joinToString(" AND") { column -> "${column.name} = ?" })
        }
        Log.d("Table", "createUpdateStatement: $sql")
        return sql
    }

    private fun createDeleteStatement(): String {
        val sql = buildString {
            append("DELETE FROM $tableName")
            append(" WHERE ")
            append(columns.filter(Column<*>::primaryKey).joinToString(" AND") { column -> "${column.name} = ?" })
        }
        Log.d("Table", "createDeleteStatement: $sql")
        return sql
    }

}