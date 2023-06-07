package com.example.businesscarapp.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.businesscarapp.R


import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class IDCardDetailActivity : AppCompatActivity() {

    companion object {
        private val user = Firebase.auth.currentUser
    }



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idcard_detail)

        val name = getIntent().getStringExtra("name")
        val studentID = getIntent().getStringExtra("studentID")
        val school = getIntent().getStringExtra("school")
        val department = getIntent().getStringExtra("department")
        val description = getIntent().getStringExtra("description")
        val email = getIntent().getStringExtra("email")

        val nameTextView = findViewById<TextView>(R.id.profile_textview_name)
        val studentIDTextView = findViewById<TextView>(R.id.profile_textview_snum)
        val schoolTextView = findViewById<TextView>(R.id.profile_textview_univ)
        val departmentTextView = findViewById<TextView>(R.id.profile_textview_dept)
        val descriptionTextView = findViewById<TextView>(R.id.profile_textview_desc)
        val emailTextView = findViewById<TextView>(R.id.profile_textview_email)

        nameTextView.text = name
        studentIDTextView.text = studentID
        schoolTextView.text = school
        departmentTextView.text = department
        descriptionTextView.text = description
        emailTextView.text = email

    }


}