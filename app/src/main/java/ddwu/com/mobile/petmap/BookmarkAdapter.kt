package ddwu.com.mobile.petmap

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import ddwu.com.mobile.data.MyHos
import ddwu.com.mobile.petmap.databinding.ItemsBinding

// 로컬저장소에서 즐겨찾기 이미지 찾아와야 하므로 북마크 어댑터 생성해야 함
// 핸드폰 내 RoomDB에서 가져온다. MyHos에서 @Ignore 처리 -> placeId만 저장되어 있음
// 따라서 구글에 이 placeId를 이용하여 사진 티켓 받고 룸디비에 요청

// context: 현재 앱의 정보, Places.createClient(context) 호출 위해
class BookmarkAdapter(val context: Context): RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    var items = mutableListOf<MyHos>()
    var onDeleteClick: ((MyHos) -> Unit)? = null // 삭제 처리 클릭

    fun updateItems(newItems: List<MyHos>) {
        this.items.clear()          // 기존 리스트 비우기
        this.items.addAll(newItems) // 새 리스트 추가
        notifyDataSetChanged()      // 이제 화면 그려라
    }

    inner class BookmarkViewHolder(val binding: ItemsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding

        binding.tvName.text = item.name
        binding.tvLoc.text = item.address
        binding.tvPhone.text = item.phone ?: "전화번호 없음"

        // 핵심: 구글 Places API를 사용해 사진을 새로 가져옴 -> 재인증 해야
        val placesClient = Places.createClient(context)

        // 1. 해당 placeId로부터 사진 메타데이터(티켓)를 먼저 요청
        val placeFields = listOf(Place.Field.PHOTO_METADATAS)
        // 재요청
        val request = FetchPlaceRequest.builder(item.placeId, placeFields).build()

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val photoMetadata = response.place.photoMetadatas?.firstOrNull()

            if (photoMetadata != null) {
                // 2. 티켓이 있다면 실제 사진 파일을 요청
                val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(400)
                    .setMaxHeight(300)
                    .build()

                placesClient.fetchPhoto(photoRequest).addOnSuccessListener { photoResponse ->
                    binding.tvImage.setImageBitmap(photoResponse.bitmap)
                }
            } else {
                binding.tvImage.setImageResource(R.drawable.ic_launcher_background)
            }
        }.addOnFailureListener {
            binding.tvImage.setImageResource(R.drawable.ic_launcher_background)
        }

        // 아이템 긴 클릭 시 삭제하거나, 별도의 삭제 버튼이 있다면 사용
        holder.itemView.setOnLongClickListener {
            onDeleteClick?.invoke(item)
            true
        }
    }

    override fun getItemCount(): Int = items.size
}