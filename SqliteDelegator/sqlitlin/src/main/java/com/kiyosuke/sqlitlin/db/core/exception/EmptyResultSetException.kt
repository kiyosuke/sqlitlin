package com.kiyosuke.sqlitlin.db.core.exception

/**
 * SELECTをかけた際にデータが一件もなかった際に投げる
 */
class EmptyResultSetException(message: String) : RuntimeException(message)