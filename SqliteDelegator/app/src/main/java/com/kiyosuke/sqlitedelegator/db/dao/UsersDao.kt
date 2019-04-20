package com.kiyosuke.sqlitedelegator.db.dao

import com.kiyosuke.sqlitedelegator.db.User
import com.kiyosuke.sqlitedelegator.db.Users
import com.kiyosuke.sqlitlin.db.ColumnMap
import com.kiyosuke.sqlitlin.db.Dao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsersDao(private val dao: Dao<Users>) {

    suspend fun insertUsers(users: List<ColumnMap>) = withContext(Dispatchers.IO) {
        dao.insert(users)
    }

    suspend fun findUser(id: Int) = withContext(Dispatchers.IO) {
        dao.select {
            where { Users.id.eq(id) }
        }.get {
            User(
                it.get(Users.id),
                it.get(Users.name),
                it.getOpt(Users.age)
            )
        }
    }

    suspend fun getUser(name: String) = withContext(Dispatchers.IO) {
        dao.select {
            where { Users.name.eq(name) }
        }
    }

    suspend fun getUser(age: Int) = withContext(Dispatchers.IO) {
        dao.select {
            where { Users.age.eq(age) }
        }
    }

    suspend fun getAllUsers() = withContext(Dispatchers.IO) {
        dao.selectAll()
    }
}