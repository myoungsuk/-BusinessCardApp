package com.example.businesscarapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.businesscarapp.ArticleModel;
import com.example.businesscarapp.R;
import com.example.businesscarapp.activity.AddArticleActivity;
import com.example.businesscarapp.adapters.ArticleAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class NoticeFragment extends Fragment
{
    private ArrayList<ArticleModel> ArticleList;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Uri imageUri;
    private StorageReference firebaseStorage;
    private FirebaseUser user;
    private String uid;

    private RecyclerView recyclerview;
    private ArticleAdapter articleAdapter;


    ChildEventListener listener = new ChildEventListener() {


        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
        {
            ArticleModel articleModel = snapshot.getValue(ArticleModel.class);
            ArticleList.add(articleModel);
//           articleAdapter.submitList(ArticleList);

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
        {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot)
        {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
        {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error)
        {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.fragment_notice,container,false);
        // Inflate the layout for this fragment

        FloatingActionButton addArticleButton = v.findViewById(R.id.addFloatingButton);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();
        uid = user.getUid();

        ArticleList = new ArrayList<>();

        ArticleList.clear();
        mDatabase.addChildEventListener(listener);
        recyclerview = v.findViewById(R.id.articleRecyclerView);
        articleAdapter = new ArticleAdapter(ArticleList);
        recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerview.setAdapter(articleAdapter);










        addArticleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), AddArticleActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

}
