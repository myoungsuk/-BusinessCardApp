package com.example.businesscarapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.businesscarapp.models.CardModel;

import com.example.businesscarapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class AddCardActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 2;
    private Uri imageUri;
    private ImageView imageView;
    private EditText descriptionEditText;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private Button captureButton;
    private Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_photo);

        // Firebase 설정
        databaseReference = FirebaseDatabase.getInstance().getReference("bscard");
        storageReference = FirebaseStorage.getInstance().getReference();

        // 뷰 초기화
       ImageView imageView = findViewById(R.id.imageView);
       EditText descriptionEditText = findViewById(R.id.descriptionEditText);
       Button captureButton = findViewById(R.id.captureButton);
       Button uploadButton = findViewById(R.id.uploadButton);

        // ActivityResultLauncher 초기화
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Bundle extras = data.getExtras();
                                if (extras != null) {
                                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                                    if (imageBitmap != null) {
                                        imageView.setImageBitmap(imageBitmap);
                                        // 이미지 URI 설정
                                        imageUri = getImageUri(imageBitmap);
                                        uploadImage(); // 이미지 업로드
                                    } else {
                                        Toast.makeText(AddCardActivity.this, "사진을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
                });

        // 사진 찍기 버튼 클릭 이벤트
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        // 업로드 버튼 클릭 이벤트
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(); // 사진 업로드
            }
        });
    }

    // 카메라 권한 확인
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    // 카메라 앱 실행
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    // 카메라 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "카메라 권한을 허용해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 비트맵 이미지를 URI로 변환하는 메서드
    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void uploadImage() {
        if (imageUri != null) {
            // 이미지 파일 이름 설정 (현재 시간 기준)
            String imageName = String.valueOf(System.currentTimeMillis());

            // 이미지 파일을 Firebase Storage에 업로드
            StorageReference imageRef = storageReference.child("bscard/" + imageName);
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // 이미지 업로드 성공 시, 다운로드 URL 가져오기
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // 업로드된 이미지와 추가 설명을 포함한 CardModel 생성
                            String imageUrl = uri.toString();
                            String description = descriptionEditText.getText().toString();
                            CardModel card = new CardModel(imageUrl, description); // 수정된 부분

                            // Firebase Database에 CardModel 업로드
                            String key = databaseReference.push().getKey();
                            if (key != null) {
                                databaseReference.child(key).setValue(card)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(AddCardActivity.this, "사진 업로드 완료", Toast.LENGTH_SHORT).show();
                                            // 입력 필드 초기화
                                            imageView.setImageResource(0);
                                            descriptionEditText.setText("");
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(AddCardActivity.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show());
                            }
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddCardActivity.this, "사진 업로드 실패", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "사진을 먼저 찍어주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}