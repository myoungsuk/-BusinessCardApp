package com.example.businesscarapp.fragment;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.businesscarapp.CustomDialog;
import com.example.businesscarapp.R;
import com.example.businesscarapp.activity.AddPhotoActivity;
import com.example.businesscarapp.activity.IdcardFormStateActivity;
import com.example.businesscarapp.activity.LoginActivity;
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


public class SettingFragment extends Fragment
{
    private ArrayList<Friend> friendList = new ArrayList<>();
    private Friend friend;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Uri imageUri;
    private StorageReference firebaseStorage;
    private FirebaseUser user;
    private String uid;
    public CustomDialog dialog = new CustomDialog();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();
        uid = user.getUid();
        friendList = new ArrayList<>();

        ImageView profilephoto = v.findViewById(R.id.profile);
        ImageView profileEdit = v.findViewById(R.id.photoedit);
        TextView nameText = v.findViewById(R.id.name);
        TextView emailText = v.findViewById(R.id.email);
        TextView nameEditButton = v.findViewById(R.id.nameEditText);
        TextView signoutButton = v.findViewById(R.id.signoutButton);
        TextView idcardListButton = v.findViewById(R.id.idcardListButton);

        //프로필에 데이터 보여주기
        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Friend friend = snapshot.getValue(Friend.class);

                String email = friend.email;
                String name = friend.name;

                emailText.setText(email);
                nameText.setText(name);

                if (friend.profileImageUrl.equals(""))
                {
                } else
                {
                    Glide.with(requireContext())
                            .load(friend.profileImageUrl)
                            .apply(new RequestOptions().circleCrop())
                            .into(profilephoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });

        //이름변경하기
        nameEditButton.setOnClickListener(new View.OnClickListener()
        {


            @Override
            public void onClick(View view)
            {
                dialog.setButtonClickListener(new CustomDialog.OnButtonClickListener()
                {

                    @Override
                    public void onButton1Clicked()
                    {

                    }

                    @Override
                    public void onButton2Clicked(String nameEdit)
                    {
                        nameText.setText(nameEdit);
                        mDatabase.child("Users").child(uid).child("name").setValue(nameEdit);

                        Toast.makeText(requireContext(), "이름이 변경되었습니다", Toast.LENGTH_SHORT).show();

                        nameText.clearFocus();
                        dialog.dismiss();
                    }
                });
                dialog.show(requireActivity().getSupportFragmentManager(), "CustomDialog");
            }
        });

        //사진변경하기
        profileEdit.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), AddPhotoActivity.class);
                startActivity(intent);
            }
        });

        //명함목록 들어가기
        idcardListButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IdcardFormStateActivity.class);
                startActivity(intent);
            }
        });


        //로그아웃하기
        signoutButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        return v;
    }
}

