package com.kiyosuke.sqlitlin.db

import com.kiyosuke.sqlitlin.db.core.SupportDatabase
import com.kiyosuke.sqlitlin.db.table.Table

object UserTable : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = text("name")
    val age = integer("age")
    val birthday = text("birthday").nullable()
}

class DemoDao(database: SupportDatabase) : Dao<UserTable>(database) {

    override val table: UserTable = UserTable

    suspend fun getUser() = select {
        where {
            it.age.eq(19) or it.age.eq(20)
        }
        limit(10)
    }


}