package com.example.businesscarapp.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.businesscarapp.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OCRActivity extends AppCompatActivity {

    Bitmap image; //사용되는 이미지
    private TessBaseAPI mTess; //Tess API reference
    String datapath = "" ; //언어데이터가 있는 경로

    Button btn_picture; //사진 찍는 버튼
    Button btn_ocr; //텍스트 추출 버튼

    private String imageFilePath; //이미지 파일 경로
    private Uri p_Uri;

    static final int REQUEST_IMAGE_CAPTURE = 672;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        runtimeCheckPermission();
        //이미지 디코딩을 위한 초기화
        //image = BitmapFactory.decodeResource(getResources(), R.drawable.img_kor); //샘플이미지파일

        btn_picture = (Button)findViewById(R.id.takePicture);
        btn_ocr = (Button)findViewById(R.id.ocrButton);

        //언어파일 경로
        datapath = getFilesDir()+ "/tesseract/";

        //트레이닝데이터가 카피되어 있는지 체크
        checkFile(new File(datapath + "tessdata/"), "kor");
        checkFile(new File(datapath + "tessdata/"), "eng");

        String lang = "kor+eng";

        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);

        // 사진 찍는 버튼 클릭시 카메라 킴
        btn_picture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sendTakePhotoIntent();
            }
        });

        // 텍스트 추출 버튼
        btn_ocr.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // 가져와진 사진을 bitmap으로 추출
                BitmapDrawable d = (BitmapDrawable)((ImageView) findViewById(R.id.imageView)).getDrawable();
                image = d.getBitmap();

                String OCRresult = null;
                mTess.setImage(image);

                //텍스트 추출
                OCRresult = mTess.getUTF8Text();
                TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
                OCRTextView.setText(OCRresult);
            }
        });
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void runtimeCheckPermission() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int permission2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int permission3 = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        1000);
            }
            return;
        }
    }

    private void sendTakePhotoIntent(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                p_Uri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, p_Uri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ((ImageView) findViewById(R.id.imageView)).setImageURI(p_Uri);
            ExifInterface exif = null;

            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);
            } else {
                exifDegree = 0;
            }
            ((ImageView)findViewById(R.id.imageView)).setImageBitmap(rotate(bitmap, exifDegree));
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".png",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    //장치에 파일 복사
    private void copyFiles(String lang) {
        try{
            //파일이 있을 위치
            String filepath = datapath + "/tessdata/"+lang+".traineddata";

            //AssetManager에 액세스
            AssetManager assetManager = getAssets();

            //읽기/쓰기를 위한 열린 바이트 스트림
            InputStream instream = assetManager.open("tessdata/"+lang+".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //filepath에 의해 지정된 위치에 파일 복사
            byte[] buffer = new byte[1024];
            int read;

            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //check file on the device
    private void checkFile(File dir, String lang) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles(lang);
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/"+lang+".traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles(lang);
            }
        }
    }
}