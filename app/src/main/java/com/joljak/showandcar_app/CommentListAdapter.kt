package com.joljak.showandcar_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentListAdapter(private val commentList: ArrayList<Comment>) : RecyclerView.Adapter<CommentListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_comment,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun getComment(position: Int): Comment {
        return commentList[position]
    }

    fun removeComment(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            commentList.toMutableList().removeAt(position)
            notifyItemRemoved(position)
            selectedPosition = RecyclerView.NO_POSITION
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = commentList[position]

        holder.commentAuthor.text = currentItem.commentAuthor
        holder.commentContent.text = currentItem.commentContent
        holder.commentLikes.text = currentItem.commentLikes.toString()
        holder.commentAnswer.text = currentItem.commentAnswer.toString()
        holder.commentPostDate.text = currentItem.commentPostDate
    }

    override fun getItemCount(): Int {

        return commentList.size
    }

    fun updateData(commentList: ArrayList<Comment>) {

        // 새로운 댓글 목록으로 데이터를 교체
        this.commentList.clear()
        this.commentList.addAll(commentList)
    }

    fun setComments(commentList: ArrayList<Comment>) {

        this.commentList.clear()
        this.commentList.addAll(commentList)
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val commentAuthor: TextView = itemView.findViewById(R.id.commentAuthorView)
        val commentContent: TextView = itemView.findViewById(R.id.commentContentTextView)
        val commentLikes: TextView = itemView.findViewById(R.id.commentLikesTextView)
        val commentAnswer: TextView = itemView.findViewById(R.id.commentAnswerTextView)
        val commentPostDate: TextView = itemView.findViewById(R.id.commentPostDateView)
    }
}