package com.example.businesscarapp.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.businesscarapp.ArticleModel;
import com.example.businesscarapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private ArrayList<ArticleModel> ArticleList = null;

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Uri imageUri;
    private StorageReference firebaseStorage;
    private FirebaseUser user;
    private String uid;
    private ArticleModel articleModel;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView dateTextView;
        TextView contents;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            contents = itemView.findViewById(R.id.contentsTextView);

            String dateText = dateTextView.getText().toString();
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateText = dateFormat.format(date);

        }
    }


    public ArticleAdapter(ArrayList<ArticleModel> ArticleList) {
        this.ArticleList = ArticleList;

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();

        FirebaseDatabase.getInstance().getReference().child("Article").addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                ArticleList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    ArticleModel articleModel = dataSnapshot.getValue(ArticleModel.class);
                    ArticleList.add(articleModel);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        ArticleModel articleModel = ArticleList.get(position);
//        if (articleModel.imageUrl.equals(""))
//        {
//
//        } else
//        {
            Glide.with(holder.itemView.getContext())
                    .load(ArticleList.get(position).imageUrl)
                    .into(holder.thumbnailImageView);

        holder.titleTextView.setText(ArticleList.get(position).title);
        holder.contents.setText(ArticleList.get(position).contents);
        holder.dateTextView.setText(ArticleList.get(position).createdAt);


    }



    @Override
    public int getItemCount()
    {
        return ArticleList.size();
    }






}
