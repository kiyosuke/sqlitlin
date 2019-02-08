package com.kiyosuke.sqlitlin.db.core.common

import android.database.Cursor

class IndexCachedCursor(private val cursor: Cursor) : Cursor by cursor {
    private val indexCache: MutableMap<String, Int> = mutableMapOf()

    fun getString(name: String): String = getString(getColumnIndexOrThrow(name))

    fun getBlob(name: String): ByteArray = getBlob(getColumnIndexOrThrow(name))

    fun getShort(name: String): Short = getShort(getColumnIndexOrThrow(name))

    fun getInt(name: String): Int = getInt(getColumnIndexOrThrow(name))

    fun getLong(name: String): Long = getLong(getColumnIndexOrThrow(name))

    fun getFloat(name: String): Float = getFloat(getColumnIndexOrThrow(name))

    fun getDouble(name: String): Double = getDouble(getColumnIndexOrThrow(name))

    fun getStringOrNull(name: String): String? = getStringOrNull(getColumnIndexOrThrow(name))

    fun getBlobOrNull(name: String): ByteArray? = getBlobOrNull(getColumnIndexOrThrow(name))

    fun getShortOrNull(name: String): Short? = getShortOrNull(getColumnIndexOrThrow(name))

    fun getIntOrNull(name: String): Int? = getIntOrNull(getColumnIndexOrThrow(name))

    fun getLongOrNull(name: String): Long? = getLongOrNull(getColumnIndexOrThrow(name))

    fun getFloatOrNull(name: String): Float? = getFloatOrNull(getColumnIndexOrThrow(name))

    fun getDoubleOrNull(name: String): Double? = getDoubleOrNull(getColumnIndexOrThrow(name))

    fun getStringOrNull(index: Int): String? = if (this.isNull(index)) null else this.getString(index)

    fun getBlobOrNull(index: Int): ByteArray? = if (this.isNull(index)) null else this.getBlob(index)

    fun getShortOrNull(index: Int): Short? = if (this.isNull(index)) null else this.getShort(index)

    fun getIntOrNull(index: Int): Int? = if (this.isNull(index)) null else this.getInt(index)

    fun getLongOrNull(index: Int): Long? = if (this.isNull(index)) null else this.getLong(index)

    fun getFloatOrNull(index: Int): Float? = if (this.isNull(index)) null else this.getFloat(index)

    fun getDoubleOrNull(index: Int): Double? = if (this.isNull(index)) null else this.getDouble(index)

    override fun getColumnIndex(name: String): Int {
        val index: Int
        if (indexCache.containsKey(name)) {
            index = indexCache.getValue(name)
        } else {
            index = cursor.getColumnIndex(name)
            indexCache[name] = index
        }
        return index
    }

    override fun getColumnIndexOrThrow(name: String): Int {
        val index: Int
        if (indexCache.containsKey(name)) {
            index = indexCache.getValue(name)
        } else {
            index = cursor.getColumnIndexOrThrow(name)
            indexCache[name] = index
        }
        return index
    }
}