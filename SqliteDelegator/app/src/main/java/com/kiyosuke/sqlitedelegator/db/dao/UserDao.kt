package com.kiyosuke.sqlitedelegator.db.dao

import com.kiyosuke.sqlitedelegator.db.User
import com.kiyosuke.sqlitedelegator.db.Users
import com.kiyosuke.sqlitlin.db.Dao
import com.kiyosuke.sqlitlin.db.OrderBy
import com.kiyosuke.sqlitlin.db.core.SupportDatabase

class UserDao(database: SupportDatabase) : Dao<Users>(database) {
    override val table: Users = Users

    suspend fun getUser() =
        select {
            where {
                it.age.less(20) or it.name.like("%k%")
            }
            orderBy(Users.age to OrderBy.SortOrder.ASC)
            limit(10)
        }.map {
            User(it.getValue(Users.id), it.getValue(Users.name), it[Users.age])
        }

    suspend fun getUser(age: Int) =
        select {
            where {
                it.age.eq(age)
            }
        }

    suspend fun getUser(name: String) =
        select {
            where {
                it.name.eq(name)
            }
        }

    suspend fun getAllUsers() = selectAll()
}