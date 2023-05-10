package com.example.businesscarapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.businesscarapp.models.ArticleModel
import com.example.businesscarapp.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(val itemClickedlistener: (ArticleModel) -> Unit) :
    ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(articleModel: ArticleModel) {

            val format = SimpleDateFormat("MM월 dd일") //날짜를 가지고 오기 위한 포맷
            val date = Date(articleModel.createdAt) //날짜로 데이터타입 변경

            binding.titleTextView.text = articleModel.title
            binding.dateTextView.text = format.format(date).toString()
            binding.contentsTextView.text = articleModel.content

            if (articleModel.imageUrl.isNotEmpty()) {
                Glide.with(binding.thumbnailImageView)
                    .load(articleModel.imageUrl)
                    .into(binding.thumbnailImageView)
            }

            binding.root.setOnClickListener {
                itemClickedlistener(articleModel)
            }


        }

    }

    //아티클 어댑터 뷰홀더 생성함수 오버라이드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // 바인드 뷰홀더 함수 오버라이드
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])

    }


    //데이터가 변경되면  새로운 아이템 콜백 요청
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}
















//
//class ArticleAdapter(val itemClickedListener: (ArticleModel) -> Unit) :
//    ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
//        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
//        private val contentsTextView: TextView = itemView.findViewById(R.id.contentsTextView)
//        private val thumbnailImageView: ImageView = itemView.findViewById(R.id.thumbnailImageView)
//
//        fun bind(articleModel: ArticleModel) {
//            val format = SimpleDateFormat("MM월 dd일")
//            val date = Date(articleModel.createdAt)
//
//            titleTextView.text = articleModel.title
//            dateTextView.text = format.format(date).toString()
//            contentsTextView.text = articleModel.content
//
//            if (articleModel.imageUrlList.isNotEmpty()) {
//                Glide.with(thumbnailImageView)
//                    .load(articleModel.imageUrlList.first())
//                    .into(thumbnailImageView)
//            }
//
//            itemView.setOnClickListener {
//                itemClickedListener(articleModel)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(currentList[position])
//    }
//
//    companion object {
//        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
//            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
//                return oldItem.createdAt == newItem.createdAt
//            }
//
//            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//}
