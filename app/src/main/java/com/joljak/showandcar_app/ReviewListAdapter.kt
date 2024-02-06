package com.joljak.showandcar_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.imageview.ShapeableImageView

class ReviewListAdapter(private val reviewList: ArrayList<Review>) : RecyclerView.Adapter<ReviewListAdapter.MyViewHolder>() {

    private val originalContentList: ArrayList<Review> = ArrayList(reviewList)
    private var onItemClickListener: OnItemClickListener? = null

    // 검색 필터
    fun filter(query: String) {
        reviewList.clear()
        if (query.isEmpty()) {
            reviewList.addAll(originalContentList)
        } else {
            val filteredList = originalContentList.filter { car ->
                car.title.contains(query, ignoreCase = true)
            }
            reviewList.addAll(filteredList)
        }
        notifyDataSetChanged()
    }

    // 아이템 클릭 리스너 인터페이스
    interface OnItemClickListener {
        fun onItemClick(itemId: String) // 클릭한 아이템의 고유 ID를 전달
    }

    // 아이템 클릭 리스너 설정
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_review,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = reviewList[position]

        // Glide를 사용하여 이미지 로드 및 표시
        Glide.with(holder.itemView.context)
            .load(currentItem.image) // Contents 클래스에서 이미지 URL을 가져옴
            // .apply(RequestOptions.centerInside())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.reviewImage)

        holder.reviewTitle.text = currentItem.title
        holder.reviewAuthor.text = currentItem.author
        holder.reviewRatingBar.rating = currentItem.ratingBar
        holder.reviewDate.text = currentItem.postDate

        // 아이템 클릭 이벤트 처리
        holder.itemView.setOnClickListener {

            val reviewId = currentItem.reviewId // 클릭한 아이템의 고유 ID
            onItemClickListener?.onItemClick(reviewId) // 클릭 이벤트 콜백 호출
        }
    }

    override fun getItemCount(): Int {

        return reviewList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val reviewImage: ShapeableImageView = itemView.findViewById(R.id.imageView)
        val reviewTitle: TextView = itemView.findViewById(R.id.titleView)
        val reviewAuthor: TextView = itemView.findViewById(R.id.authorView)
        val reviewRatingBar: RatingBar = itemView.findViewById(R.id.ratingBarView)
        val reviewDate: TextView = itemView.findViewById(R.id.postDateView)
    }
}