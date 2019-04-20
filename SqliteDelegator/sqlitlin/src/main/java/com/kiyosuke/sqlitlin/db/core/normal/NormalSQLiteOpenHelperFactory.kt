package com.kiyosuke.sqlitlin.db.core.normal

import com.kiyosuke.sqlitlin.db.core.support.SupportSQLiteOpenHelper

class NormalSQLiteOpenHelperFactory : SupportSQLiteOpenHelper.Factory {
    override fun create(configure: SupportSQLiteOpenHelper.Configure): SupportSQLiteOpenHelper {
        return NormalSQLiteOpenHelper(
            context = configure.context,
            name = configure.name,
            callback = configure.callback
        )
    }
}