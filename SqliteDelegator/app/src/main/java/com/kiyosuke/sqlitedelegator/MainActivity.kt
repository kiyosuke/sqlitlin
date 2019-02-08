package com.kiyosuke.sqlitedelegator

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kiyosuke.sqlitedelegator.db.Users
import com.kiyosuke.sqlitlin.db.ColumnMap
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
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
        }

        buttonAge.setOnClickListener {
            getUser("kiyo")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUsers() = launch {
        userDao.getAllUsers().forEach { columnMap: ColumnMap ->
            val text = buildString {
                append("Id: ${columnMap[Users.id]}")
                append("\n")
                append("Name: ${columnMap[Users.name]}")
                append("\n")
                append("Age: ${columnMap[Users.age]}")
                append("\n")
                append("\n")
            }
            textView.text = "${textView.text} $text"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUser(age: Int) = launch {
        userDao.getUser(age).forEach { columnMap: ColumnMap ->
            val text = buildString {
                append("Id: ${columnMap[Users.id]}")
                append("\n")
                append("Name: ${columnMap[Users.name]}")
                append("\n")
                append("Age: ${columnMap[Users.age]}")
                append("\n")
                append("\n")
            }
            textView.text = "${textView.text} $text"
        }
    }

    private fun getUser(name: String) = launch {
        try {
            userDao.getUser(name).forEach { columnMap ->
                val text = buildString {
                    append("Id: ${columnMap[Users.id]}")
                    append("\n")
                    append("Name: ${columnMap[Users.name]}")
                    append("\n")
                    append("Age: ${columnMap[Users.age]}")
                    append("\n")
                    append("\n")
                }
                textView.text = "${textView.text} $text"
            }
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
        userDao.insert(listOf(kiyo, john))
    }

}
