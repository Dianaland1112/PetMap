package data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ddwu.com.mobile.data.MyHos

@Dao
interface MyHosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHospital(hospital: MyHos)

    @Query("SELECT * FROM hospital_table")
    suspend fun getAllHospitals(): List<MyHos>

    // 특정 항목만 삭제하는 쿼리
    @Query("DELETE FROM hospital_table WHERE place_id = :placeId")
    suspend fun deleteHospitalByPlaceId(placeId: String)

    @Query("DELETE FROM hospital_table")
    suspend fun deleteAllHospitals()
}



