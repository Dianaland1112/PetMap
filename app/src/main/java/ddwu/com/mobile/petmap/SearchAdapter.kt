package ddwu.com.mobile.petmap

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import ddwu.com.mobile.data.MyHos // 패키지 경로 확인 필요
import ddwu.com.mobile.petmap.databinding.ItemsBinding

class SearchAdapter(private val context: Context) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    private var items = mutableListOf<MyHos>()

    // 클릭 리스너의 인자를 객체 전체로 변경
    private var itemClickListener: ((MyHos) -> Unit)? = null

    fun setOnItemClickListener(listener: (MyHos) -> Unit) {
        itemClickListener = listener
    }

    fun updateItems(newItems: List<MyHos>?) {
        items.clear()
        if (newItems != null) {
            items.addAll(newItems)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding

        binding.tvName.text = item.name
        binding.tvLoc.text = item.address
        binding.tvPhone.text = item.phone ?: "전화번호 정보 없음"

        val placesClient = Places.createClient(context)
        val photoMetadata = item.photoMetadata

        if (photoMetadata != null) {
            // 사진 요청 생성
            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(400)
                .setMaxHeight(300)
                .build()

            // 실제 이미지 다운로드 요청
            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener { fetchPhotoResponse ->
                    val bitmap = fetchPhotoResponse.bitmap
                    binding.tvImage.setImageBitmap(bitmap) // 다운로드 성공 시 이미지 세팅
                }
                .addOnFailureListener {
                    binding.tvImage.setImageResource(R.drawable.ic_launcher_background) // 실패 시 기본 이미지
                }
        } else {
            binding.tvImage.setImageResource(R.drawable.ic_launcher_background) // 사진 정보가 없을 때
        }

        holder.itemView.setOnClickListener { itemClickListener?.invoke(item) }
    }

    // 데이터만 바꿔서 보여주기 위해
    inner class SearchViewHolder(val binding: ItemsBinding) : RecyclerView.ViewHolder(binding.root)
    override fun getItemCount(): Int = items.size

    /*
        어댑터는 다음 세 함수를 무조건 포함해야 함.
        onCreateViewHolder: 한 칸의 레이아웃(그릇)을 만든다.
        onBindViewHolder: 그릇에 데이터를 채워 넣는다.
        getItemCount: 전체 데이터가 몇 개인지 말해준다.
    */
}