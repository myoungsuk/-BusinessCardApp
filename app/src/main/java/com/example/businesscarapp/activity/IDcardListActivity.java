package com.example.businesscarapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.businesscarapp.IDcard;
import com.example.businesscarapp.R;
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

import java.util.ArrayList;

public class IDcardListActivity extends AppCompatActivity {

    private ArrayList<IDcard> idcardList;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Uri imageUri;
    private StorageReference firebaseStorage;
    private FirebaseUser user;
    private String uid;

    private RecyclerView recyclerView;
    private IDcardRecyclerViewAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcardlist);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();
        uid = user.getUid();

        idcardList = new ArrayList<>();
        recyclerView = findViewById(R.id.idcardRecyclerView);
        mRecyclerAdapter = new IDcardRecyclerViewAdapter(idcardList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    class IDcardRecyclerViewAdapter extends RecyclerView.Adapter<IDcardRecyclerViewAdapter.MyViewHolder> {
        private ArrayList<IDcard> idcardList;
        private AlertDialog.Builder alertDialog;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView idcardname, idcardemail;
            public ImageView idcardprofile;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                idcardname = itemView.findViewById(R.id.idcard_item_tv);
                idcardemail = itemView.findViewById(R.id.idcard_item_email);
                idcardprofile = itemView.findViewById(R.id.idcard_item_iv);
            }
        }

        public IDcardRecyclerViewAdapter(ArrayList<IDcard> list) {
            this.idcardList = list;
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            uid = user.getUid();

            FirebaseDatabase.getInstance().getReference().child("IDcards").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        IDcard item = dataSnapshot.getValue(IDcard.class);
                        list.add(item);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_idcard, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            IDcard idcard = idcardList.get(position);
            if (idcard.getProfileImageUrl().equals("")) {

            } else {
                Glide.with(holder.itemView.getContext())
                        .load(idcardList.get(position).getProfileImageUrl())
                        .apply(new RequestOptions().circleCrop())
                        .into(holder.idcardprofile);
            }
            holder.idcardname.setText(idcardList.get(position).getName());
            holder.idcardemail.setText(idcardList.get(position).getEmail());

            String holderPhoto = idcard.getProfileImageUrl();
            String holderName = idcard.getName();
            String holderEmail = idcard.getEmail();
            String adapterUid = idcard.getUid();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog = new AlertDialog.Builder(IDcardListActivity.this);
                    alertDialog.setView(R.layout.profile_dialog);
                    final AlertDialog dialog = alertDialog.create();
                    dialog.show();

                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    ImageView photoImage = dialog.findViewById(R.id.iv_circle_image);
                    TextView emailText = dialog.findViewById(R.id.tv_text2);
                    TextView nameText = dialog.findViewById(R.id.tv_text1);
                    MaterialButton cancleButton = dialog.findViewById(R.id.btn_cancel);
                    MaterialButton messageButton = dialog.findViewById(R.id.btn_call);

                    emailText.setText(holderEmail);
                    nameText.setText(holderName);

                    Glide.with(IDcardListActivity.this)
                            .load(holderPhoto)
                            .apply(new RequestOptions().circleCrop())
                            .into(photoImage);

                    cancleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    messageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(IDcardListActivity.this, MessageActivity.class);
                            intent.putExtra("destinationUid", adapterUid);
                            startActivity(intent);
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return idcardList.size();
        }
    }
}
