package com.example.businesscarapp.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.businesscarapp.models.ArticleModel;
import com.example.businesscarapp.R;
import com.example.businesscarapp.databinding.ActivityNoticeDetailBinding;
import com.example.businesscarapp.fragment.FriendFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NoticeDetailActivity extends AppCompatActivity
{
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final Uri imageUri = null;
    private static final StorageReference fireStorage = FirebaseStorage.getInstance().getReference();
    private static final DatabaseReference fireDatabase = FirebaseDatabase.getInstance().getReference();
    private static final String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final FriendFragment nnoticeFragment = new FriendFragment();
    private final List<ArticleModel> articleList = new ArrayList<>(); // 변수 설정을 위한 데이터 스냅샷
    private ActivityNoticeDetailBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SimpleDateFormat format = new SimpleDateFormat("MM월dd일 hh:mm:ss", Locale.getDefault()); // 포맷으로 날짜 가져오기

        String title = getIntent().getStringExtra("title");
        String contents = getIntent().getStringExtra("contents");
        String photo = getIntent().getStringExtra("photo");
        Long date = getIntent().getExtras().getLong("date");

        Log.d("date 데이터", "" + date);
        Date realdate = new Date(Objects.requireNonNull(date));
        binding.dateTextView.setText(format.format(realdate));
        Log.d("date 데이터222", "" + date);
        binding.titleTextView.setText(title);
        binding.descriptionTextView.setText(contents);

        ImageView photolayout = findViewById(R.id.coverImageView);
        if (photo != null && !photo.isEmpty()) {
            Glide.with(binding.coverImageView.getContext())
                    .load(photo)
                    .into(binding.coverImageView);
        }
        if (photo != null && photo.isEmpty())
        {
            photolayout.setVisibility(View.GONE);
        }
    }
}