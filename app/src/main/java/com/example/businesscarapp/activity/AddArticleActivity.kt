package com.example.businesscarapp.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.businesscarapp.DBkey.DB_ARTICLES
import com.example.businesscarapp.R
import com.example.businesscarapp.models.ArticleModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddArticleActivity : AppCompatActivity() {

    private var selectedUri: Uri? = null //uri 변수호출
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }                                   //firebase 호출

    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }

    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_article)


        // 상태바 없애기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        //사진 추가버튼 활성화시키기
        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
            when {
                //사진 허가받고 사진올리는과정
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }
                //교육용 팝업이 필요한 경우
                shouldShowRequestPermissionRationale(
                    android.Manifest.permission
                        .READ_EXTERNAL_STORAGE
                ) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    //그외에 경우에는 해당권한에 대해서 요청하는 코드
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }

            }
        }
        findViewById<Button>(R.id.submitButton).setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val contents = findViewById<EditText>(R.id.contentsEditText).text.toString()
            val writerId = auth.currentUser?.uid.orEmpty()

            showProgress()

            // 중간에 이미지가 있으면 업로드 과정을 추가
            if (selectedUri != null) { //비동기일때도 함수실행
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri,
                    successHandler = { uri -> //성공했을때 데이터 이동
                        uploadArticle(writerId, title, contents, uri)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        hideProgress() //프로그래스 화면 없애기
                    }
                )
            } else { //동기일때도 함수 실행
                uploadArticle(writerId, title, contents, "")
            }
        }

    }


    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = "${System.currentTimeMillis()}.png" //파일 이름 설정
        storage.reference.child("article/photo").child(fileName) // 파일 저장소 설정
            .putFile(uri) //파일 넣기
            .addOnCompleteListener { //리스너로 성공했는지 안했는지 확인
                if (it.isSuccessful) {
                    storage.reference.child("article/photo").child(fileName)
                        .downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    }

    //아티클 업로드 함수
    private fun uploadArticle(writerId: String, title: String, contents: String, imageUrl: String) {
        val model = ArticleModel(writerId, title, System.currentTimeMillis(), contents, imageUrl)
        articleDB.push().setValue(model)

        hideProgress()

        finish()


    }

    //퍼미션 요청에 대한 함수 오버라이드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1010 -> //승낙이 됬는지 확인한다
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*" //이미지 타입만 가져오도록
        activityResultLauncher.launch(intent)

    }

    private fun showProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true
    }

    private fun hideProgress() {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }

    //activityresultlauncher 처리하기 위한 설정
    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        //result.getResultCode()를 통하여 결과값 확인
        if (it.resultCode == RESULT_OK) {
            //ToDo
            val uri = it.data?.data
            if (uri != null) {
                findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
                selectedUri = uri
            } else {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
        if (it.resultCode == RESULT_CANCELED) {
            //ToDo
            return@registerForActivityResult
        }
    }

    //교육용 팝업 함수
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오기 위해 필요합니다.")
            .setPositiveButton("동의") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()

    }

}
