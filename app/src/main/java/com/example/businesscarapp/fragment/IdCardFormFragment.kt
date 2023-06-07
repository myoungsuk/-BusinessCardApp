package com.example.businesscarapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.businesscarapp.R
import com.example.businesscarapp.adapters.ArticleAdapter
import com.example.businesscarapp.adapters.IDCardAdapter
import com.example.businesscarapp.models.IDcard
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date


class IdCardFormFragment : Fragment()
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
    private lateinit var idCardAdapter: IDCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_idcard_form, container, false)
        val idCardRecyclerView = view.findViewById<RecyclerView>(R.id.Id_RecyclerView)
        database = Firebase.database.reference

        idCardRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        idCardRecyclerView.adapter = this.idCardAdapter

        (idCardRecyclerView.layoutManager as LinearLayoutManager).reverseLayout = true
        (idCardRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true
//        reference.addValueEventListener(object : ValueEventListener
//        {
//            override fun onDataChange(dataSnapshot: DataSnapshot)
//            {
//                val idCardList = ArrayList<IDcard>()
//                for (idCardSnapshot in dataSnapshot.children)
//                {
//                    val idCard = idCardSnapshot.getValue(IDcard::class.java)
//                    if (idCard != null)
//                    {
//                        idCardList.add(idCard)
//                    }
//                }
//
//                val adapter = IDCardAdapter(idCardList)
//                idCardRecyclerView.adapter = adapter
//            }
//
//            override fun onCancelled(error: DatabaseError)
//            {
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//        })
        return view
    }
}