package com.kiyosuke.sqlitlin.db

import android.database.Cursor
import android.database.sqlite.SQLiteStatement
import com.kiyosuke.sqlitlin.db.column.Column
import com.kiyosuke.sqlitlin.db.core.SupportDatabase
import com.kiyosuke.sqlitlin.db.core.adapter.EntityDeletionOrUpdateAdapter
import com.kiyosuke.sqlitlin.db.core.adapter.EntityInsertionAdapter
import com.kiyosuke.sqlitlin.db.core.adapter.SharedSQLiteStatement
import com.kiyosuke.sqlitlin.db.core.adapter.bind
import com.kiyosuke.sqlitlin.db.core.common.IndexCachedCursor
import com.kiyosuke.sqlitlin.db.core.common.wrap
import com.kiyosuke.sqlitlin.db.core.exception.EmptyResultSetException
import com.kiyosuke.sqlitlin.db.table.Table

abstract class Dao<T : Table>(private val database: SupportDatabase) {
    abstract val table: T

    private val insertAdapter by lazy {
        object : EntityInsertionAdapter<ColumnMap>(database) {
            override fun createQuery(): String = table.insertSql

            override fun bind(stmt: SQLiteStatement, entity: ColumnMap) {
                table.columns.forEachIndexed { index, column ->
                    stmt.bind(index + 1, entity[column])
                }
            }
        }
    }

    private val updationAdapter by lazy {
        object : EntityDeletionOrUpdateAdapter<ColumnMap>(database) {
            override fun createQuery(): String = table.updateSql

            override fun bind(stmt: SQLiteStatement, entity: ColumnMap) {
                var index = 0
                table.columns.forEach { column ->
                    ++index
                    stmt.bind(index, entity[column])
                }
                table.columns.filter(Column<*>::primaryKey).forEach { column ->
                    ++index
                    stmt.bind(index, entity[column])
                }
            }
        }
    }

    private val deletionAdapter by lazy {
        object : EntityDeletionOrUpdateAdapter<ColumnMap>(database) {
            override fun createQuery(): String = table.deleteSql

            override fun bind(stmt: SQLiteStatement, entity: ColumnMap) {
                table.columns.filter(Column<*>::primaryKey).forEachIndexed { index, column ->
                    stmt.bind(index + 1, entity[column])
                }
            }
        }
    }

    private val deletionSqliteSequenceAdapter by lazy {
        object : SharedSQLiteStatement(database) {
            override fun createQuery(): String = "DELETE FROM sqlite_sequence WHERE name = '${table.tableName}'"
        }
    }

    private val allDeletionAdapter by lazy {
        object : SharedSQLiteStatement(database) {
            override fun createQuery(): String = "DELETE FROM ${table.tableName}"
        }
    }

    fun select(vararg columns: Column<*> = emptyArray(), query: Select<T>.() -> Unit): List<ColumnMap> {
        val sql = Select(if (columns.isEmpty()) table.columns else listOf(*columns), table).apply(query).toSql()
        val result = database.query(sql).toResultMaps(table)
        if (result.isEmpty()) throw EmptyResultSetException("Query returned empty result set: $sql")
        return result
    }

    fun Join.select(
        vararg columns: Column<*> = emptyArray(),
        query: Select<T>.() -> Unit
    ): List<ColumnMap> {
        if (columns.isEmpty()) throw IllegalArgumentException("columns is empty.")
        val sql = Select(listOf(*columns), table, this@select).apply(query).toSql()
        val result = database.query(sql).toResultMaps(listOf(*columns))
        if (result.isEmpty()) throw EmptyResultSetException("Query returned empty result set: $sql")
        return result
    }

    fun selectAll(): List<ColumnMap> {
        val sql = Select(table.columns, table).toSql()
        val result = database.query(sql).toResultMaps(table)
        if (result.isEmpty()) throw EmptyResultSetException("Query returned empty result set: $sql")
        return result
    }

    fun <JT : Table> innerJoin(joinTable: JT, onColumn: Column<*>, joinColumn: Column<*>): InnerJoin {
        return InnerJoin(joinTable, onColumn, joinColumn)
    }

    fun count(column: Column<*>, query: (Count<T>.() -> Unit)? = null): List<Int> {
        val sql = Count(listOf(column), table).apply { query?.invoke(this) }.toSql()
        return database.query(sql).use { c ->
            val result = mutableListOf<Int>()
            while (c.moveToNext()) {
                result.add(c.getInt(0))
            }
            result
        }
    }

    fun countAll(): Int {
        val sql = Count(emptyList(), table).toSql()
        return database.query(sql).use { c ->
            c.moveToFirst()
            c.getInt(0)
        }
    }

    fun max(column: Column<*>, query: (Max<T>.() -> Unit)? = null): Int {
        val sql = Max(listOf(column), table).apply { query?.invoke(this) }.toSql()
        return database.query(sql).wrap().use { c ->
            c.moveToFirst()
            c.getInt(column.cursorKey)
        }
    }

    fun min(column: Column<*>, query: (Min<T>.() -> Unit)? = null): Int {
        val sql = Min(listOf(column), table).apply { query?.invoke(this) }.toSql()
        return database.query(sql).wrap().use { c ->
            c.moveToFirst()
            c.getInt(column.cursorKey)
        }
    }

    fun insert(item: ColumnMap) {
        database.runInTransaction {
            insertAdapter.insert(item)
        }
    }

    fun insert(items: List<ColumnMap>) {
        database.runInTransaction {
            insertAdapter.insert(items)
        }
    }

    fun update(item: ColumnMap) {
        database.runInTransaction {
            updationAdapter.handle(item)
        }
    }

    fun update(items: List<ColumnMap>) {
        database.runInTransaction {
            updationAdapter.handleMultiple(items)
        }
    }

    fun delete(item: ColumnMap) = deletionAdapter.handle(item)

    fun deleteAll() {
        val stmt = allDeletionAdapter.acquire()
        database.beginTransaction()
        try {
            stmt.executeUpdateDelete()
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
            allDeletionAdapter.release(stmt)
        }
    }

    fun truncate() {
        val stmt = allDeletionAdapter.acquire()
        val sqliteSequenceStmt = deletionSqliteSequenceAdapter.acquire()
        database.beginTransaction()
        try {
            stmt.executeUpdateDelete()
            sqliteSequenceStmt.executeUpdateDelete()
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
            allDeletionAdapter.release(stmt)
            deletionSqliteSequenceAdapter.release(sqliteSequenceStmt)
        }
    }

    private fun Cursor.toResultMaps(table: Table): List<ColumnMap> {
        return toResultMaps(table.columns)
    }

    private fun Cursor.toResultMaps(columns: List<Column<*>>): List<ColumnMap> =
        IndexCachedCursor(this).use { indexCachedCursor ->
            val result: MutableList<ColumnMap> = mutableListOf()
            while (indexCachedCursor.moveToNext()) {
                val resultMap = ColumnMap()
                columns.forEach {
                    when (it) {
                        is Column.Text -> resultMap[it] = indexCachedCursor.getStringOrNull(it.cursorKey)
                        is Column.Integer -> resultMap[it] = indexCachedCursor.getIntOrNull(it.cursorKey)
                        is Column.Real -> resultMap[it] = indexCachedCursor.getDoubleOrNull(it.cursorKey)
                        is Column.Blob -> resultMap[it] = indexCachedCursor.getBlobOrNull(it.cursorKey)
                    }
                }
                result.add(resultMap)
            }
            return@use result
        }
}