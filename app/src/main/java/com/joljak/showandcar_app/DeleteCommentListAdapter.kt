package com.joljak.showandcar_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeleteCommentListAdapter(private val commentList: ArrayList<DeleteComment>) : RecyclerView.Adapter<DeleteCommentListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_admin_comment,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    fun getSelectedComments(): List<DeleteComment> {

        return commentList.filter { it.isSelected }
    }

    fun setComment(commentList: ArrayList<DeleteComment>) {

        this.commentList.clear()
        this.commentList.addAll(commentList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = commentList[position]

        holder.commentAuthor.text = currentItem.commentAuthor
        holder.commentContent.text = currentItem.commentContent
        holder.commentLikes.text = currentItem.commentLikes.toString()
        holder.commentAnswer.text = currentItem.commentAnswer.toString()
        holder.commentPostDate.text = currentItem.commentPostDate

        holder.commentCheckBox.isChecked = currentItem.isSelected

        // CheckBox click event handling
        holder.commentCheckBox.setOnCheckedChangeListener { _, isChecked ->
            currentItem.isSelected = isChecked
        }
    }

    override fun getItemCount(): Int {

        return commentList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val commentCheckBox: CheckBox = itemView.findViewById(R.id.deleteCommentCheckBox)
        val commentAuthor: TextView = itemView.findViewById(R.id.commentAuthorView)
        val commentContent: TextView = itemView.findViewById(R.id.commentContentTextView)
        val commentLikes: TextView = itemView.findViewById(R.id.commentLikesTextView)
        val commentAnswer: TextView = itemView.findViewById(R.id.commentAnswerTextView)
        val commentPostDate: TextView = itemView.findViewById(R.id.commentPostDateView)

        init {
            // MyViewHolder가 초기화될 때 CheckBox에 대한 클릭 이벤트 리스너를 설정할 수 있습니다.
            commentCheckBox.setOnCheckedChangeListener { _, isChecked ->
                // 여기에 체크박스의 상태가 변경될 때 실행되어야 하는 코드를 추가할 수 있습니다.
            }
        }
    }
}