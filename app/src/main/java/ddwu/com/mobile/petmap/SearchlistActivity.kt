package ddwu.com.mobile.petmap

import android.content.Intent // Intent 임포트 추가
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.SearchByTextRequest
import ddwu.com.mobile.data.MyHos // 엔티티 패키지 경로 확인
import ddwu.com.mobile.petmap.databinding.ActivitySearchlistBinding

class SearchlistActivity : AppCompatActivity(), OnMapReadyCallback {
    private val binding by lazy { ActivitySearchlistBinding.inflate(layoutInflater) }
    private lateinit var googleMap: GoogleMap
    private lateinit var adapter: SearchAdapter
    private val TAG = "SearchlistActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 공장 가동: API 키 확인
        initPlaces()
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        // Adapter 초기화 및 클릭 리스너 설정
        adapter = SearchAdapter(this)
        binding.rvlists.adapter = adapter
        binding.rvlists.layoutManager = LinearLayoutManager(this)

        // 주의: ID가 아닌 객체(MyHos)를 통째로 전달받음
        adapter.setOnItemClickListener { hospital ->
            val intent = Intent(this, DetailActivity::class.java)
            // 객체의 각 필드를 상세 화면으로 전달
            intent.putExtra("placeId", hospital.placeId)
            intent.putExtra("name", hospital.name)
            intent.putExtra("address", hospital.address)
            intent.putExtra("phone", hospital.phone)
            intent.putExtra("lat", hospital.latitude)
            intent.putExtra("lng", hospital.longitude)
            startActivity(intent)
        }

        binding.btnListClose.setOnClickListener { finish() }

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    // 전화번호 안됨 -> Place.Field.Phone_NUMBER 추가
    private fun searchPlaces(query: String) {
        val placesClient = Places.createClient(this)
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG,
                            Place.Field.PHONE_NUMBER, Place.Field.PHOTO_METADATAS)
        val request = SearchByTextRequest.builder(query, placeFields)
                    .setMaxResultCount(10)
                    .build()

        placesClient.searchByText(request)
            .addOnSuccessListener { response ->
                Log.d(TAG, "검색 성공: ${response.places.size}개 발견")
                val hospitalList = mutableListOf<MyHos>()
                googleMap.clear()

                for (place in response.places) {
                    val latLng = place.latLng
                    if (latLng != null) {
                        // MyHos 엔티티 구조에 맞게 생성, 필드명 매칭하기!!
                        val hospital = MyHos(
                            placeId = place.id ?: "",
                            name = place.name ?: "이름 없음",
                            address = place.address ?: "주소 없음",
                            phone = place.phoneNumber,
                            latitude = latLng.latitude,
                            longitude = latLng.longitude,
                            photoMetadata = place.photoMetadatas?.firstOrNull()
                        )
                        hospitalList.add(hospital)
                        googleMap.addMarker(MarkerOptions().position(latLng).title(place.name))
                    }
                }
                adapter.updateItems(hospitalList) // 수정한 updateItems 호출

                if (hospitalList.isNotEmpty()) {
                    val firstLoc = com.google.android.gms.maps.model.LatLng(hospitalList[0].latitude, hospitalList[0].longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLoc, 14f))
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "구글 API 검색 실패: ${exception.message}")
                exception.printStackTrace()
                Toast.makeText(this, "검색 실패: 인터넷 연결을 확인하세요", Toast.LENGTH_SHORT).show()
            }
    }

    // Places API 초기화: AndroidManifest에 등록된 API Key를 자동으로 읽어옴
    private fun initPlaces() {
        try {
            val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = ai.metaData
            val apiKey = bundle.getString("com.google.android.geo.API_KEY")

            if (apiKey != null) {
                Places.initialize(applicationContext, apiKey)
            } else {
                Log.e(TAG, "API Key를 찾을 수 없습니다.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Places 초기화 실패: ${e.message}")
        }
    }

    // 지도가 준비되었을 때 호출: MainActivity에서 보낸 검색어를 기반으로 검색 시작
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // MainActivity에서 intent.putExtra("searchQuery", query)로 보낸 값을 가져옴
        val query = intent.getStringExtra("searchQuery") ?: "저녁 메뉴 추천"
        Log.d("API_TEST", "지도가 준비됨. 검색어: $query")
        searchPlaces(query)
        // 지도 초기 위치 설정
        val seoul = com.google.android.gms.maps.model.LatLng(37.5665, 126.9780)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 12f))

        searchPlaces(query) // 실제 구글 검색 실행
    }

    // MapView 생명주기 관리: 메모리 누수 방지 및 지도 정상 작동을 위해 반드시 필요
    // activity_searchlist.xml의 MapView와 연결된다.
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}