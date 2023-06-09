package com.example.businesscarapp.activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.businesscarapp.R

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.security.AccessController.getContext


class IDCardDetailActivity : AppCompatActivity() {

    companion object {
        private val user = Firebase.auth.currentUser
    }

    private val mDatabase: DatabaseReference? = null
    private val auth: FirebaseAuth? = null

    private val uid: String? = null
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
        val profileImageUrl = getIntent().getStringExtra("profileImageUrl")


        Log.d("profileImageView", "ProfileImageUrl: $profileImageUrl")

        val nameTextView = findViewById<TextView>(R.id.profile_textview_name)
        val studentIDTextView = findViewById<TextView>(R.id.profile_textview_snum)
        val schoolTextView = findViewById<TextView>(R.id.profile_textview_univ)
        val departmentTextView = findViewById<TextView>(R.id.profile_textview_dept)
        val descriptionTextView = findViewById<TextView>(R.id.profile_textview_desc)
        val emailTextView = findViewById<TextView>(R.id.profile_textview_email)
        val addprofileImageView = findViewById<ImageView>(R.id.add_photo_button)
        val profileImageView = findViewById<ImageView>(R.id.profile_imageview)
        val DeleteIDcardButton = findViewById<Button>(R.id.delete_button)

        if (profileImageUrl != null && !profileImageUrl.isEmpty())
        {
            Glide.with(this)
                .load(profileImageUrl)
                .apply(RequestOptions().circleCrop())
                .into(profileImageView)
        } else
        {
            profileImageView.setImageResource(R.drawable.ic_baseline_person_24)
        }

        nameTextView.text = name
        studentIDTextView.text = studentID
        schoolTextView.text = school
        departmentTextView.text = department
        descriptionTextView.text = description
        emailTextView.text = email


        DeleteIDcardButton.setOnClickListener {
            val uid = FirebaseAuth.getInstance().uid ?: ""
            val ref = FirebaseDatabase.getInstance().getReference("/IDcards/$uid")
            ref.removeValue()
                .addOnSuccessListener {
                    Log.d("IDCardDetailActivity", "Successfully deleted value from database")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("IDCardDetailActivity", "Failed to delete value from database")
                }
        }

        addprofileImageView.setOnClickListener {

            val intent = Intent(this, AddCardPhotoActivity::class.java)
            startActivity(intent)

            }
        }


    }