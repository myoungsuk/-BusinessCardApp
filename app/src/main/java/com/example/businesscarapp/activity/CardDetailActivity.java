package com.example.businesscarapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.businesscarapp.R;
import com.example.businesscarapp.models.CardModel;
import com.squareup.picasso.Picasso;

public class CardDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        imageView = findViewById(R.id.imageView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        // 인텐트에서 데이터 가져오기
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("card")) {
            CardModel card = intent.getParcelableExtra("card");
            if (card != null) {
                // 이미지 로드
                Picasso.get().load(card.getImageUrl()).into(imageView);

                // 설명 표시
                descriptionTextView.setText(card.getDescription());
            }
        }
    }
}