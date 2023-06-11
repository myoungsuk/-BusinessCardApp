package com.example.businesscarapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.businesscarapp.R;
import com.example.businesscarapp.models.CardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CardListActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private List<CardModel> cardList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        // Firebase 설정
        databaseReference = FirebaseDatabase.getInstance().getReference("bscard");

        listView = findViewById(R.id.listView);
        cardList = new ArrayList<>();

        // CardModel 데이터 가져오기
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CardModel card = dataSnapshot.getValue(CardModel.class);
                    cardList.add(card);
                }

                // ListView에 목록 표시
                ArrayAdapter<CardModel> adapter = new ArrayAdapter<>(CardListActivity.this, android.R.layout.simple_list_item_1, cardList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error 처리
            }
        });

        // ListView 아이템 클릭 이벤트 처리
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 선택된 아이템의 데이터 가져오기
                CardModel selectedCard = cardList.get(position);

                // 상세 화면으로 이동 및 데이터 전달
                Intent intent = new Intent(CardListActivity.this, CardDetailActivity.class);
                intent.putExtra("card", selectedCard);
                startActivity(intent);
            }
        });
    }
}