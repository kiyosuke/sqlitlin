package com.kiyosuke.sqlitedelegator.db

import com.kiyosuke.sqlitlin.db.table.Table

object Users : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = text("name")
    val age = integer("age").nullable()
    val job = text("job").nullable()
}