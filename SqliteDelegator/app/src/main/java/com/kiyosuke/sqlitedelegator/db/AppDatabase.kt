package com.kiyosuke.sqlitedelegator.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.kiyosuke.sqlitedelegator.db.dao.UserDao
import com.kiyosuke.sqlitlin.db.core.NormalDatabase
import com.kiyosuke.sqlitlin.db.core.SupportDatabase

class AppDatabase(context: Context, name: String, version: Int) : NormalDatabase(context, name, version) {

    override fun createTable(db: SQLiteDatabase) {
        db.execSQL(Users.createSql)
    }

    val userDao by lazy { UserDao(this) }

}