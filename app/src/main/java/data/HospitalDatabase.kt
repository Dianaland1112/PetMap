package data

import ddwu.com.mobile.data.MyHos
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ddwu.com.mobile.data.Diary

@Database(entities = [MyHos::class, Diary::class], version = 2)
abstract class HospitalDatabase : RoomDatabase() {
    abstract fun hospitalDao(): MyHosDao
    abstract fun diaryDao(): DiaryDao

    companion object {
        @Volatile
        private var INSTANCE: HospitalDatabase? = null

        fun getDatabase(context: Context): HospitalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HospitalDatabase::class.java,
                    "hospital_db"
                ).fallbackToDestructiveMigration() // 버전이 1에서 2로 올라갔으므로 이 줄을 추가하면 앱이 안 꺼진다
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

