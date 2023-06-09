package com.example.businesscarapp.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.businesscarapp.R
import com.example.businesscarapp.adapters.ArticleAdapter.Companion.diffUtil
import com.example.businesscarapp.databinding.ItemIdcardBinding
import com.example.businesscarapp.models.ArticleModel
import com.example.businesscarapp.models.IDcard
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date


private var idCardList: ArrayList<IDcard> = arrayListOf()

interface OnItemClickListener {
    fun onItemClick(idCard : IDcard)
}

class IDCardAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<IDCardAdapter.IDCardViewHolder>() {

    init {
        val myUid = Firebase.auth.currentUser?.uid.toString()

        FirebaseDatabase.getInstance().reference.child("IDcard").child(myUid)
            .addValueEventListener(object : ValueEventListener
            {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    idCardList.clear()
                    for (data in snapshot.children) {
                        val item = data.getValue<IDcard>()
                        if (item != null) {
                            idCardList.add(item)
                        }
                    }
                    notifyDataSetChanged()
                }

            })

    }
    class IDCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val schoolTextView: TextView = view.findViewById(R.id.schoolTextView)
        val departmentTextView: TextView = view.findViewById(R.id.departmentTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val studentIDTextView: TextView = view.findViewById(R.id.student_id_textview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IDCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_idcard, parent, false)
        return IDCardViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: IDCardViewHolder, position: Int) {
        val idCard = idCardList[position]
        holder.nameTextView.text = idCard.name
        holder.studentIDTextView.text = idCard.studentId
        holder.schoolTextView.text = idCard.school
        holder.departmentTextView.text = idCard.department
        holder.descriptionTextView.text = idCard.description


        holder.itemView.setOnClickListener {
            listener.onItemClick(idCard)
        }
    }

    override fun getItemCount() = idCardList.size
}