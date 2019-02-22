# Sqlitlin

AndroidのSQLite操作ライブラリ

# How to use

## Initial
最初にAppDatabaseクラスを作成します。
```
class AppDatabase(context: Context, name: String, version: Int) : NormalDatabase(context, name, version) {
    
    override fun createTable(db: SQLiteDatabase) {
        db.execSQL(Users.createSql)
    }
    
    val usersDao by lazy { UsersDao(this) }
}

class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        db = AppDatabase(applicationContext, "sqlitlin.db", 1)
    }
    
    companion object {
        lateinit ver db: AppDatabase
    }
}
```

## テーブル定義
```
// usersテーブルを定義
object Users : Table("users") {
    val id = integer("id").primaryKey().autoIncrement()
    val name = text("name")
    val age = integer("age").nullable()
}
```
```
data class User(
    val id: Int,
    val name: String,
    val age: Int?
)
```


## データベースアクセス
```
class UsersDao(database: SupportDatabase) : Dao<Users>(database) {
    override val table: Users = Users
    
    fun getUser() = 
        select {
            where {
                Users.age between (10 to 30)
            }
            orderBy(Users.age to OrderBy.SortOrder.ASC)
            limit(10)
        }
}
```


## 利用側
```
class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    
    private val usersDao by lazy { App.db.usersDao }
    
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
