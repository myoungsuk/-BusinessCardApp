package com.example.businesscarapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.businesscarapp.R;
import com.example.businesscarapp.activity.CloudVisionAPIActivity;
import com.example.businesscarapp.models.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HomeFragment extends Fragment
{
    Activity activity = getActivity();


    private Button accessButton;
    private ImageView profile_imageview;
    private TextView profile_email;
    private TextView profile_name;
    private TextView profile_snum;
    private TextView profile_dept;
    private TextView profile_univ;
    private TextView profile_desc;


    private ArrayList<Friend> friendList = new ArrayList<>();
    private Friend friend;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Uri imageUri;
    private StorageReference firebaseStorage;
    private FirebaseUser user;
    private String uid;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Context mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();
        uid = user.getUid();

        accessButton = (Button) v.findViewById(R.id.startText);
        profile_imageview = (ImageView) v.findViewById(R.id.profile_imageview);
        profile_name = (TextView) v.findViewById(R.id.profile_textview_name);
        profile_email = (TextView) v.findViewById(R.id.profile_textview_email);
        profile_snum = (TextView) v.findViewById(R.id.profile_textview_snum);
        profile_dept = (TextView) v.findViewById(R.id.profile_textview_dept);
        profile_univ = (TextView) v.findViewById(R.id.profile_textview_univ);
        profile_desc = (TextView) v.findViewById(R.id.profile_textview_desc);


        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Friend friend = snapshot.getValue(Friend.class);

                String email = friend.email;
                String name = friend.name;
                String photo = friend.profileImageUrl;
                String snum = friend.studentId;
                String dept = friend.department;
                String univ = friend.school;
                String desc = friend.description;

                profile_name.setText(name);
                profile_email.setText(email);
                profile_snum.setText(snum);
                profile_dept.setText(dept);
                profile_univ.setText(univ);
                profile_desc.setText(desc);


                if (friend.profileImageUrl.equals(""))
                {
                    //Toast.makeText(requireContext(), "설정에서 프로필을 등록해주세요", Toast.LENGTH_SHORT).show();
                } else
                {
                    Glide.with(getContext())
                            .load(photo)
                            .apply(new RequestOptions().circleCrop())
                            .into(profile_imageview);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });

        //OCR
        accessButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), CloudVisionAPIActivity.class);
                startActivity(intent);
            }
        });


        return v;


    }


}
