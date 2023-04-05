package com.example.businesscarapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.businesscarapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{

    private static LottieAnimationView register;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private Uri imageUri;
    private ProgressBar progressBar;
    private EditText emailEditText, passwordEditText, passwordCheckEditText, et_registration_name;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        auth = FirebaseAuth.getInstance();
        //firebase 정의
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ImageButton btnSignIn = findViewById(R.id.signUpButton);
        Button btnGotoLogin = findViewById(R.id.gotoLoginButton);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText passwordCheckEditText = findViewById(R.id.passwordCheckEditText);
        EditText nameEditText = findViewById(R.id.et_registration_name);

        btnGotoLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });



        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String passwordCheck = passwordCheckEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6)
                {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                if(!email.equals("") && !password.equals("") && !passwordCheckEditText.getText().toString().equals("")) {
                    createUser(email, password, nameEditText.getText().toString(), "");

                    progressBar.setVisibility(View.GONE);
                }else {
                    // 이메일과 비밀번호가 공백인 경우
                    Toast.makeText(RegisterActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void createUser(String email, String password, String name, String profileImageUrl) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = auth.getCurrentUser();
                            String email = user.getEmail();
                            String uid = user.getUid();
                            EditText nameEditText = findViewById(R.id.et_registration_name);
                            String name = nameEditText.getText().toString().trim();

                            HashMap<Object,String> hashMap = new HashMap<>();

                            hashMap.put("uid", uid);
                            hashMap.put("email", email);
                            hashMap.put("name", name);
                            hashMap.put("profileImageUrl", profileImageUrl);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            finish();
                            //테스트

                        } else
                        {
                            // 계정이 중복된 경우
                            Toast.makeText(RegisterActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}