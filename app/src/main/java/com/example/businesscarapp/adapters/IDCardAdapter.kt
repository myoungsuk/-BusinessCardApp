package com.example.businesscarapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.businesscarapp.R
import com.example.businesscarapp.models.IDcard

class IDCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val nameTextView: TextView = view.findViewById(R.id.nameTextView)
    val schoolTextView: TextView = view.findViewById(R.id.schoolTextView)
    val departmentTextView: TextView = view.findViewById(R.id.departmentTextView)
    val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
}

class IDCardAdapter(private val idCardList: List<IDcard>) : RecyclerView.Adapter<IDCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IDCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_idcard, parent, false)
        return IDCardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IDCardViewHolder, position: Int) {
        val idCard = idCardList[position]
        holder.nameTextView.text = idCard.name
        holder.schoolTextView.text = idCard.studentId
        holder.schoolTextView.text = idCard.school
        holder.departmentTextView.text = idCard.department
        holder.descriptionTextView.text = idCard.description
    }

    override fun getItemCount() = idCardList.size
}
