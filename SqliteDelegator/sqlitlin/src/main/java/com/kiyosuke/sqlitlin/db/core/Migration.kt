package com.kiyosuke.sqlitlin.db.core

import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteDatabase

abstract class Migration(val startVersion: Int, val endVersion: Int) {

    abstract fun migration(database: SupportSQLiteDatabase)
}