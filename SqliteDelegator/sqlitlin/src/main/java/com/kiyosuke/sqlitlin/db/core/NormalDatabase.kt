package com.kiyosuke.sqlitlin.db.core

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import android.os.Looper

/**
 * 暗号化したDB操作クラス
 */
abstract class NormalDatabase(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version), SupportDatabase {


    override fun onCreate(db: SQLiteDatabase) {
        createTable(db)
    }

    /**
     * Create文実行
     */
    abstract fun createTable(db: SQLiteDatabase)

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // FIXME: データベースの変更があればここにマイグレーション処理を記述
    }

    /**
     * SQLからSQLiteStatement生成
     */
    override fun compileStatement(sql: String): SQLiteStatement {
        return writableDatabase.compileStatement(sql)
    }

    /**
     * SQL実行
     */
    override fun query(query: String): Cursor {
        return query(query, emptyArray())
    }

    /**
     * SQL実行
     */
    override fun query(query: String, bindArgs: Array<Any?>): Cursor {
        assertMainThread()
        return writableDatabase.rawQuery(query, bindArgs.map { it.toString() }.toTypedArray())
    }

    /**
     * メインスレッドからアクセスされたら例外を投げる
     */
    override fun assertMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) throw IllegalStateException("current thread is MainThread.")
    }

    /**
     * トランザクション開始
     */
    override fun beginTransaction() {
        assertMainThread()
        val database = writableDatabase
        database.beginTransaction()
    }

    /**
     * トランザクション終了
     */
    override fun endTransaction() {
        writableDatabase.endTransaction()
    }

    /**
     * トランザクション成功
     */
    override fun setTransactionSuccessful() {
        writableDatabase.setTransactionSuccessful()
    }

    /**
     * トランザクション中か
     */
    override fun inTransaction() = writableDatabase.inTransaction()

    /**
     * bodyに受け取った処理をトランザクション内で実行する
     */
    override fun runInTransaction(body: () -> Unit) {
        beginTransaction()
        try {
            body()
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }

    /**
     * bodyに受け取った処理をトランザクション内で実行する
     * bodyの結果を返却する
     */
    override fun <V> runInTransaction(body: () -> V): V {
        beginTransaction()
        try {
            val result = body()
            setTransactionSuccessful()
            return result
        } catch (e: RuntimeException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("Exception in transaction", e)
        } finally {
            endTransaction()
        }
    }

    companion object {
        private const val DB_PASSWORD = "gr295qkpx6pfHoympbXjsg=="
    }
}