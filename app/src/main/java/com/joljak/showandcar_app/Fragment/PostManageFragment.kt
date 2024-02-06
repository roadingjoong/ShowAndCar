package com.joljak.showandcar_app.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.joljak.showandcar_app.DeletePost
import com.joljak.showandcar_app.DeletePostListAdapter
import com.joljak.showandcar_app.R

class PostManageFragment : Fragment() {

    private lateinit var backButton: ImageButton
    private lateinit var backButtonText: TextView
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var deletePostButton: Button
    private lateinit var deletePostAdapter: DeletePostListAdapter

    private val fireStoreDatabase = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post_manage, container, false)

        // Initialize views
        backButton = view.findViewById(R.id.imageButton2)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        backButtonText = view.findViewById(R.id.textView2)
        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        deletePostButton = view.findViewById(R.id.deletePostButton)

        // Setup RecyclerView
        deletePostAdapter = DeletePostListAdapter(ArrayList())
        postRecyclerView.adapter = deletePostAdapter

        // Fetch data from Firebase
        fetchPostsFromFirebase()

        deletePostButton.setOnClickListener {
            // Get the list of selected posts from the adapter
            val selectedPosts = deletePostAdapter.getSelectedPosts()

            // Delete selected posts from Firebase
            deleteSelectedPosts(selectedPosts)

            // Fetch and update the RecyclerView with the latest data
            fetchPostsFromFirebase()
        }
        return view
    }

    private fun fetchPostsFromFirebase() {

        fireStoreDatabase.collection("post")
            .orderBy("postDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val postList = ArrayList<DeletePost>()
                for (document in result) {
                    // Firestore 문서에서 필요한 데이터 추출
                    val image = document.getString("image") ?: ""
                    val title = document.getString("title") ?: ""
                    val comments = document.getLong("comments")?.toString() ?: "0"
                    val likes = document.getLong("likes")?.toInt() ?: 0
                    val author = document.getString("author") ?: ""
                    val postDate = document.getString("postDate") ?: ""

                    val commentsText = "[$comments]"

                    val post = DeletePost(document.id, image, title, commentsText, likes, author, postDate)
                    postList.add(post)
                }
                deletePostAdapter = DeletePostListAdapter(ArrayList())
                postRecyclerView.adapter = deletePostAdapter
                // Update the RecyclerView with the fetched data
                deletePostAdapter.setPost(postList)
            }
            .addOnFailureListener { exception ->
                // Handle errors here
                Log.e("PostManageFragment", "Error getting posts", exception)
            }
    }

    private fun deleteSelectedPosts(selectedPosts: List<DeletePost>) {
        // Assuming you have a "posts" collection in your Firestore database
        val batch = fireStoreDatabase.batch()

        for (post in selectedPosts) {
            val postId = post.postId
            val postRef = fireStoreDatabase.collection("post").document(postId)
            batch.delete(postRef)
        }
        batch.commit()
            .addOnSuccessListener {
                // Deletion successful, you can handle any UI updates or messages here
            }
            .addOnFailureListener { exception ->
                // Handle errors here
                Log.e("PostManageFragment", "Error deleting posts", exception)
            }
    }
}