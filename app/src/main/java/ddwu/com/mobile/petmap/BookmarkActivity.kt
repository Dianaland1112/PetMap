package ddwu.com.mobile.petmap

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import data.HospitalDatabase
import ddwu.com.mobile.data.MyHos
import ddwu.com.mobile.petmap.databinding.ActivityBookmarkBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 즐겨찾기한 병원들 목록을 보여준다.
// 사용자 액션: MainActivity에서 '즐겨찾기' 버튼(btnBookmark) 클릭.
// 화면 전환: Intent를 통해 BookmarkActivity가 실행됨.
// DB 쿼리: BookmarkActivity가 실행되자마자 HospitalDatabase를 통해 getAllHospitals() 명령을 내림.
// 리스트 표시: DB에서 가져온 HospitalEntity 데이터들을 RecyclerView를 통해 화면에 리스트업함.

class BookmarkActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBookmarkBinding.inflate(layoutInflater) }
    private val adapter by lazy { BookmarkAdapter(this) }
    // -> Bookmark Adapter
    // BookmarkAdapter는 티켓이 없는 로컬 DB 데이터를 위해 placeId로 티켓을 먼저 구글에 요청하는 로직(fetchPlace)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // BookmarkAdapter와 연결
        binding.rvBookmarkList.adapter = adapter
        binding.rvBookmarkList.layoutManager = LinearLayoutManager(this)

        // 즐겨찾기에서 선택 삭제 후 확인창
        adapter.onDeleteClick = { hospital ->
            AlertDialog.Builder(this)
                .setTitle("삭제 확인")
                .setMessage("${hospital.name}을(를) 즐겨찾기에서 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    deleteBookmark(hospital)
                }
                .setNegativeButton("취소", null)
                .show()
        }

        loadBookmarks()

        binding.btnReset.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                // DB에서 모든 데이터 삭제
                HospitalDatabase.getDatabase(this@BookmarkActivity)
                    .hospitalDao()
                    .deleteAllHospitals()

                withContext(Dispatchers.Main) {
                    // loadBookmarks()를 다시 호출해도 되지만, 빈 리스트를 넣는 것이 더 빠름
                    adapter.updateItems(emptyList())
                }
            }
        }

        adapter.onDeleteClick = { hospital ->
            // 코루틴 내부에서 this 사용하면 코루틴 자신을 가리킴 -> 코루틴 사용 X
                // 알림 띄우고 확인하면 삭제
                android.app.AlertDialog.Builder(this)
                    .setTitle("삭제 확인")
                    .setMessage("${hospital.name}을(를) 즐겨찾기에서 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ ->
                        // 사용자가 '삭제'를 눌렀을 때만 실제 DB 삭제 실행
                        deleteBookmark(hospital)
                    }
                    .setNegativeButton("취소", null) // 취소 시 아무 동작 안 함
                    .show()

                // 삭제 후 리스트를 다시 불러와서 화면 갱신
                loadBookmarks()
        }
    }

    private fun loadBookmarks() {
        CoroutineScope(Dispatchers.IO).launch {
            // 내 스마트폰 안에 저장된 데이터베이스(Room DB)에 접속해서 저장된 모든 데이터를 꺼내오는 과정
            val dbList = HospitalDatabase.getDatabase(this@BookmarkActivity)
                        .hospitalDao()
                        .getAllHospitals()

            // DB 엔티티를 어댑터에서 사용하는 MyHos 모델로 변환
            val hospitalList = dbList.map { entity ->
                MyHos(
                    placeId = entity.placeId,
                    name = entity.name,
                    address = entity.address,
                    phone = entity.phone,
                    latitude = entity.latitude,
                    longitude = entity.longitude
                )
            }

            // adapter.notifyDataSetChanged(): 어떤 데이터가 추가됐는지, 삭제됐는지 따지지 않고 리스트 전체를 통째로 다시 그림
            // updateItems(): 이 새 리스트를 가져가서, 네가 가진 기존 리스트를 비우고 새로 채운 다음 화면을 갱신
           withContext(Dispatchers.Main) {
                adapter.updateItems(hospitalList)
           }
        }
    }

    private fun deleteBookmark(hospital: MyHos) {
        CoroutineScope(Dispatchers.IO).launch {
            // DB에서 해당 데이터 삭제
            // hospital.placeId를 조건으로 삭제를 수행
            HospitalDatabase.getDatabase(this@BookmarkActivity)
                .hospitalDao()
                .deleteHospitalByPlaceId(hospital.placeId)

            // 삭제 후 화면 갱신
            withContext(Dispatchers.Main) {
                loadBookmarks() // 리스트를 새로고침하여 삭제된 항목을 화면에서 제거
            }
        }
    }
}