package com.kiyosuke.sqlitedelegator.db.dao

import com.kiyosuke.sqlitedelegator.db.Users
import com.kiyosuke.sqlitlin.db.ColumnMap
import com.kiyosuke.sqlitlin.db.Dao
import com.kiyosuke.sqlitlin.db.core.SupportDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserDao(database: SupportDatabase) : Dao<Users>(database) {
    override val table: Users = Users

    suspend fun getUser(age: Int) = withContext(Dispatchers.IO) {
        select {
            where {
                it.age.eq(age)
            }
        }
    }

    suspend fun getUser(name: String) = withContext(Dispatchers.IO) {
        select {
            where {
                it.name.eq(name)
            }
        }
    }

    suspend fun maxAge() = withContext(Dispatchers.IO) {
        max(Users.age)
    }

    suspend fun minAge() = withContext(Dispatchers.IO) {
        min(Users.age)
    }

    suspend fun countAge() = withContext(Dispatchers.IO) {
        count(Users.name) {
            where {
                Users.age between (10 to 20)
            }
            groupBy(Users.age)
        }
    }

    suspend fun getAllUsers() = withContext(Dispatchers.IO) {
        selectAll()
    }

    suspend fun insertUsers(users: List<ColumnMap>) = withContext(Dispatchers.IO) {
        insert(users)
    }

}