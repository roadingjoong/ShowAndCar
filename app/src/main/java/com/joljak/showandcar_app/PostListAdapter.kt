package com.joljak.showandcar_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.imageview.ShapeableImageView

class PostListAdapter(private val postList: ArrayList<Post>) : RecyclerView.Adapter<PostListAdapter.MyViewHolder>() {

    private val originalContentList: ArrayList<Post> = ArrayList(postList)
    private var onItemClickListener: OnItemClickListener? = null

    // 검색 필터
    fun filter(query: String) {

        postList.clear()

        if (query.isEmpty()) {
            postList.addAll(originalContentList)
        } else {
            val filteredList = originalContentList.filter { car ->
                car.title.contains(query, ignoreCase = true)
            }
            postList.addAll(filteredList)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_post,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    // 아이템 클릭 리스너 인터페이스
    interface OnItemClickListener {

        fun onItemClick(itemId: String) // 클릭한 아이템의 고유 ID를 전달
    }

    // 아이템 클릭 리스너 설정
    fun setOnItemClickListener(listener: OnItemClickListener) {

        this.onItemClickListener = listener
    }

    fun setPost(postList: ArrayList<Post>) {

        this.postList.clear()
        this.postList.addAll(postList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = postList[position]
        val reversedPosition = postList.size - position

        // Glide를 사용하여 이미지 로드 및 표시
        if (currentItem.image.isNotEmpty()) {
            // 이미지가 있을 경우
            holder.boardImage.visibility = View.VISIBLE

            Glide.with(holder.itemView.context)
                .load(currentItem.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.boardImage)
        } else {
            // 이미지가 없을 경우
            holder.boardImage.layoutParams.width = 0 // 두께를 0으로 설정
        }

        holder.boardNum.text = reversedPosition.toString()
        holder.boardTitle.text = currentItem.title
        holder.boardComment.text = currentItem.comments
        holder.boardLike.text = currentItem.likes.toString()
        holder.boardAuthor.text = currentItem.author
        holder.boardPostDate.text = currentItem.postDate

        // 아이템 클릭 이벤트 처리
        holder.itemView.setOnClickListener {

            val postId = currentItem.postId // 클릭한 아이템의 고유 ID
            onItemClickListener?.onItemClick(postId) // 클릭 이벤트 콜백 호출
        }
    }

    override fun getItemCount(): Int {

        return postList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val boardImage: ShapeableImageView = itemView.findViewById(R.id.imageView)
        val boardNum: TextView = itemView.findViewById(R.id.numView)
        val boardTitle: TextView = itemView.findViewById(R.id.titleView)
        val boardComment: TextView = itemView.findViewById(R.id.commentView)
        val boardLike: TextView = itemView.findViewById(R.id.likeView)
        val boardAuthor: TextView = itemView.findViewById(R.id.authorView)
        val boardPostDate: TextView = itemView.findViewById(R.id.postDateView)
    }
}