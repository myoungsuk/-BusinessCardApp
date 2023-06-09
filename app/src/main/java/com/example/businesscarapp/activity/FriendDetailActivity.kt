package com.example.businesscarapp.activity

import com.example.businesscarapp.models.IDcard
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.businesscarapp.R

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FriendDetailActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_detail_acitivty)

        val name = getIntent().getStringExtra("name")
        val studentID = getIntent().getStringExtra("studentID")
        val school = getIntent().getStringExtra("school")
        val department = getIntent().getStringExtra("department")
        val description = getIntent().getStringExtra("description")
        val email = getIntent().getStringExtra("email")
        val profileImageUrl = getIntent().getStringExtra("profileImageUrl")

        Log.d("FriendDetailActivity", "ProfileImageUrl: $profileImageUrl")

        val nameTextView = findViewById<TextView>(R.id.F_profile_textview_name)
        val studentIDTextView = findViewById<TextView>(R.id.F_profile_textview_snum)
        val schoolTextView = findViewById<TextView>(R.id.F_profile_textview_univ)
        val departmentTextView = findViewById<TextView>(R.id.F_profile_textview_dept)
        val descriptionTextView = findViewById<TextView>(R.id.F_profile_textview_desc)
        val emailTextView = findViewById<TextView>(R.id.F_profile_textview_email)
        val profileImageView = findViewById<ImageView>(R.id.F_profile_imageview)
        val addIDcardButton = findViewById<Button>(R.id.btn_add_idcard)


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

        addIDcardButton.setOnClickListener {

            val FriendName = nameTextView.text.toString()
            val FriendEmail = emailTextView.text.toString()
            val FriendStudentID = studentIDTextView.text.toString()
            val FriendSchool = schoolTextView.text.toString()
            val FriendDepartment = departmentTextView.text.toString()
            val FriendDescription = descriptionTextView.text.toString()
            val FriendProfileImageUrl = profileImageUrl.toString()


            saveUserData(FriendName, FriendStudentID, FriendSchool, FriendDepartment, FriendDescription, createdAt =System.currentTimeMillis() , ProfileImageUrl = FriendProfileImageUrl)
        }


    }

    private fun saveUserData(
        name: String,
        studentId: String,
        department: String,
        school: String,
        description: String,
        createdAt: Long = System.currentTimeMillis(),
        ProfileImageUrl: String
    )
    {
        val (_, name1, studentId1, school1, department1, description1, createdAt, _) = IDcard(
            "",
            name,
            studentId,
            department,
            school,
            description,
            System.currentTimeMillis(),
            ""
        )
        val database = FirebaseDatabase.getInstance()

        // Get current user's uid
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val reference = database.getReference("IDcard").child(uid).push() // create unique id for each data
        val hashMap = HashMap<String, Any>()

        hashMap["name"] = name1
        hashMap["studentId"] = studentId1
        hashMap["school"] = school1
        hashMap["department"] = department1
        hashMap["description"] = description1
        hashMap["createdAt"] = createdAt
        hashMap["id"] = reference.key.toString()
        hashMap["profileImageUrl"] = ProfileImageUrl

        reference.setValue(hashMap) // save data under the uid of current user with unique id
        Toast.makeText(this, "ID card 추가완료", Toast.LENGTH_SHORT)
            .show()
    }

}