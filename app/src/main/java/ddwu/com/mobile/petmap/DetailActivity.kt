package ddwu.com.mobile.petmap

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import data.HospitalDatabase
import ddwu.com.mobile.data.MyHos
import ddwu.com.mobile.petmap.databinding.ActivityDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.getValue

// 해당 동물병원을 즐겨찾기에 추가할지
class DetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 1. 전달받은 데이터 추출
        val name = intent.getStringExtra("name") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val phone = intent.getStringExtra("phone")
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lng = intent.getDoubleExtra("lng", 0.0)
        val placeId = intent.getStringExtra("placeId") ?: ""

        // 2. "예" 버튼 클릭 시 저장
        binding.btnYes.setOnClickListener {
            val entity = MyHos(
                placeId = placeId,
                name = name,
                address = address,
                phone = phone,
                latitude = lat,
                longitude = lng
            )

            CoroutineScope(Dispatchers.IO).launch {
                HospitalDatabase.getDatabase(this@DetailActivity).hospitalDao().insertHospital(entity)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailActivity, "즐겨찾기에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.btnNo.setOnClickListener { finish() }
    }
}