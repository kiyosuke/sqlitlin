package com.kiyosuke.sqlitlin.db.core.adapter

import android.database.sqlite.SQLiteStatement

fun SQLiteStatement.bind(index: Int, value: Any?) {
    when (value) {
        null -> bindNull(index)
        is ByteArray -> bindBlob(index, value)
        is Float -> bindDouble(index, value.toDouble())
        is Double -> bindDouble(index, value)
        is Long -> bindLong(index, value)
        is Int -> bindLong(index, value.toLong())
        is Short -> bindLong(index, value.toLong())
        is Byte -> bindLong(index, value.toLong())
        is String -> bindString(index, value)
        is Boolean -> bindLong(index, if (value) 1 else 0)
        else -> throw IllegalArgumentException(
            "Cannot bind $value ar index $index" +
                    " Supported types: null, byte[], float, double, long, int, short, byte," +
                    " string."
        )
    }
}