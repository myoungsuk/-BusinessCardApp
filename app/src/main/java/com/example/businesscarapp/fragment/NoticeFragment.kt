package com.example.businesscarapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.businesscarapp.models.ArticleModel
import com.example.businesscarapp.DBkey.DB_ARTICLES
import com.example.businesscarapp.R
import com.example.businesscarapp.activity.AddArticleActivity
import com.example.businesscarapp.activity.NoticeDetailActivity
import com.example.businesscarapp.adapters.ArticleAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.businesscarapp.databinding.FragmentNoticeBinding
import java.util.*

class NoticeFragment :Fragment(R.layout.fragment_notice) {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleDB: DatabaseReference
    private val articleList = mutableListOf<ArticleModel>() // 데이터 스냅샷을 통한 데이터 변경을 알기위해 아티클데이터 변수 설정
    private var article: java.util.ArrayList<ArticleModel> = arrayListOf()

    //차일드 이벤트 리스너 전역변수 설정
    private val user = Firebase.auth.currentUser
    private val uid = user?.uid.toString()
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            //모델클래스 자체를 업로드하고 다운받는다
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return //모델이 없을 경우 리턴

            articleList.add(articleModel) //아티클모텔을 리스트에 추가
            articleAdapter.submitList(articleList) //아티클 어뎁터에 리스트 넣어주기

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(error: DatabaseError) {}

    }

    private var binding: FragmentNoticeBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val fragmentNoticeBinding = FragmentNoticeBinding.bind(view)
        binding = fragmentNoticeBinding

        articleList.clear() // 화면 옮기면 두번 출력되는거 방지

        articleDB = Firebase.database.reference.child(DB_ARTICLES) //아티클DB firebase 데이터베이스 선언
        articleAdapter = ArticleAdapter(itemClickedlistener = {
            val intent = Intent(context, NoticeDetailActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("title", it.title)
            intent.putExtra("contents", it.content)
            intent.putExtra("photo", it.imageUrl)
            intent.putExtra("date", it.createdAt)
            intent.putExtra("id", it.noticeId)
            context?.startActivity(intent)

        }) //아티클 어댑터 호출

        articleDB.addChildEventListener(listener) //아티클 데이터 이벤트 리스너에 추가


        fragmentNoticeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentNoticeBinding.articleRecyclerView.adapter = articleAdapter

        (fragmentNoticeBinding.articleRecyclerView.layoutManager as LinearLayoutManager).reverseLayout =
            true
        (fragmentNoticeBinding.articleRecyclerView.layoutManager as LinearLayoutManager)
            .stackFromEnd = true


        //플로팅버튼 fragment에서 화면 넘기기
        fragmentNoticeBinding.addFloatingButton.setOnClickListener {
            context?.let {

                //회원만 게시글을 올릴 수 있게 해놨다. TODO: 지정한 운영자만 게시글을 올릴 수 있게 수정하기
                if (auth.currentUser != null) {
                    val intent = Intent(requireContext(), AddArticleActivity::class.java)
                    startActivity(intent)
                } else {
                    Snackbar.make(view, "로그인 후 사용해 주세요", Snackbar.LENGTH_LONG).show()
                }


            }
        }
        //  articleDB.addChildEventListener(listener)
    }

    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    //  게시글 올렸을때 데이터 변환을 알고 새로고침해줌
    override fun onResume() {
        super.onResume()


//        articleList.clear() // 화면 옮기면 두번 출력되는거 방지
        getFragmentManager()?.let { refreshFragment(this, it) }
        //articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        articleDB.removeEventListener(listener)
    }

}
//class NoticeFragment : Fragment()
//{
//    companion object
//    {
//        fun newInstance(): NoticeFragment
//        {
//            return NoticeFragment()
//        }
//    }
//
//    private lateinit var database: DatabaseReference
//    private var articleModel: ArrayList<ArticleModel> = arrayListOf()
//    private var _binding: FragmentNoticeBinding? = null
//    private val binding get() = _binding!!
//    private val articleList = mutableListOf<ArticleModel>();
//
//    private val auth: FirebaseAuth by lazy {
//        Firebase.auth
//    }
//
//    //뷰가 생성되었을 때
//    //프레그먼트와 레이아웃을 연결시켜주는 부분
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View?
//    {
//
//        database = Firebase.database.reference
//        _binding = FragmentNoticeBinding.inflate(inflater, container, false)
//        val view = binding.root
//
//        articleList.clear();
//
//        binding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
//        binding.articleRecyclerView.adapter = ArticleAdapter()
//
//        (binding.articleRecyclerView.layoutManager as LinearLayoutManager).reverseLayout =
//            true
//        (binding.articleRecyclerView.layoutManager as LinearLayoutManager)
//            .stackFromEnd = true
//
//        binding.addFloatingButton.setOnClickListener {
//            context?.let {
//
//                //회원만 게시글을 올릴 수 있게 해놨다.
//                if (auth.currentUser != null)
//                {
//                    val intent = Intent(requireContext(), AddArticleActivity::class.java)
//                    startActivity(intent)
//                } else
//                {
//                    Snackbar.make(view, "로그인 후 작성해주세요", Snackbar.LENGTH_LONG).show()
//                }
//
//            }
//        }
//        return view
//    }
//
//    override fun onDestroyView()
//    {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    inner class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.CustomViewHolder>()
//    {
//
//        init
//        {
//            val myUid = Firebase.auth.currentUser?.uid.toString()
//            FirebaseDatabase.getInstance().reference.child("DB_ARTICLES")
//                .addValueEventListener(object :
//                    ValueEventListener
//                {
//                    override fun onCancelled(error: DatabaseError)
//                    {
//                    }
//
//                    override fun onDataChange(snapshot: DataSnapshot)
//                    {
//                        articleModel.clear()
//                        for (data in snapshot.children)
//                        {
//                            val item = data.getValue<ArticleModel>()
//                            if (item != null)
//                            {
//                                articleModel.add(item)
//                            }
//                        }
//                        notifyDataSetChanged()
//                    }
//                })
//        }
//
////        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder
////        {
//////            val binding =
//////                ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//////            return CustomViewHolder(binding)
////            return CustomViewHolder(
////                LayoutInflater.from(context).inflate(
////                    R.layout.item_article, parent,
////                    false
////                )
////            )
////        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder
//        {
//            return CustomViewHolder(
//                LayoutInflater.from(context).inflate(
//                    R.layout.item_article, parent,
//                    false
//                )
//            )
//        }
//
//        inner class CustomViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
//
//            fun bind(articleModel: ArticleModel)
//            {
//
//
////                if (article.imageUrl.isNotEmpty()) {
////                    Glide.with(binding.thumbnailImageView)
////                        .load(article.imageUrl.first())
////                        .into(binding.thumbnailImageView)
////                }
//
//            }
//            val imageView: ImageView = itemView.findViewById(R.id.thumbnailImageView)
//            val textViewTitle : TextView = itemView.findViewById(R.id.titleTextView)
//            val textViewContent : TextView = itemView.findViewById(R.id.contentsTextView)
//            val dateTextView : TextView = itemView.findViewById(R.id.dateTextView)
//        }
//
//        override fun onBindViewHolder(holder: CustomViewHolder, position: Int)
//        {
//            val format = SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.getDefault())
//            val timestamp = articleModel[position].createdAt
//            val date = Date(timestamp)
//
//            val holderPhoto = articleModel[position].imageUrl
//            val holderTitle = articleModel[position].title
//            val holderContent = articleModel[position].content
//
//            Glide.with(holder.itemView.context)
//                .load(articleModel[position].imageUrl)
//                .into(holder.imageView)
//            holder.textViewTitle.text = articleModel[position].title
//            holder.textViewContent.text = articleModel[position].content
//            holder.dateTextView.text = format.format(date)
//
//            holder.bind(articleModel[position])
////                binding.dateTextView.text = format.format(date).toString()
////                binding.titleTextView.text = article.title
////                binding.contentsTextView.text = article.content
//
//
//            holder.itemView.setOnClickListener {
//                val intent = Intent(context, NoticeDetailActivity::class.java)
//                context?.startActivity(intent)
//            }
//
//        }
//
//        override fun getItemCount(): Int
//        {
//            return articleModel.size
//        }
//    }
//    companion object {
//        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
//            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
//                return oldItem.createdAt == newItem.createdAt
//            }
//
//            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
//                return oldItem == newItem
//            }
//
//        }
//    }


