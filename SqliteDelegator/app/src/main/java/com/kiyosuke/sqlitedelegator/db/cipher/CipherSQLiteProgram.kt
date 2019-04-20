package com.kiyosuke.sqlitedelegator.db.cipher

import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteProgram
import net.sqlcipher.database.SQLiteProgram

open class CipherSQLiteProgram(private val delegate : SQLiteProgram) : SupportSQLiteProgram {
    override fun bindNull(index: Int) {
        delegate.bindNull(index)
    }

    override fun bindLong(index: Int, value: Long) {
        delegate.bindLong(index, value)
    }

    override fun bindDouble(index: Int, value: Double) {
        delegate.bindDouble(index, value)
    }

    override fun bindString(index: Int, value: String) {
        delegate.bindString(index, value)
    }

    override fun bindBlob(index: Int, value: ByteArray) {
        delegate.bindBlob(index, value)
    }

    override fun clearBindings() {
        delegate.clearBindings()
    }

    override fun close() {
        delegate.close()
    }
}