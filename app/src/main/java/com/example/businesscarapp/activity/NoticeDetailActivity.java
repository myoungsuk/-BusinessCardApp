package com.example.businesscarapp.activity;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.businesscarapp.models.ArticleModel;
import com.example.businesscarapp.R;
import com.example.businesscarapp.databinding.ActivityNoticeDetailBinding;
import com.example.businesscarapp.fragment.FriendFragment;
import com.example.businesscarapp.models.Friend;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private DatabaseReference fireDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mDatabase;
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

        // 상태바 없애기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(WindowInsets.Type.statusBars());
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        SimpleDateFormat format = new SimpleDateFormat("MM월dd일 hh:mm:ss", Locale.getDefault()); // 포맷으로 날짜 가져오기

        String title = getIntent().getStringExtra("title");
        String contents = getIntent().getStringExtra("contents");
        String photo = getIntent().getStringExtra("photo");
        Long date = getIntent().getExtras().getLong("date");
        String mUserId = getIntent().getStringExtra("id");

        getUserInfo();
        Log.d("uid 있음?", "" + mUserId);


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

    private void getUserInfo() {
        String mUserId = getIntent().getStringExtra("id");
        DatabaseReference userRef = fireDatabase.child("Users").child(mUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String photoUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                // 이름, 사진 URL 등의 정보를 화면에 표시
                // 예를 들면, TextView에 이름을 표시하고, ImageView에 사진을 불러옵니다.
                displayUserInfo(name, email, photoUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read user info.", databaseError.toException());
            }
        });
    }

    private void displayUserInfo(String name, String email, String photoUrl)
    {
        TextView nameTextView = findViewById(R.id.friend_item_tv);
        TextView emailTextView = findViewById(R.id.friend_item_email);
        ImageView profileImageView = findViewById(R.id.friend_item_iv);

        nameTextView.setText(name);
        emailTextView.setText(email);

        // Glide 또는 Picasso 같은 이미지 로딩 라이브러리를 사용하여 ImageView에 이미지를 불러옵니다.
        // 예를 들면 Glide를 사용하면 다음과 같습니다.
        // implementation 'com.github.bumptech.glide:glide:4.12.0'
        // annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
        if(photoUrl != null && !photoUrl.isEmpty()){
            Glide.with(this)
                    .load(photoUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(profileImageView);
        }
        else{
            profileImageView.setImageResource(R.drawable.ic_baseline_person_24);
        }
    }

}

