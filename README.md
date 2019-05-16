# Sqlitlin

AndroidのSQLite操作ライブラリ

# How to use

## Initial
最初にAppDatabaseクラスを作成します。
```kotlin
class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        db = Sqlitlin.builder(applicationContext, "sqlitlin.db", 1)
            .addTables(Users)
            .addMigration(MIGRATION_1_2)
            .build()
    }
    
    companion object {
        lateinit ver db: Sqlitlin
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migration(database: SupportSQLiteDatabase) {
                Log.d(TAG, "running migration 1 to 2")
                database.addColumn(Users.job)
            }
        }
    }
}
```

## テーブル定義
```kotlin
// usersテーブルを定義
object Users : Table("users") {
    val id = integer("id").primaryKey().autoIncrement()
    val name = text("name")
    val age = integer("age").nullable()
}
```
```kotlin
data class User(
    val id: Int,
    val name: String,
    val age: Int?
)
```


## データベースアクセス
```kotlin
class UsersDao(private val dao: Dao<Users>) {

    fun getUser() = withContext(Dispachers.IO) {
        dao.select {
            where {
                Users.age between (10 to 30)
            }
            orderBy(Users.age to OrderBy.SortOrder.ASC)
            limit(10)
        }   
    }
    
    fun insertUsers(users: List<ColumnMap>) = withContext(Dispatchers.IO) {
        dao.insert(users)
    }
}

object AppModule {
    val usersDao: UsersDao
        get() = UsersDao(App.db.createDao(Users))
}
```


## 利用側
```kotlin
class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    
    private val usersDao by lazy { AppModule.usersDao }
    
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()
        
        insertUser()
        getUser()
    }
    
    private fun insertUser() = launch {
        val kiyosuke = ColumnMap().apply {
            this[Users.name] = "kiyosuke"
            this[Users.age] = 20
        }
        usersDao.insert(kiyosuke)
    }
    
    private fun getUser() = launch {
        usersDao.getUser().forEach { user ->
            println("user: $user")
        }
    }
}
```

## 注意

*  データベース操作はメインスレッド以外のスレッドで行ってください。メインスレッドで実行された場合例外が発生します。
*  SELECTした結果が0件の場合 EmptyResultSetException が発生します。
