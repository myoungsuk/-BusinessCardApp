package com.example.businesscarapp.activity;


import static com.example.businesscarapp.DBkey.DB_IDCARDS;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.businesscarapp.R;
import com.example.businesscarapp.fragment.HomeFragment;
import com.example.businesscarapp.models.IDcard;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class CloudVisionAPIActivity extends AppCompatActivity
{
    //    private static final String CLOUD_VISION_API_KEY = BuildConfig.API_KEY;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDYqi3NYMninIEnrFkdOWQwcQjWwbAYauE";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = CloudVisionAPIActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private ImageView mMainImage;

    private FirebaseUser user;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private Button saveButton;

    private DatabaseReference IDcardDB = FirebaseDatabase.getInstance().getReference().child(DB_IDCARDS);


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloudvisionapi);


        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view ->
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(CloudVisionAPIActivity.this);
            builder
                    .setMessage(R.string.dialog_select_prompt)
                    .setPositiveButton(R.string.dialog_select_gallery, (dialog, which) -> startGalleryChooser())
                    .setNegativeButton(R.string.dialog_select_camera, (dialog, which) -> startCamera());
            builder.create().show();
        });

        mImageDetails = findViewById(R.id.image_details);
        mMainImage = findViewById(R.id.main_image);
    }

    public void startGalleryChooser()
    {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, android.Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera()
    {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA))
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile()
    {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null)
        {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK)
        {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults))
                {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults))
                {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri)
    {
        if (uri != null)
        {
            try
            {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);

                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e)
            {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else
        {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException
    {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY)
                {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException
                    {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>()
        {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            annotateImageRequest.setFeatures(new ArrayList<Feature>()
            {{
                Feature textDetection = new Feature();
                textDetection.setType("TEXT_DETECTION");
                textDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(textDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private class LabelDetectionTask extends AsyncTask<Object, Void, String>
    {
        private final WeakReference<CloudVisionAPIActivity> mActivityWeakReference;
        private final Vision.Images.Annotate mRequest;

        LabelDetectionTask(CloudVisionAPIActivity activity, Vision.Images.Annotate annotate)
        {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params)
        {
            try
            {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                String text = Arrays.toString(convertResponseToString(response));

                return text;

            } catch (GoogleJsonResponseException e)
            {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e)
            {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result)
        {
            CloudVisionAPIActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing())
            {
                TextView imageDetail = activity.findViewById(R.id.image_details);
                imageDetail.setText(result);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap)
    {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try
        {
            AsyncTask<Object, Void, String> labelDetectionTask = new LabelDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e)
        {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension)
    {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth)
        {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight)
        {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth)
        {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }


    private void saveUserData(String name, String studentId, String department, String school, String description)
    {
        IDcard cardModel = new IDcard(name, studentId, department, school, description);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get current user's uid
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reference = database.getReference("IDcard").child(uid).push(); // create unique id for each data

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", cardModel.getName());
        hashMap.put("studentId", cardModel.getStudentId());
        hashMap.put("school", cardModel.getSchool());
        hashMap.put("department", cardModel.getDepartment());
        hashMap.put("description", cardModel.getDescription());

        reference.setValue(hashMap);  // save data under the uid of current user with unique id

        Toast.makeText(CloudVisionAPIActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
    }


    private String[] convertResponseToString(BatchAnnotateImagesResponse response)
    {
        // 응답으로부터 라벨을 얻습니다.
        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();

        if (labels != null)
        {
            // 라벨의 수와 동일한 크기로 배열을 초기화합니다.
            String[] txt = new String[labels.size()];

            // 라벨을 반복하고 설명을 배열에 저장합니다.
            for (int i = 0; i < labels.size(); i++)
            {
                txt[i] = labels.get(i).getDescription();

                // 디버깅 목적으로, 설명을 출력할 수 있습니다.
                Log.d(TAG, "Message: " + txt[i]);
            }
            // 명함 데이터 담는 변수
//            String name = ""; // 이름
            StringBuilder name = new StringBuilder();
//            StringBuilder univ = new StringBuilder();
            // Initialize variables to hold university name components
            StringBuilder universityNameBuilder = new StringBuilder();

            String univ = ""; // 학교
            String dept = ""; // 전공
            String snum = ""; // 학번
            String uid = ""; // uid

            String email = ""; // 이메일
            String phoneNum = ""; // 폰번호
            String tel = ""; // 회사 번호
            String fax = ""; // 팩스

            // TODO: 필요한 형식 더 추가하기
            // parsing


            for (int i = 0; i < txt.length; i++)
            {


                if (txt[i].startsWith("201") || txt[i].startsWith("202"))
                {
                    snum = txt[i].substring(0, Math.min(txt[i].length(), 10));

                    // 학번 추출 후 바로 뒤에 있는 한글 이름 추출

                    int j = i + 1;
                    while (j < txt.length && txt[j].matches("[가-힣]+"))
                    {
                        name.append(txt[j]);
                        j++;
                    }
                    if (name.length() > 0)
                    {
                        System.out.println("이름: " + name.toString());
//                        break;
                    }
                    // 학번 추출 후 바로 뒤에 있는 한글 이름 추출
//                    name = txt[i + 1];
//                    if (i + 1 < txt.length)
//                    {
//                         name = txt[i + 1];
//                        if (name.length() >= 2 && name.length() <= 4 && name.matches("[가-힣]+"))
//                        {
//                            System.out.println("이름: " + name);
//                            break;
//                        }
//                    }
                }


//                // 학번
//                if (txt[i].contains("201") || txt[i].contains("202")) {
//                    if (txt[i].contains("201")) {
//                        snum = txt[i].substring(0, 10);
//                    } else if (txt[i].contains("202")) {
//                        snum = txt[i].substring(0, 10);
//                    }
//                }

//                // 이름
//                name = txt[18]; // 충북대 학생증 기준 배열 18번째가 이름

//                // 학교
                if (i > 0 && txt[i].contains("대학교"))
                {
                    String keyword = "대학교";
                    univ = txt[i - 1] + keyword;
                    System.out.println("학교: " + univ);
                }

                // Loop through the text array

//                if (txt[i].equals("대학교"))
//                {
//                    // Add the current word to the university name components
//                    universityNameBuilder.append(txt[i - 1]);
//                    universityNameBuilder.append(txt[i]);
//
//                    // Assign the combined university name components to the final university name
//                    univ = universityNameBuilder.toString();
//                    System.out.println("학교: " + univ);
//                    break; // Stop the loop once we have extracted the university name
//                } else if (txt[i].equals("CHUNG") || txt[i].equals("BUY") || txt[i].equals("UNIVERSIT"))
//                {
//                    // If the current word is a part of the university name, add it to the university name components
//                    universityNameBuilder.append(txt[i]);
//                    universityNameBuilder.append(" ");
//                }


                // 전공
                if (txt[i].contains("학과") || txt[i].contains("학부") || txt[i].contains("전공"))
                {
                    if (i > 0 && txt[i].contains("학과"))
                    {
                        String keyword = "학과";
                        dept = txt[i - 1] + keyword;
                    } else if (i > 0 && txt[i].contains("학부"))
                    {
                        String keyword = "학부";
                        dept = txt[i - 1] + keyword;
                    } else if (i > 0 && txt[i].contains("전공"))
                    {
                        String keyword = "전공";
                        dept = txt[i - 1] + keyword;
                    }
                }

                // 휴대폰 번호 (M)
                if (txt[i].contains("-") && txt[i].contains("010") || txt[i].contains("82"))
                {
                    if (txt[i].contains("M."))
                    {
                        phoneNum = txt[i].replace("M.", " ").trim().substring(0, 13);
                    } else if (txt[i].contains("M"))
                    {
                        phoneNum = txt[i].replace("M", " ").trim().substring(0, 13);
                    } else
                    {
                        phoneNum = txt[i].trim();
                    }
                }

                // companyTel (T)
                if (txt[i].contains("-") && txt[i].contains("T."))
                {
                    String telA = txt[i].substring(txt[i].indexOf("T."));
                    if (telA.length() >= 15)
                    {
                        tel = txt[i].substring(txt[i].indexOf("T."), txt[i].indexOf("T.") + 15).replace("T.", " ").trim();
                    } else if (telA.length() < 15 || telA.length() >= 14)
                    {
                        tel = txt[i].substring(txt[i].indexOf("T."), txt[i].indexOf("T.") + 14).replace("T.", " ").trim();
                    } else
                    {
                        tel = txt[i].substring(txt[i].indexOf("T."), telA.length()).replace("T.", " ").trim();
                    }
                } else if (txt[i].contains("-") && txt[i].contains("T"))
                {
                    String telA = txt[i].substring(txt[i].indexOf("T"));
                    if (telA.length() >= 15)
                    {
                        tel = txt[i].substring(txt[i].indexOf("T"), txt[i].indexOf("T") + 15).replace("T", " ").trim();
                    } else if (telA.length() < 15 || telA.length() >= 14)
                    {
                        tel = txt[i].substring(txt[i].indexOf("T"), txt[i].indexOf("T") + 14).replace("T", " ").trim();
                    } else
                    {
                        tel = txt[i].substring(txt[i].indexOf("T"), telA.length()).replace("T", " ").trim();
                    }
                }

                // fax (F)
                if (txt[i].contains("-") && txt[i].contains("F."))
                {
                    String faxA = txt[i].substring(txt[i].indexOf("F."));
                    if (faxA.length() >= 15)
                    {
                        fax = txt[i].substring(txt[i].indexOf("F."), txt[i].indexOf("F.") + 15).replace("F.", " ").trim();
                    } else if (faxA.length() < 15 || faxA.length() >= 14)
                    {
                        fax = txt[i].substring(txt[i].indexOf("F."), txt[i].indexOf("F.") + 14).replace("F.", " ").trim();
                    } else
                    {
                        fax = txt[i].substring(txt[i].indexOf("F."), faxA.length()).replace("F.", " ").trim();
                    }
                } else if (txt[i].contains("-") && txt[i].contains("F,"))
                {
                    String faxA = txt[i].substring(txt[i].indexOf("F,"));
                    if (faxA.length() >= 15)
                    {
                        fax = txt[i].substring(txt[i].indexOf("F,"), txt[i].indexOf("F,") + 15).replace("F,", " ").trim();
                    } else if (faxA.length() < 15 || faxA.length() >= 14)
                    {
                        fax = txt[i].substring(txt[i].indexOf("F,"), txt[i].indexOf("F,") + 14).replace("F,", " ").trim();
                    } else
                    {
                        fax = txt[i].substring(txt[i].indexOf("F,"), faxA.length()).replace("F,", " ").trim();
                    }
                } else if (txt[i].contains("-") && txt[i].contains("F"))
                {
                    String faxA = txt[i].substring(txt[i].indexOf("F"));
                    if (faxA.length() >= 15)
                    {
                        fax = txt[i].substring(txt[i].indexOf("F"), txt[i].indexOf("F") + 15).replace("F", " ").trim();
                    } else if (faxA.length() < 15 || faxA.length() >= 14)
                    {
                        fax = txt[i].substring(txt[i].indexOf("F"), txt[i].indexOf("F") + 14).replace("F", " ").trim();
                    } else
                    {
                        fax = txt[i].substring(txt[i].indexOf("F"), faxA.length()).replace("F", " ").trim();
                    }
                }

                // 이메일
                if (txt[i].contains("@"))
                {
                    if (txt[i].contains("E."))
                    {
                        if (txt[i].contains(".com"))
                        {
                            email = txt[i].substring(txt[i].indexOf("E."), txt[i].indexOf(".com") + 4).replace("E.", " ").trim();
                        } else if (txt[i].contains(".kr"))
                        {
                            email = txt[i].substring(txt[i].indexOf("E."), txt[i].indexOf(".kr") + 3).replace("E.", " ").trim();
                        }
                    } else if (txt[i].contains("E"))
                    {
                        if (txt[i].contains(".com"))
                        {
                            email = txt[i].substring(txt[i].indexOf("E"), txt[i].indexOf(".com") + 4).replace("E", " ").trim();
                        } else if (txt[i].contains(".kr"))
                        {
                            email = txt[i].substring(txt[i].indexOf("E"), txt[i].indexOf(".kr") + 3).replace("E", " ").trim();
                        }
                    } else
                    {
                        email = txt[i];
                    }
                }
            }

            EditText nameEditText = findViewById(R.id.nameEditText);
            nameEditText.setText(name);


            EditText snumEditText = findViewById(R.id.snumEditText);
            snumEditText.setText(snum);

            EditText deptEditText = findViewById(R.id.deptEditText);
            deptEditText.setText(dept);

            EditText univEditText = findViewById(R.id.univEditText);
            univEditText.setText(univ);

            EditText descriptionText = findViewById(R.id.decriptionEditText);


            String finalName = nameEditText.getText().toString();
            String finalUniv = univ;
            String finalDept = dept;
            String finalSnum = snum;
            String finalDescription = descriptionText.getText().toString();

            saveButton = (Button) findViewById(R.id.saveButton);
            saveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    String finalName = nameEditText.getText().toString();
                    String finalUniv = univEditText.getText().toString();
                    String finalDept = deptEditText.getText().toString();
                    String finalSnum = snumEditText.getText().toString();
                    String finalDescription = descriptionText.getText().toString();

                    saveUserData(finalName, finalSnum, finalUniv, finalDept, finalDescription);

                    // Replace the current fragment with the HomeFragment
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });


            // 배열을 반환합니다.
            return txt;
        } else
        {
            // 라벨이 없는 경우, 빈 배열을 반환하거나, 사용 사례에 따라 null을 반환할 수 있습니다.
            return new String[0];

        }
    }
}
