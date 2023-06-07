package com.example.businesscarapp.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.businesscarapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity
{

    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // 상태바 없애기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(WindowInsets.Type.statusBars());
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        ImageButton sendButton = (ImageButton) findViewById(R.id.sendButton);
        Button backButton = (Button) findViewById(R.id.btn_back);

        sendButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                send();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PasswordResetActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void send()
    {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        EditText emailText = (EditText) findViewById(R.id.emailEditText);
        String email = emailText.getText().toString().trim();

        if (email.length() > 0)
        {
            progressBar.setVisibility(View.VISIBLE);
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PasswordResetActivity.this, "이메일을 보냈습니다 메일을 확인해 주세요", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PasswordResetActivity.this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}