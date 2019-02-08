package com.kiyosuke.sqlitlin.db.core.adapter

import android.database.sqlite.SQLiteStatement
import com.kiyosuke.sqlitlin.db.core.SupportDatabase
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 細かい単位での削除や更新に使用
 */
abstract class SharedSQLiteStatement(private val database: SupportDatabase) {
    private val lock = AtomicBoolean(false)

    private var stmt: SQLiteStatement? = null

    protected abstract fun createQuery(): String

    private fun assertMainThread() {
        database.assertMainThread()
    }

    private fun createNewStatement(): SQLiteStatement {
        val query = createQuery()
        return database.compileStatement(query)
    }

    private fun getStmt(canUseCached: Boolean): SQLiteStatement {
        val stmt: SQLiteStatement
        if (canUseCached) {
            if (this.stmt == null) {
                this.stmt = createNewStatement()
            }
            stmt = requireNotNull(this.stmt) {
                "stmt == null"
            }
        } else {
            stmt = createNewStatement()
        }
        return stmt
    }

    fun acquire(): SQLiteStatement {
        assertMainThread()
        return getStmt(lock.compareAndSet(false, true))
    }

    fun release(statement: SQLiteStatement) {
        if (statement == this.stmt) {
            lock.set(false)
        }
    }
}