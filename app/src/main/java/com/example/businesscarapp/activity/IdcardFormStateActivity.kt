package com.example.businesscarapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.businesscarapp.R
import com.example.businesscarapp.fragment.IdCardFormFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


private lateinit var auth: FirebaseAuth

var idcardFormFragment = IdCardFormFragment()

class IdcardFormStateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idcard_form_state)
        auth = Firebase.auth

        replaceFragment(idcardFormFragment)


    }

    private fun replaceFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer2, fragment)
                commit()
            }
    }
}
