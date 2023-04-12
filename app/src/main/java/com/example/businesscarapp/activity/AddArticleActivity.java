package com.example.businesscarapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.businesscarapp.R;
import com.example.businesscarapp.models.Friend;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddArticleActivity extends AppCompatActivity
{
    private ArrayList<Friend> friendList;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference reference;
    private FirebaseUser user;
    private String uid;

    private ProgressBar progressBar;
    private Uri selectedUri;

    final int CAMERA = 100; // 카메라 선택시 인텐트로 보내는 값
    final int GALLERY = 101; // 갤러리 선택 시 인텐트로 보내는 값
    int imgFrom; // 이미지 어디서 가져왔는지 (카메라 or 갤러리)
    String imagePath = "";
    String TAG = "@@TAG@@";
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat imageDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();

        Button addImageButton = (Button) findViewById(R.id.imageAddButton);
        Button submitImageButton = (Button) findViewById(R.id.submitButton);
        ImageView profileImageView = (ImageView) findViewById(R.id.photoImageView);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        boolean hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!hasCamPerm || !hasWritePerm)  // 권한 없을 시  권한설정 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        //사진추가버튼
        addImageButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY);
            }
        });

        //사진 등록 버튼
        submitImageButton.setOnClickListener(new View.OnClickListener()
        {
            EditText title = findViewById(R.id.titleEditText);
            EditText contents = findViewById(R.id.contentsEditText);
            String writerName = auth.getCurrentUser().getDisplayName();

            @Override

            public void onClick(View view)
            {


                if (imagePath.length() > 0 && imgFrom >= 100)
                {
                    uploadImg(); // 업로드 작업 실행
                    uploadArticle();
                } else
                {
                    uploadArticle();
                    ;
                }

            }
        });


    }

    private void uploadArticle()
    {

        EditText titleEditText = findViewById(R.id.titleEditText);
        EditText contentsEditText = findViewById(R.id.contentsEditText);
        ImageView profileImageView = findViewById(R.id.photoImageView);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HashMap<String, String> hashMap = new HashMap<>();


        FirebaseUser user = auth.getCurrentUser();
        String writerName = user.getDisplayName();
        String writerId = user.getUid();
        String contents = contentsEditText.getText().toString().trim();
        String title = titleEditText.getText().toString().trim();
        String createdAt = date.toString();
        String imageUrl = String.valueOf(profileImageView);

        hashMap.put("createdAt", createdAt);
        hashMap.put("writerName", writerName);
        hashMap.put("writerId", writerId);
        hashMap.put("contents", contents);
        hashMap.put("title", title);
        hashMap.put("imageUrl", imageUrl);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Article");
        mDatabase.child("Article").child(uid).setValue(hashMap);
        // reference.child(uid).setValue(hashMap2);

        Toast.makeText(AddArticleActivity.this, "게시글을 등록했습니다", Toast.LENGTH_SHORT).show();
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView profileImageView = (ImageView) findViewById(R.id.photoImageView);

        if (resultCode == Activity.RESULT_OK)
        { // 결과가 있을 경우
//            갤러리를 선택한 경우 인텐트를 활용해 이미지 정보 가져오기
            if (requestCode == GALLERY)
            { // 갤러리 선택한 경우
                selectedUri = data.getData(); // 이미지 Uri 정보
                imagePath = data.getDataString(); // 이미지 위치 경로 정보
            } else
            {

            }
          /*  카메라를 선택할 경우, createImageFile()에서 별도의 imageFile 을 생성 및 파일 절대경로 저장을 하기 때문에
            onActivityResult()에서는 별도의 작업 필요無 */

//            저장한 파일 경로를 이미지 라이브러리인 Glide 사용하여 이미지 뷰에 세팅하기
            if (imagePath.length() > 0)
            {
                Glide.with(this)
                        .load(imagePath)
                        .into(profileImageView);
                imgFrom = requestCode; // 사진을 가져온 곳이 카메라일 경우 CAMERA(100), 갤러리일 경우 GALLERY(101)
            }
        } else
        {

        }
    }

    @SuppressLint("SimpleDateFormat")
    File createImageFile() throws IOException
    {
//        이미지 파일 생성
        String timeStamp = imageDate.format(new Date()); // 파일명 중복을 피하기 위한 "yyyyMMdd_HHmmss"꼴의 timeStamp
        String fileName = "IMAGE_" + timeStamp; // 이미지 파일 명
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(fileName,
                ".jpg",
                storageDir); // 이미지 파일 생성
        imagePath = file.getAbsolutePath(); // 파일 절대경로 저장하기
        return file;
    }

    void uploadImg()
    {
//        firebase storage 에 이미지 업로드하는 method
//        progressBar.setVisibility(View.VISIBLE);
        showProgress();
        UploadTask uploadTask = null; // 파일 업로드하는 객체
        UploadTask uploadTask2 = null;
        switch (imgFrom)
        {
            case GALLERY:
                /*갤러리 선택 시 새로운 파일명 생성 후 reference 에 경로 세팅,
                 * uploadTask 에서 onActivityResult()에서 받은 인텐트의 데이터(Uri)를 업로드하기로 설정*/
                String timeStamp = imageDate.format(new Date()); // 중복 파일명을 막기 위한 시간스탬프
                String imageFileName = "IMAGE_" + timeStamp + "_.png"; // 파일명

                reference = storage.getReference().child("Article").child(uid).child("photo").child(imageFileName); // 이미지 파일 경로 지정 (/item/imageFileName)


                uploadTask = reference.putFile(selectedUri); // 업로드할 파일과 업로드할 위치 설정


                break;
        }

//        파일 업로드 시작
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
//            업로드 성공 시 동작
                hideProgress();
//                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onSuccess: upload");
                downloadUri(); // 업로드 성공 시 업로드한 파일 Uri 다운받기


            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
//                업로드 실패 시 동작
                hideProgress();
                Log.d(TAG, "onFailure: upload");
            }
        });
    }

    void downloadUri()
    {
        showProgress();
//        지정한 경로(reference)에 대한 uri 을 다운로드하는 method
//        progressBar.setVisibility(View.VISIBLE);
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri)
            {
//                uri 다운로드 성공 시 동작
                hideProgress();
//                다운받은 uri를 인텐트에 넣어 다른 액티비티로 이동
//                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onSuccess: download");

                finish();


            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                hideProgress();
//                uri 다운로드 실패 시 동작
//                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: download");
            }
        });
    }

    void showProgress()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgress()
    {
        progressBar.setVisibility(View.GONE);
    }
}