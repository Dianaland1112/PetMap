package ddwu.com.mobile.petmap

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import data.HospitalDatabase
import ddwu.com.mobile.petmap.databinding.ActivityDiaryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDiaryBinding.inflate(layoutInflater) }
    private val adapter by lazy { DiaryAdapter() } // 다이어리용 어댑터 필요

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rvDiaryList.adapter = adapter
        binding.rvDiaryList.layoutManager = LinearLayoutManager(this)

        // 일지 추가 버튼
        binding.btnAddDiary.setOnClickListener {
            startActivity(Intent(this, AddDiaryActivity::class.java))
        }

        // 일지 길게 눌러 삭제 (병원 삭제와 동일한 방식)
        adapter.onDeleteClick = { diary ->
            AlertDialog.Builder(this)
                .setTitle("삭제 확인")
                .setMessage("이 일지를 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        HospitalDatabase.getDatabase(this@DiaryActivity).diaryDao().deleteDiary(diary)
                        loadDiaries()
                    }
                }.show()
        }

    }

    override fun onResume() {
        super.onResume()
        loadDiaries()
    }

    private fun loadDiaries() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = HospitalDatabase.getDatabase(this@DiaryActivity).diaryDao().getAllDiaries()
            withContext(Dispatchers.Main) {
                adapter.updateItems(list)
            }
        }
    }
}