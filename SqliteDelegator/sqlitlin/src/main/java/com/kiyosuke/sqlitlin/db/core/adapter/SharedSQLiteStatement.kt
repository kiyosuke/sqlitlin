package com.kiyosuke.sqlitlin.db.core.adapter

import com.kiyosuke.sqlitlin.db.core.Sqlitlin
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteStatement
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 細かい単位での削除や更新に使用
 */
abstract class SharedSQLiteStatement(private val database: Sqlitlin) {
    private val lock = AtomicBoolean(false)

    private var stmt: SupportSQLiteStatement? = null

    protected abstract fun createQuery(): String

    private fun assertMainThread() {
        database.assertMainThread()
    }

    private fun createNewStatement(): SupportSQLiteStatement {
        val query = createQuery()
        return database.compileStatement(query)
    }

    private fun getStmt(canUseCached: Boolean): SupportSQLiteStatement {
        val stmt: SupportSQLiteStatement
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

    fun acquire(): SupportSQLiteStatement {
        assertMainThread()
        return getStmt(lock.compareAndSet(false, true))
    }

    fun release(statement: SupportSQLiteStatement) {
        if (statement == this.stmt) {
            lock.set(false)
        }
    }
}