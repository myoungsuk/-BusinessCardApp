package com.example.businesscarapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

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

public class CustomDialog extends DialogFragment
{
    private ArrayList<Friend> friendList = new ArrayList<>();
    private Friend friend;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Uri imageUri;
    private StorageReference firebaseStorage;
    private FirebaseUser user;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.custom_dialog, container, false);
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();
        uid = user.getUid();

        EditText nameEditText = v.findViewById(R.id.name_edit);
        EditText emailEditText = v.findViewById(R.id.email_edit);
        EditText descriptionText = v.findViewById(R.id.desc_edit);

        Button cancleButton = v.findViewById(R.id.cancel_button);
        Button okayButton = v.findViewById(R.id.finish_button);

        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Friend friend = snapshot.getValue(Friend.class);

                String name = friend.name;
                String email = friend.email;
                String description = friend.description;

                nameEditText.setText(name);
                emailEditText.setText(email);
                descriptionText.setText(description);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                buttonClickListener.onButton1Clicked();
                dismiss();
            }
        });
        okayButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                buttonClickListener.onButton2Clicked(
                        nameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        descriptionText.getText().toString()
                );

            }
        });


        return v;
    }

    public interface OnButtonClickListener
    {
        void onButton1Clicked();

        void onButton2Clicked(String nameEdit, String emailEdit, String descriptionEdit);
    }

    public void setButtonClickListener(OnButtonClickListener buttonClickListener)
    {
        this.buttonClickListener = buttonClickListener;
    }

    private OnButtonClickListener buttonClickListener;
}
