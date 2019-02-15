package com.kiyosuke.sqlitedelegator

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    private val userDao by lazy { App.db.userDao }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()

        buttonAll.setOnClickListener {
            getUsers()
            userCount()
            userMaxAge()
            userMinAge()
            userAges()
        }

        buttonAge.setOnClickListener {
            insertUser()
        }
    }

    private fun getUsers() = launch {
        try {
            val text = buildString {
                userDao.getAllUsers().forEach { columnMap: ColumnMap ->
                    append("Id: ${columnMap[Users.id]}")
                    append("\n")
                    append("Name: ${columnMap[Users.name]}")
                    append("\n")
                    append("Age: ${columnMap[Users.age]}")
                    append("\n")
                    append("\n")
                }
            }
            textView.text = text
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUser(age: Int) = launch {
        try {
            val text = buildString {
                userDao.getUser(age).forEach { columnMap: ColumnMap ->
                    append("Id: ${columnMap[Users.id]}")
                    append("\n")
                    append("Name: ${columnMap[Users.name]}")
                    append("\n")
                    append("Age: ${columnMap[Users.age]}")
                    append("\n")
                    append("\n")
                }
            }
            textView.text = text
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUser(name: String) = launch {
        try {
            val text = buildString {
                userDao.getUser(name).forEach { columnMap ->
                    append("Id: ${columnMap[Users.id]}")
                    append("\n")
                    append("Name: ${columnMap[Users.name]}")
                    append("\n")
                    append("Age: ${columnMap[Users.age]}")
                    append("\n")
                    append("\n")
                }
            }
            textView.text = text
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun userCount() = launch {
        try {
            val count = userDao.countAll()
            Log.d("MainActivity", "count: $count")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun userMaxAge() = launch {
        try {
            val age = userDao.maxAge()
            Log.d("MainActivity", "maxAge: $age")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun userMinAge() = launch {
        try {
            val age = userDao.minAge()
            Log.d("MainActivity", "minAge: $age")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun userAges() = launch {
        try {
            val result = userDao.countAge()
            Log.d("MainActivity", "countAge: $result")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun insertUser() = launch {
        val kiyo = ColumnMap().apply {
            this[Users.name] = "kiyo"
            this[Users.age] = 20
        }

        val john = ColumnMap().apply {
            this[Users.name] = "john"
            this[Users.age] = 31
        }
        try {
            userDao.insert(listOf(kiyo, john))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
