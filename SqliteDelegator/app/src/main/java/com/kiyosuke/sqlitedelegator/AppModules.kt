package com.kiyosuke.sqlitedelegator

import com.kiyosuke.sqlitedelegator.db.Users
import com.kiyosuke.sqlitedelegator.db.dao.UsersDao

object AppModules {
    val usersDao: UsersDao
        get() = UsersDao(App.sqlitlin.createDao(Users))
}