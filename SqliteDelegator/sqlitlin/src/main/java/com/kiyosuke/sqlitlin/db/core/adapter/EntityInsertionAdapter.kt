package com.kiyosuke.sqlitlin.db.core.adapter

import android.database.sqlite.SQLiteStatement
import com.kiyosuke.sqlitlin.db.core.SupportDatabase

/**
 * インサート時に利用
 */
@Suppress("unused")
abstract class EntityInsertionAdapter<T>(database: SupportDatabase) : SharedSQLiteStatement(database) {

    protected abstract fun bind(stmt: SQLiteStatement, entity: T)

    fun insert(entity: T) {
        val stmt = acquire()
        try {
            bind(stmt, entity)
            stmt.executeInsert()
        } finally {
            release(stmt)
        }
    }

    fun insert(entities: Array<T>) {
        val stmt = acquire()
        try {
            entities.forEach { entity ->
                bind(stmt, entity)
                stmt.executeInsert()
            }
        } finally {
            release(stmt)
        }
    }

    fun insert(entities: Iterable<T>) {
        val stmt = acquire()
        try {
            entities.forEach { entity ->
                bind(stmt, entity)
                stmt.executeInsert()
            }
        } finally {
            release(stmt)
        }
    }

    fun insertAndReturnId(entity: T): Long {
        val stmt = acquire()
        try {
            bind(stmt, entity)
            return stmt.executeInsert()
        } finally {
            release(stmt)
        }
    }

    fun insertAndReturnIdsArray(entities: Collection<T>): LongArray {
        val stmt = acquire()
        try {
            val result = LongArray(entities.size)
            var index = 0
            entities.forEach { entity ->
                bind(stmt, entity)
                result[index] = stmt.executeInsert()
                index++
            }
            return result
        } finally {
            release(stmt)
        }
    }

    fun insertAndReturnIdsArray(entities: Array<T>): LongArray {
        val stmt = acquire()
        try {
            val result = LongArray(entities.size)
            var index = 0
            entities.forEach { entity ->
                bind(stmt, entity)
                result[index] = stmt.executeInsert()
                index++
            }
            return result
        } finally {
            release(stmt)
        }
    }
}