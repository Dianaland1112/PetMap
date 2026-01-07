package ddwu.com.mobile.petmap

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.data.Diary
import ddwu.com.mobile.petmap.databinding.DiaryItemBinding // diary_item.xml

// 일지들을 리사이클러뷰에 보여줌
class DiaryAdapter : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    private var items = mutableListOf<Diary>()

    // 삭제 클릭 처리를 위한 변수
    var onDeleteClick: ((Diary) -> Unit)? = null

    fun updateItems(newItems: List<Diary>) {
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val binding = DiaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
            val item = items[position]
            val binding = holder.binding

            binding.diaryTitle.text = item.title
            binding.diaryDate.text = item.date
            binding.DiaryMemo.text = item.memo

            // 사진 경로가 있으면 불러오고, 없으면 기본 이미지 표시
            if (!item.photoPath.isNullOrEmpty()) {
                binding.DiaryPhoto.setImageURI(Uri.parse(item.photoPath))
                binding.DiaryPhoto.scaleType = ImageView.ScaleType.CENTER_CROP // 미리보기 예쁘게
            } else {
                binding.DiaryPhoto.setImageResource(R.drawable.ic_launcher_background)
            }

            // 길게 눌러 삭제 (Activity에서 설정한 리스너 호출)
            holder.itemView.setOnLongClickListener {
                onDeleteClick?.invoke(item)
                true
            }
    }

    override fun getItemCount(): Int = items.size

    inner class DiaryViewHolder(val binding: DiaryItemBinding) : RecyclerView.ViewHolder(binding.root)
}