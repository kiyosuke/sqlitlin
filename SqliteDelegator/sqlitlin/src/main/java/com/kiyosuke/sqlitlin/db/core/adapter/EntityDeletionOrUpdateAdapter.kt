package com.kiyosuke.sqlitlin.db.core.adapter

import android.database.sqlite.SQLiteStatement
import com.kiyosuke.sqlitlin.db.core.SupportDatabase

/**
 * 全件削除または、全カラムに更新をかける際に利用
 */
abstract class EntityDeletionOrUpdateAdapter<T>(database: SupportDatabase) : SharedSQLiteStatement(database) {

    protected abstract fun bind(stmt: SQLiteStatement, entity: T)

    fun handle(entity: T): Int {
        val stmt = acquire()
        try {
            bind(stmt, entity)
            return stmt.executeUpdateDelete()
        } finally {
            release(stmt)
        }
    }

    fun handleMultiple(entities: Iterable<T>): Int {
        val stmt = acquire()
        try {
            var total = 0
            entities.forEach { entity ->
                bind(stmt, entity)
                total += stmt.executeUpdateDelete()
            }
            return total
        } finally {
            release(stmt)
        }
    }

    fun handleMultiple(entities: Array<T>): Int {
        val stmt = acquire()
        try {
            var total = 0
            entities.forEach { entity ->
                bind(stmt, entity)
                total += stmt.executeUpdateDelete()
            }
            return total
        } finally {
            release(stmt)
        }
    }
}