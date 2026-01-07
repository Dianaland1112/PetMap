package ddwu.com.mobile.petmap

import android.app.Application
import com.google.android.libraries.places.api.Places

// 즐겨찾기에서 사진 가져올 때 재인증 해야하므로.
// Manifest 파일에도 추가.
class MyPetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyD71GvWQhR9srb8YLM3_wqMvlpnf1FEFwo")
        }
    }
}