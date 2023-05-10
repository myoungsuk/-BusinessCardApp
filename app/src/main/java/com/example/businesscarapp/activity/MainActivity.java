package com.example.businesscarapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.businesscarapp.R;
import com.example.businesscarapp.fragment.ChatFragment;
import com.example.businesscarapp.fragment.FriendFragment;
import com.example.businesscarapp.fragment.HomeFragment;
import com.example.businesscarapp.fragment.NoticeFragment;
import com.example.businesscarapp.fragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "Main_Activity";
    private BottomNavigationView mBottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBottomNavigationView=findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer,new HomeFragment()).commit();

        //case 함수를 통해 클릭 받을 때마다 화면 변경하기
        mBottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener()  {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {

                switch (item.getItemId()){
                    case R.id.home :
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new HomeFragment()).commit();
                        break;
                    case R.id.friend:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new FriendFragment()).commit();
                        break;
                    case R.id.chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new ChatFragment()).commit();
                        break;
                    case R.id.notice :
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new NoticeFragment()).commit();
                        break;
                    case R.id.settings :
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new SettingFragment()).commit();
                        break;
                }
                return true;
            }
        });
    }
}