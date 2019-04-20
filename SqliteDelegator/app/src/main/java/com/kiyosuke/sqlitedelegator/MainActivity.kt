package com.kiyosuke.sqlitedelegator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kiyosuke.sqlitedelegator.db.Users
import com.kiyosuke.sqlitlin.db.ColumnMap
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    private val userDao by lazy { AppModules.usersDao }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()

        buttonAll.setOnClickListener {
            getUsers()
        }

        buttonAge.setOnClickListener {
            insertUser()
        }
    }

    private fun getUsers() = launch {
        try {
            val text = buildString {
                userDao.getAllUsers().forEach { columnMap ->
                    append("Id: ${columnMap.get(Users.id)}")
                    append("\n")
                    append("Name: ${columnMap.get(Users.name)}")
                    append("\n")
                    append("Age: ${columnMap.getOpt(Users.age)}")
                    append("\n")
                    append("Job: ${columnMap.getOpt(Users.job)}")
                    append("\n")
                    append("\n")
                }
            }
            textView.text = text
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun insertUser() = launch {
        val kiyo = ColumnMap().apply {
            this[Users.name] = "kiyo"
            this[Users.age] = 20
            this[Users.job] = "programmer"
        }

        val john = ColumnMap().apply {
            this[Users.name] = "john"
            this[Users.age] = 31
            this[Users.job] = "NEET"
        }
        try {
            userDao.insertUsers(listOf(kiyo, john))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
