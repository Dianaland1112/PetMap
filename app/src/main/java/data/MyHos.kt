package ddwu.com.mobile.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "hospital_table")
data class MyHos(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "place_id")
    var placeId: String,        // 구글 Places API의 고유 ID

    @ColumnInfo(name = "name")
    var name: String,           // 병원 이름

    @ColumnInfo(name = "address")
    var address: String,        // 주소

    @ColumnInfo(name = "phone")
    var phone: String?,         // 전화번호 (없을 수 있으므로 Nullable)

    @ColumnInfo(name = "latitude")
    var latitude: Double,       // 위도 (좌표 저장을 위해 필요)

    @ColumnInfo(name = "longitude")
    var longitude: Double,       // 경도

    @Ignore
    var photoMetadata: com.google.android.libraries.places.api.model.PhotoMetadata? = null, // 사진 정보 추가
) {
    // [중요] Room이 DB 데이터를 담을 때 사용할 빈 생성자 역할을 하는 보조 생성자
    // 모든 인자에 기본값이 있다면 코틀린이 자동으로 생성하지만,
    // 명확하게 하기 위해 아래처럼 작성하거나 기본값을 모두 채워줘야 함. @Ignore 때문.
    constructor(id: Long, placeId: String, name: String, address: String, phone: String?, latitude: Double, longitude: Double)
            : this(id, placeId, name, address, phone, latitude, longitude, null)

// 리스트나 로그에서 확인하기 편하도록 toString 오버라이드
//    override fun toString(): String {
//        return "병원명: $name, 주소: $address"
//    }
}