package com.kiyosuke.sqlitlin.db


abstract class Op {
    abstract fun toSql(): String
}
