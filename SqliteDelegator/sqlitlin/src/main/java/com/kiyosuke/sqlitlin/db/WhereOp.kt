package com.kiyosuke.sqlitlin.db


abstract class WhereOp {
    abstract fun toSql(): String
}
