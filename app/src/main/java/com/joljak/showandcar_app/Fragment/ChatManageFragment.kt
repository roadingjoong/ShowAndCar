package com.joljak.showandcar_app.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.joljak.showandcar_app.DeleteComment
import com.joljak.showandcar_app.DeleteCommentListAdapter
import com.joljak.showandcar_app.R

class ChatManageFragment : Fragment() {

    private lateinit var backButton: ImageButton
    private lateinit var backButtonText: TextView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var deleteChatButton: Button
    private lateinit var deleteCommentAdapter: DeleteCommentListAdapter

    private val fireStoreDatabase = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_manage, container, false)

        // Initialize views
        backButton = view.findViewById(R.id.imageButton2)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        backButtonText = view.findViewById(R.id.textView2)
        commentRecyclerView = view.findViewById(R.id.commentRecyclerView)
        deleteChatButton = view.findViewById(R.id.deleteChat)

        deleteCommentAdapter = DeleteCommentListAdapter(ArrayList())
        commentRecyclerView.adapter = deleteCommentAdapter

        fetchCommentsFromFirebase()

        // 댓글 삭제 버튼에 클릭 리스너 추가
        deleteChatButton.setOnClickListener {

            val selectedPosts = deleteCommentAdapter.getSelectedComments()

            // Delete selected posts from Firebase
            deleteSelectedComments(selectedPosts)

            // Fetch and update the RecyclerView with the latest data
            fetchCommentsFromFirebase()
        }
        return view
    }

    private fun fetchCommentsFromFirebase() {

        fireStoreDatabase.collection("comment")
            .orderBy("commentPostDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val commentList = ArrayList<DeleteComment>()
                for (document in result) {
                    // Firestore 문서에서 필요한 데이터 추출
                    val commentId = document.id
                    val commentAuthor = document.getString("commentAuthor") ?: ""
                    val commentContent = document.getString("commentContent") ?: ""
                    val commentLikes = document.getLong("commentLikes")?.toInt() ?: 0
                    val commentAnswer = document.getLong("commentAnswer")?.toInt() ?: 0
                    val commentPostDate = document.getString("commentPostDate") ?: ""

                    val comment = DeleteComment(commentId, commentAuthor, commentContent, commentLikes, commentAnswer, commentPostDate,)
                    commentList.add(comment)
                }
                deleteCommentAdapter = DeleteCommentListAdapter(ArrayList())
                commentRecyclerView.adapter = deleteCommentAdapter
                // Update the RecyclerView with the fetched data
                deleteCommentAdapter.setComment(commentList)
            }
            .addOnFailureListener { exception ->
                // Handle errors here
                Log.e("CommentManageFragment", "Error getting comments", exception)
            }
    }

    private fun deleteSelectedComments(selectedComments: List<DeleteComment>) {

        val batch = fireStoreDatabase.batch()

        for (comment in selectedComments) {
            val commentId = comment.commentId
            val commentRef = fireStoreDatabase.collection("comment").document(commentId)
            batch.delete(commentRef)
        }
        batch.commit()
            .addOnSuccessListener {
                // Deletion successful, you can handle any UI updates or messages here
            }
            .addOnFailureListener { exception ->
                // Handle errors here
                Log.e("CommentManageFragment", "Error deleting comments", exception)
            }
    }
}