package ddwu.com.mobile.petmap

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import data.HospitalDatabase
import ddwu.com.mobile.data.Diary
import ddwu.com.mobile.petmap.databinding.ActivityAddDiaryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class AddDiaryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddDiaryBinding.inflate(layoutInflater) }
    private var currentPhotoPath: String? = null // 사진 파일 경로 저장용

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // 촬영 완료 후 미리보기 이미지뷰에 표시
            binding.ivPreview.setImageURI(Uri.parse(currentPhotoPath))
        }
    }

    // 권한 요청 런처 등록 (onCreate 위쪽에 선언)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 권한 허용됨 -> 카메라 실행 함수 호출
            openCamera()
        } else {
            Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 카메라 실행 로직
    private fun openCamera() {
        try {
            val photoFile = File.createTempFile("DIARY_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES))
            currentPhotoPath = photoFile.absolutePath

            val photoUri = FileProvider.getUriForFile(this, "ddwu.com.mobile.petmap.fileprovider", photoFile)

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                clipData = android.content.ClipData.newRawUri(null, photoUri) // 삼성 기기 대응
            }
            takePictureLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("CameraError", "전체 에러: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener {
            // 권한 확인 및 요청
            when {
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED -> {
                    // 이미 권한이 있으면 바로 실행
                    openCamera()
                }
                else -> {
                    // 권한이 없으면 요청
                    requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }
        }

        // 저장 버튼
        binding.btnClick.setOnClickListener { // XML의 저장 버튼 ID 확인 (btnClick 인지?)
            val title = binding.tvDiaryTitle.text.toString()
            val date = binding.tvDiaryDate.text.toString()
            val memo = binding.tvMemo.text.toString()

            if (title.isEmpty() || memo.isEmpty()) {
                Toast.makeText(this, "제목과 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val diary = Diary(date = date, title = title, memo = memo, photoPath = currentPhotoPath)
                HospitalDatabase.getDatabase(this@AddDiaryActivity).diaryDao().insertDiary(diary)

                withContext(Dispatchers.Main) {
                    finish()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}