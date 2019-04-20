package com.kiyosuke.sqlitlin.db

import android.database.Cursor
import com.kiyosuke.sqlitlin.db.column.Column
import com.kiyosuke.sqlitlin.db.core.common.wrap
import java.io.Closeable

class ResultSet(cursor: Cursor) : Closeable {
    private val cursor = cursor.wrap()

    fun getCount(): Int = cursor.count

    fun move(offset: Int): Boolean = cursor.move(offset)

    fun moveToPosition(position: Int): Boolean = cursor.moveToPosition(position)

    fun moveToFirst(): Boolean = cursor.moveToFirst()

    fun moveToLast(): Boolean = cursor.moveToLast()

    fun moveToNext(): Boolean = cursor.moveToNext()

    fun moveToPrevious(): Boolean = cursor.moveToPrevious()

    fun isFirst(): Boolean = cursor.isFirst

    fun isLast(): Boolean = cursor.isLast

    fun isBeforeFirst(): Boolean = cursor.isBeforeFirst

    fun isAfterLast(): Boolean = cursor.isAfterLast

    inline fun <reified T : Any> get(column: Column<T>): T {
        val result: Any = when (column) {
            is Column.Text -> getString(column)
            is Column.Integer -> getInt(column)
            is Column.Long -> getLong(column)
            is Column.Real -> getReal(column)
            is Column.Blob -> getBlob(column)
        }
        return result as T
    }

    inline fun <reified T> getOpt(column: Column<T>): T? {
        val result: Any? = when (column) {
            is Column.Text -> getOptString(column)
            is Column.Integer -> getOptInt(column)
            is Column.Long -> getOptLong(column)
            is Column.Real -> getOptReal(column)
            is Column.Blob -> getOptBlob(column)
        }
        return result as? T
    }

    fun getString(column: Column<String>): String {
        return cursor.getString(column.cursorKey)
    }

    fun getInt(column: Column<Int>): Int {
        return cursor.getInt(column.cursorKey)
    }

    fun getLong(column: Column<Long>): Long {
        return cursor.getLong(column.cursorKey)
    }

    fun getReal(column: Column<Double>): Double {
        return cursor.getDouble(column.cursorKey)
    }

    fun getBlob(column: Column<ByteArray>): ByteArray {
        return cursor.getBlob(column.cursorKey)
    }

    fun getOptString(column: Column<String>): String? {
        return cursor.getStringOrNull(column.cursorKey)
    }

    fun getOptInt(column: Column<Int>): Int? {
        return cursor.getIntOrNull(column.cursorKey)
    }

    fun getOptLong(column: Column<Long>): Long? {
        return cursor.getLongOrNull(column.cursorKey)
    }

    fun getOptReal(column: Column<Double>): Double? {
        return cursor.getDoubleOrNull(column.cursorKey)
    }

    fun getOptBlob(column: Column<ByteArray>): ByteArray? {
        return cursor.getBlobOrNull(column.cursorKey)
    }

    inline fun forEach(block: (ResultSet) -> Unit) {
        this.use {
            while (this.moveToNext()) {
                block(this)
            }
        }
    }

    inline fun <T> getList(block: (ResultSet) -> T): List<T> {
        val result: MutableList<T> = mutableListOf()
        forEach {
            result.add(block(it))
        }
        return result
    }

    inline fun <T> get(block: (ResultSet) -> T): T = this.use {
        this.moveToFirst()
        return@use block(this)
    }

    override fun close() {
        cursor.close()
    }

    fun isClosed(): Boolean = cursor.isClosed
}