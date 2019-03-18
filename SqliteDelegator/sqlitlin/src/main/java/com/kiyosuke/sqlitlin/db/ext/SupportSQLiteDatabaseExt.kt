package com.kiyosuke.sqlitlin.db.ext

import android.util.Log
import com.kiyosuke.sqlitlin.db.column.Column
import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteDatabase
import com.kiyosuke.sqlitlin.db.table.Table

/**
 * ALTER TABLE ${テーブル名} ADD COLUMN ${カラム名} を実行する
 * データベースにカラムを追加したマイグレーション処理を行う際に呼び出してください
 */
fun SupportSQLiteDatabase.addColumn(c: Column<*>) {
    checkAlterPredicate(c)
    val alterSql = buildString {
        append("ALTER TABLE ${c.tableName} ADD COLUMN ")
        when (c) {
            is Column.Text -> {
                append("${c.name} INTEGER")
                if (!c.nullable) append(" NOT NULL")
                if (c.default != null) append(" DEFAULT ${c.default}")
            }
            is Column.Integer -> {
                append("${c.name} INTEGER")
                if (!c.nullable) append(" NOT NULL")
                if (c.default != null) append(" DEFAULT ${c.default}")
            }
            is Column.Long -> {
                append("${c.name} INTEGER")
                if (!c.nullable) append(" NOT NULL")
                if (c.default != null) append(" DEFAULT ${c.default}")
            }
            is Column.Real -> {
                append("${c.name} REAL")
                if (!c.nullable) append(" NOT NULL")
                if (c.default != null) append(" DEFAULT ${c.default}")
            }
            is Column.Blob -> {
                append("${c.name} BLOB")
                if (!c.nullable) append(" NOT NULL")
                if (c.default != null) append(" DEFAULT ${c.default}")
            }
        }
    }
    Log.d("SupportSQLiteDatabase", "alterSql: $alterSql")
    this.execSQL(alterSql)
}

/**
 * ALTER TABLE ADD COLUMNで新しくカラムを追加する場合、以下の条件を満たしている必要があります
 * 1. PRIMARY KEYやUNIQUE制約は設定できない
 * 2. DEFAULT制約を設定するときは、CURRENT_TIME/CURRENT_DATE/CURRENT_TIMESTAMPは指定できない
 * 3. NOT NULL制約を設定するときは、NULL以外のデフォルト値の設定が必要
 *
 * 1と3の条件をチェックして満たしていない場合は例外を投げます。
 * 2の条件に関しては、CURRENT_TIME/CURRENT_DATE/CURRENT_TIMESTAMPを設定しているAndroidアプリを見たことがない
 * また、ライブラリの作りからそれらをデフォルト値としてセットできないため2のチェックは行っていない
 */
private fun checkAlterPredicate(c: Column<*>) {
    if (c.primaryKey || c.unique) throw IllegalArgumentException("When adding a column with alter table, you can not attach PRIMARY KEY constraints and UNIQUE constraints.")
    if (!c.nullable && c.default == null) throw IllegalArgumentException("When adding a column with alter table, you need to set a non-null default value when adding a NOT NULL constraint.")
}

/**
 * テーブルをドロップします
 */
fun SupportSQLiteDatabase.dropTable(vararg tables: Table) {
    tables.forEach { table ->
        this.execSQL(table.dropSql)
    }
}

/**
 * テーブルをテーブル名を指定してドロップします
 */
fun SupportSQLiteDatabase.dropTable(vararg tableNames: String) {
    tableNames.forEach { tableName ->
        this.execSQL("DROP TABLE $tableName")
    }
}

/**
 * テーブルを生成します
 */
fun SupportSQLiteDatabase.createTable(vararg tables: Table) {
    tables.forEach { table ->
        this.execSQL(table.createSql)
    }
}