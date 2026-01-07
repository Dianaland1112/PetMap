package ddwu.com.mobile.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_table")
data class Diary(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var date: String,      // 날짜 (예: 2023-11-20)
    var title: String,     // 제목
    var memo: String,
    var photoPath: String? // 사진 경로
)