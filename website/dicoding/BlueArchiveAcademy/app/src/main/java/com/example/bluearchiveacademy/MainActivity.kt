package com.example.bluearchiveacademy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluearchiveacademy.student.StudentAdapter
import com.example.bluearchiveacademy.about.About
import com.example.bluearchiveacademy.student.Student
import com.example.bluearchiveacademy.student.StudentData

class MainActivity : AppCompatActivity() {
    private lateinit var rvStudent: RecyclerView
    private var list: ArrayList<Student> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionbar = supportActionBar
        actionbar!!.title = "Student"

        rvStudent = findViewById(R.id.rv_student)
        rvStudent.setHasFixedSize(true)

        list.addAll(StudentData.listData)
        showRecyclerList()
    }

    private fun showRecyclerList() {
        rvStudent.layoutManager = LinearLayoutManager(this)
        val academyAdapter = StudentAdapter(list)
        rvStudent.adapter = academyAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMode(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setMode(selectedMode: Int) {
        when (selectedMode) {
            R.id.miCompose -> {
                val iAbout = Intent(
                    this@MainActivity,
                    About::class.java
                )
                startActivity(iAbout)
            }
        }
    }
}