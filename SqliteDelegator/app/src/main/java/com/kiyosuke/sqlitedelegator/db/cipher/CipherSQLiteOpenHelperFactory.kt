package com.kiyosuke.sqlitedelegator.db.cipher

import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteOpenHelper

class CipherSQLiteOpenHelperFactory : SupportSQLiteOpenHelper.Factory {
    override fun create(configure: SupportSQLiteOpenHelper.Configure): SupportSQLiteOpenHelper {
        return CipherSQLiteOpenHelper(context = configure.context, name = configure.name, callback = configure.callback)
    }
}