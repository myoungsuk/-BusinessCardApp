package com.example.businesscarapp.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import com.example.businesscarapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AddCardPhotoActivity : AppCompatActivity()
{
    private val mDatabase: DatabaseReference? = null
    private var imageUri: Uri? = null
    private val fireStorage = FirebaseStorage.getInstance().reference
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private val user = Firebase.auth.currentUser
    var database = FirebaseDatabase.getInstance()

    // Get current user's uid

    // Get current user's uid
    var uid = FirebaseAuth.getInstance().currentUser!!.uid
    val myUid = Firebase.auth.currentUser?.uid.toString()


    private var selectedUri: Uri? = null //uri 변수호출
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }                                   //firebase 호출

    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //사진 추가버튼 활성화시키기
        findViewById<Button>(R.id.imageAddButton).setOnClickListener {
            when
            {
                //사진 허가받고 사진올리는과정
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED ->
                {
                    startContentProvider()
                }
                //교육용 팝업이 필요한 경우
                shouldShowRequestPermissionRationale(
                    android.Manifest.permission
                        .READ_EXTERNAL_STORAGE
                ) ->
                {
                    showPermissionContextPopup()
                }

                else ->
                {
                    //그외에 경우에는 해당권한에 대해서 요청하는 코드
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1010
                    )
                }

            }
        }


        //사진 등록


        findViewById<Button>(R.id.submitButton).setOnClickListener {
            showProgress()

            if (selectedUri != null)
            {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadPhoto(photoUri)
            } else
            {
                hideProgress()
                Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
        private fun uploadPhoto(imageUri: Uri) {
            fireStorage.child("friendImages/$uid/friendPhoto").putFile(
                imageUri!!
            ).addOnSuccessListener {
                fireStorage.child("friendImages/$uid/friendPhoto")
                    .downloadUrl.addOnSuccessListener { photoUri ->
                        val imageUrl = photoUri.toString()
                        val idCardUid = intent.getStringExtra("uid")
                        val uid = FirebaseAuth.getInstance().currentUser!!.uid
                        mDatabase?.child("IDcards")?.child(uid)?.child("$idCardUid")?.child("profileImageUrl")?.setValue(imageUrl)
                            ?.addOnSuccessListener {
                                hideProgress()
                                Toast.makeText(this, "친구 프로필 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            ?.addOnFailureListener {
                                hideProgress()
                                Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
        }



        override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode)
        {
            1010 -> //승낙이 됬는지 확인한다
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startContentProvider()
                } else
                {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun startContentProvider()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*" //이미지 타입만 가져오도록
        activityResultLauncher.launch(intent)

    }

    private fun showProgress()
    {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = true
    }

    private fun hideProgress()
    {
        findViewById<ProgressBar>(R.id.progressBar).isVisible = false
    }

    // if (result.resultCode == RESULT_OK) {
//                imageUri = result.data?.data //이미지 경로 원본
//                profile.setImageURI(imageUri) //이미지 뷰를 바꿈
    //activityresultlauncher 처리하기 위한 설정
    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        //result.getResultCode()를 통하여 결과값 확인
        if (it.resultCode == RESULT_OK)
        {
            val uri = it.data?.data
            if (uri != null)
            {
                findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
                selectedUri = uri
            } else
            {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        } else
        {
            Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
        if (it.resultCode == RESULT_CANCELED)
        {
            //ToDo
            return@registerForActivityResult
        }
    }

    //교육용 팝업 함수
    private fun showPermissionContextPopup()
    {
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
