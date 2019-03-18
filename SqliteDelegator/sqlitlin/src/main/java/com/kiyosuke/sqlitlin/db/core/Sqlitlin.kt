package com.kiyosuke.sqlitlin.db.core

import android.content.Context
import android.database.Cursor
import android.os.Looper
import android.util.Log
import com.kiyosuke.sqlitlin.db.Dao
import com.kiyosuke.sqlitlin.db.core.normal.NormalSQLiteOpenHelperFactory
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteDatabase
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteOpenHelper
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteStatement
import com.kiyosuke.sqlitlin.db.table.Table

class Sqlitlin private constructor(
    context: Context,
    name: String,
    version: Int,
    factory: SupportSQLiteOpenHelper.Factory,
    private val tables: List<Table>,
    private val migrations: Migrations,
    private val isMainThreadAssertion: Boolean
) {

    private var cachedDao: LinkedHashMap<Table, Dao<*>> = linkedMapOf()

    private val openHelper: SupportSQLiteOpenHelper =
        factory.create(
            SupportSQLiteOpenHelper.Configure(context, name, object : SupportSQLiteOpenHelper.Callback(version) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    createAllTables(db)
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    migrations.findMigrations(oldVersion, newVersion).forEach { migration ->
                        migration.migration(db)
                    }
                }

                override fun onDowngrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    // do nothing.
                }
            })
        )

    private fun createAllTables(db: SupportSQLiteDatabase) {
        tables.forEach { table ->
            db.execSQL(table.createSql)
        }
    }

    fun compileStatement(sql: String): SupportSQLiteStatement {
        return openHelper.getWritableDatabase().compileStatement(sql)
    }

    fun query(query: String): Cursor {
        return query(query, emptyArray())
    }

    fun query(query: String, bindArgs: Array<Any?>): Cursor {
        assertMainThread()
        return openHelper.getWritableDatabase().query(query, bindArgs)
    }

    fun beginTransaction() {
        assertMainThread()
        val database = openHelper.getWritableDatabase()
        database.beginTransaction()
    }

    fun endTransaction() {
        openHelper.getWritableDatabase().endTransaction()
    }

    fun setTransactionSuccessful() {
        openHelper.getWritableDatabase().setTransactionSuccessful()
    }

    fun inTransaction(): Boolean {
        return openHelper.getWritableDatabase().inTransaction()
    }

    fun <T : Table, DAO : Dao<T>> createDao(table: T): DAO {
        fun create(): Dao<T> {
            return object : Dao<T>(this) {
                override val table: T
                    get() = table
            }
        }

        val dao = cachedDao.getOrPut(table) {
            create()
        }
        @Suppress("UNCHECKED_CAST") return dao as DAO
    }

    /**
     * bodyに受け取った処理をトランザクション内で実行する
     */
    inline fun runInTransaction(body: () -> Unit) {
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
    inline fun <V> runInTransaction(body: () -> V): V {
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

    fun assertMainThread() {
        if (!isMainThreadAssertion) return
        if (Looper.myLooper() == Looper.getMainLooper()) throw IllegalStateException("current thread is MainThread.")
    }

    companion object {
        fun builder(context: Context, name: String, version: Int): Builder {
            return Builder(context, name, version)
        }
    }

    class Migrations {
        private val migrations: MutableMap<Int, MutableMap<Int, Migration>> = mutableMapOf()

        /**
         * 追加するマイグレーション処理は必ずDBバージョンを一つずつ上げたものを作成してください。
         * 1から3へのマイグレーション処理など、バージョンが飛ぶようなマイグレーション処理は追加しないでください。
         * 1から3へ上げる場合でも、マイグレーション処理を飛ばさずに1から2のマイグレーション、2から3へのマイグレーション処理を
         * 作成して下さい。
         * 正しい例）addMigrations(Migration(1, 2), Migration(2, 3))
         * 不正な例）addMigrations(Migration(1, 2), Migration(1, 3))
         */
        fun addMigrations(vararg migrations: Migration) {
            migrations.forEach(this::addMigration)
        }

        private fun addMigration(migration: Migration) {
            val start = migration.startVersion
            val end = migration.endVersion
            var target = migrations[start]
            if (target == null) {
                target = mutableMapOf()
                migrations[start] = target
            }
            val existing = target[end]
            if (existing != null) {
                Log.w("Sqlitlin", "Overriding migration $existing with $migration")
            }
            target[end] = migration
        }

        fun findMigrations(startVersion: Int, endVersion: Int): List<Migration> {
            migrations.forEach { (_, migrations) ->
                migrations.forEach { (_, migration) ->
                    Log.d("Sqlitlin", "start: ${migration.startVersion}, end: ${migration.endVersion}")
                }
            }

            val migrations: MutableList<Migration> = mutableListOf()
            (startVersion until endVersion).forEach { version ->
                Log.d("Sqlitlin", "findMigration() version: $version")
                this.migrations[version]?.values?.filter { it.endVersion <= endVersion }
                    ?.sortedBy(Migration::endVersion)?.forEach {
                        // MigrationのstartVersionとendVersionが一致する場合はそのMigration処理だけ返却する
                        if (it.startVersion == startVersion && it.endVersion == endVersion) return listOf(it)
                        migrations.add(it)
                    }
            }
            return migrations
        }
    }

    class Builder internal constructor(
        private val context: Context,
        private val name: String,
        private val version: Int
    ) {
        private var factory: SupportSQLiteOpenHelper.Factory? = null
        private var tables: MutableList<Table> = mutableListOf()
        private var migrations: Migrations = Migrations()
        private var isMainThreadAssertion: Boolean = true

        fun addTables(vararg table: Table) = apply {
            table.forEach(this::addTable)
        }

        private fun addTable(table: Table) {
            tables.add(table)
        }

        fun mainThreadAssert(isEnabled: Boolean) = apply {
            this.isMainThreadAssertion = isEnabled
        }

        fun addMigrations(vararg migrations: Migration) = apply {
            this.migrations.addMigrations(*migrations)
        }

        fun build(): Sqlitlin {
            val factory = factory ?: NormalSQLiteOpenHelperFactory()
            return Sqlitlin(context, name, version, factory, tables, migrations, isMainThreadAssertion)
        }
    }
}