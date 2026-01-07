package data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ddwu.com.mobile.data.Diary

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_table ORDER BY id DESC")
    suspend fun getAllDiaries(): List<Diary>

    @Insert
    suspend fun insertDiary(diary: Diary)

    @Delete
    suspend fun deleteDiary(diary: Diary)
}