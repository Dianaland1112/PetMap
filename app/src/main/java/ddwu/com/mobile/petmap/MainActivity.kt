package ddwu.com.mobile.petmap

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ddwu.com.mobile.petmap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // binding.root로 변경

        binding.searchBtn.setOnClickListener {
            val query = binding.searchText.text.toString()
            if (query.isNotEmpty()) {
                val intent = Intent(this, SearchlistActivity::class.java)
                val finalQuery = if (query.contains("병원")) query else "$query 동물병원"
                intent.putExtra("searchQuery", finalQuery) // searchQuery라는 이름에 담아 보냄
                startActivity(intent)
            }
        }

        binding.btnBookmark.setOnClickListener {
            // 즐겨찾기 목록 화면인 BookmarkActivity로 이동
            val intent = Intent(this, BookmarkActivity::class.java)
            startActivity(intent)
        }

        binding.btnDiary.setOnClickListener {
            val intent = Intent(this, DiaryActivity::class.java)
            startActivity(intent)
        }
    }

}