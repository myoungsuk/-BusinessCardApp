package com.example.businesscarapp.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.businesscarapp.R
import com.example.businesscarapp.adapters.IDCardAdapter
import com.example.businesscarapp.models.IDcard
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date


class IdCardFormFragment : Fragment(R.layout.fragment_idcard_form)
{

    companion object
    {
        fun newInstance(): IdCardFormFragment
        {
            return IdCardFormFragment()
        }
    }

    private lateinit var database: DatabaseReference
    private var IDcardForm: ArrayList<IDcard> = arrayListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val idCardRecyclerView = view.findViewById<RecyclerView>(R.id.Id_RecyclerView)
        idCardRecyclerView.layoutManager = LinearLayoutManager(activity)

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("IDcard")

        reference.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                val idCardList = ArrayList<IDcard>()
                for (idCardSnapshot in dataSnapshot.children)
                {
                    val idCard = idCardSnapshot.getValue(IDcard::class.java)
                    if (idCard != null)
                    {
                        idCardList.add(idCard)
                    }
                }

                val adapter = IDCardAdapter(idCardList)
                idCardRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError)
            {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }
}