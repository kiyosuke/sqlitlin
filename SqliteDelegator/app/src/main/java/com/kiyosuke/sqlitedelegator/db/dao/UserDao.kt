package com.kiyosuke.sqlitedelegator.db.dao

import com.kiyosuke.sqlitedelegator.db.AppDatabase
import com.kiyosuke.sqlitedelegator.db.Users
import com.kiyosuke.sqlitlin.db.ColumnMap
import com.kiyosuke.sqlitlin.db.Dao
import com.kiyosuke.sqlitlin.db.core.SupportDatabase

class UserDao(database: SupportDatabase) : Dao<Users>(database) {
    override val table: Users = Users

    suspend fun getUser(age: Int) = select {
        where {
            it.age.eq(age)
        }
    }

    suspend fun getUser(name: String) = select {
        where {
            it.name.eq(name)
        }
    }

    suspend fun getAllUsers() = selectAll()
}