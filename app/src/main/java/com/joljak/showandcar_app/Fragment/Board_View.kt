package com.joljak.showandcar_app.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.joljak.showandcar_app.Comment
import com.joljak.showandcar_app.CommentListAdapter
import com.joljak.showandcar_app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Board_View : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private var userId: String? = null

    private lateinit var commentAdapter: CommentListAdapter
    private lateinit var commentRecyclerView: RecyclerView

    private lateinit var heartImageView :ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_board__view, container, false)

        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser?.uid

        // 게시물 고유 ID 가져오기
        val postId = arguments?.getString("postId")

        heartImageView = view.findViewById<ImageView>(R.id.heartImageView)
        heartImageView.setOnClickListener {
            if (postId != null && userId != null) {
                updateLikes(postId, userId!!)
            }
        }

        if (postId != null) {

            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("LikedStatus", Context.MODE_PRIVATE)
            val isLiked = sharedPreferences.getBoolean(postId, false)
            if (isLiked) {
                heartImageView.setImageResource(R.drawable.ic_filled_heart)
            } else {
                heartImageView.setImageResource(R.drawable.ic_empty_heart)
            }

            // Firestore에서 데이터를 가져오는 코드
            val fireStoreDatabase = FirebaseFirestore.getInstance()
            val collectionRef = fireStoreDatabase.collection("post")
            // postId를 사용하여 해당 게시물의 Firestore 문서를 가져오도록 수정
            collectionRef.document(postId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val category = documentSnapshot.getString("category") ?: ""
                        val title = documentSnapshot.getString("title") ?: ""
                        val author = documentSnapshot.getString("author") ?: ""
                        val postDate = documentSnapshot.getString("postDate") ?: ""
                        val imageUrl = documentSnapshot.getString("image") ?: ""
                        val content = documentSnapshot.getString("content") ?: ""
                        val comments = documentSnapshot.getLong("comments")?.toInt() ?: 0
                        val likes = documentSnapshot.getLong("likes")?.toInt() ?: 0

                        // TextView에 데이터를 설정
                        val categoryTextView = view.findViewById<TextView>(R.id.categoryTextView)
                        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
                        val authorTextView = view.findViewById<TextView>(R.id.authorTextView)
                        val postDateTextView = view.findViewById<TextView>(R.id.postDateTextView)
                        val postImageView = view.findViewById<ImageView>(R.id.postImageView)
                        val contentTextView = view.findViewById<TextView>(R.id.contentTextView)
                        val commentCountTextView = view.findViewById<TextView>(R.id.commentCountTextView)
                        val likeCountTextView = view.findViewById<TextView>(R.id.likeCountTextView)

                        categoryTextView.text = "$category"
                        titleTextView.text = "$title"
                        authorTextView.text = " 작성자: $author | "
                        postDateTextView.text = "작성일: $postDate"
                        if (imageUrl.isNotEmpty()) {
                            // 이미지가 있을 경우
                            Glide.with(this)
                                .load(imageUrl)
                                .centerCrop()
                                .into(postImageView)
                            postImageView.visibility = View.VISIBLE
                        } else {
                            // 이미지가 없을 경우
                            postImageView.visibility = View.GONE
                        }
                        contentTextView.text = "$content"
                        commentCountTextView.text = "$comments"
                        likeCountTextView.text = "$likes"
                    } else {
                        // 문서가 존재하지 않는 경우 처리
                        Log.e("Board_View", "Document does not exist.")
                    }
                }
                .addOnFailureListener { exception ->
                    // 데이터 가져오기에 실패한 경우 처리
                    Log.e("Board_View", "Error fetching Firestore data: $exception")
                }
            val previousButton = view.findViewById<Button>(R.id.previousButton)
            previousButton.setOnClickListener {
                // 이전 페이지로 이동 (FragmentManager를 이용)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        // 댓글 목록을 보여주기 위한 RecyclerView 설정
        val commentLayoutManager = LinearLayoutManager(context)

        commentRecyclerView = view.findViewById(R.id.commentRecyclerView)
        commentRecyclerView.layoutManager = commentLayoutManager
        commentRecyclerView.setHasFixedSize(true)

        val fireStoreDatabase = FirebaseFirestore.getInstance()

        if (postId != null) {
            // Firestore에서 해당 게시물의 댓글을 가져오는 코드
            fireStoreDatabase.collection("comment")
                .whereEqualTo("postId", postId)
                .orderBy("commentPostDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    if (result != null && !result.isEmpty) {
                        val commentList = ArrayList<Comment>()

                        for (document in result) {
                            val commentAuthor = document.getString("commentAuthor") ?: ""
                            val commentContent = document.getString("commentContent") ?: ""
                            val commentLikes = document.getLong("commentLikes")?.toInt() ?: 0
                            val commentAnswer = document.getLong("commentAnswer")?.toInt() ?: 0
                            val commentPostDate = document.getString("commentPostDate") ?: ""
                            val userId = document.getString("userId") ?: ""

                            val comment = Comment(
                                commentAuthor,
                                commentContent,
                                commentLikes,
                                commentAnswer,
                                commentPostDate,
                                userId
                            )
                            commentList.add(comment)
                        }
                        // RecyclerView에 댓글 목록을 표시하기 위한 CommentListAdapter 초기화 및 설정
                        commentAdapter = CommentListAdapter(commentList)
                        commentRecyclerView.adapter = commentAdapter
                        commentAdapter.notifyDataSetChanged()
                    } else {
                        // 댓글이 없는 경우 처리
                    }
                }
                .addOnFailureListener { exception ->
                    // 댓글 목록 가져오기 실패 처리
                    Log.e("Board_View", "Error fetching comments: $exception")
                }
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val commentWriteButton = view.findViewById<Button>(R.id.commentWriteButton)
        val commentEditText = view.findViewById<EditText>(R.id.commentEditText)

        // 게시물 고유 ID 가져오기
        val postId = arguments?.getString("postId")

        // 댓글 작성
        commentWriteButton.setOnClickListener {

            val userCollection = FirebaseFirestore.getInstance().collection("user")

            userCollection.document(userId ?: "")
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // 현재 사용자의 정보가 존재하는 경우
                        val name = documentSnapshot.getString("name")
                        val commentContent = commentEditText.text.toString()

                        // FireStore 저장 Function 으로 넘김
                        if (name != null) {
                            saveFireStore(postId, userId, name, commentContent)
                        }
                        // 댓글 저장 후 EditText 초기화
                        commentEditText.text.clear()
                        // name을 저장하거나 필요한 처리를 수행
                    } else {
                        // 사용자 정보가 존재하지 않는 경우에 대한 처리
                    }
                }
                .addOnFailureListener { e ->
                    // 사용자 정보를 가져오는 데 실패한 경우에 대한 처리
                    showToast("사용자 정보를 가져오는 중 오류가 발생했습니다.")
                }
        }
    }

    // FireStore에 댓글 저장
    private fun saveFireStore(postId: String?, userId: String?, name: String, commentContent: String) {

        if (postId != null && userId != null && !commentContent.isEmpty()) {
            // 한국 시간대로 TimeZone 설정 ("Asia/Seoul")
            val koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul")
            val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
            sdf.timeZone = koreaTimeZone

            // 현재 시간 가져오기
            val currentTime = Date()

            // 현재 시간을 "MM/dd HH:mm" 형식으로 포맷
            val formattedTime = sdf.format(currentTime)

            val fireStoreDatabase = FirebaseFirestore.getInstance()
            val comment: MutableMap<String, Any> = hashMapOf()
            comment["commentAuthor"] = name
            comment["commentContent"] = commentContent
            comment["commentLikes"] = 0
            comment["commentAnswer"] = 0
            comment["commentPostDate"] = formattedTime
            comment["postId"] = postId
            comment["userId"] = userId

            // Firestore의 'comment' 컬렉션에 저장
            val commentRef = fireStoreDatabase.collection("comment").document()
            fireStoreDatabase.runTransaction { transaction ->
                val postRef = fireStoreDatabase.collection("post").document(postId)
                val snapshot = transaction.get(postRef)
                if (snapshot.exists()) {
                    val currentComments = snapshot.getLong("comments") ?: 0
                    transaction.update(postRef, "comments", currentComments + 1)
                    transaction.set(commentRef, comment)
                }
                null
            }.addOnSuccessListener { _ ->
                // 댓글이 성공적으로 저장됨
                val commentId = commentRef.id
                // 이후의 처리나 메시지 출력 등을 수행
                // 댓글 작성 후, 댓글 목록 다시 가져오기
                refreshCommentList(postId)
            }.addOnFailureListener { exception ->
                // 저장에 실패한 경우
                Log.e("Board_View", "Error saving comment: $exception")
            }
        } else {
            // postId가 null이거나 commentContent가 비어있는 경우에 대한 처리
            showToast("댓글을 입력해 주세요")
        }
    }

    // 댓글 목록을 다시 가져오고 RecyclerView를 업데이트
    private fun refreshCommentList(postId: String?) {

        if (postId != null) {
            val fireStoreDatabase = FirebaseFirestore.getInstance()
            fireStoreDatabase.collection("comment")
                .whereEqualTo("postId", postId)
                // 날짜 오름차순 정렬
                .orderBy("commentPostDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    if (result != null && !result.isEmpty) {
                        val commentList = ArrayList<Comment>()

                        for (document in result) {
                            val commentAuthor = document.getString("commentAuthor") ?: ""
                            val commentContent = document.getString("commentContent") ?: ""
                            val commentLikes = document.getLong("commentLikes")?.toInt() ?: 0
                            val commentAnswer = document.getLong("commentAnswer")?.toInt() ?: 0
                            val commentPostDate = document.getString("commentPostDate") ?: ""
                            val userId = document.getString("userId") ?: ""

                            val comment = Comment(
                                commentAuthor,
                                commentContent,
                                commentLikes,
                                commentAnswer,
                                commentPostDate,
                                userId
                            )
                            commentList.add(comment)
                        }

                        // RecyclerView를 업데이트하기 위해 Adapter를 업데이트하고 다시 그리기
                        commentAdapter = CommentListAdapter(commentList)
                        commentRecyclerView.adapter = commentAdapter
                        commentAdapter.notifyDataSetChanged()
                    } else {
                        // 댓글이 없는 경우 처리
                    }
                }
                .addOnFailureListener { exception ->
                    // 댓글 목록 가져오기 실패 처리
                }
        }
    }

    private fun updateLikes(postId: String, userId: String) {

        val fireStoreDatabase = FirebaseFirestore.getInstance()
        val postRef = fireStoreDatabase.collection("post").document(postId)

        var likedBy: MutableMap<String, Boolean> = HashMap()

        fireStoreDatabase.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            if (snapshot.exists()) {
                val currentLikes = snapshot.getLong("likes") ?: 0
                likedBy = (snapshot.get("likedBy") as? Map<String, Boolean> ?: HashMap()).toMutableMap()

                // 사용자가 이미 게시물에 좋아요를 눌렀는지 확인
                if (likedBy.containsKey(userId)) {
                    // 사용자가 이미 좋아요를 눌렀으므로 좋아요를 취소
                    likedBy.remove(userId)
                    transaction.update(postRef, "likes", currentLikes - 1)
                    saveLikedStatus(postId, userId, false) // 좋아요 상태를 SharedPreferences에 저장
                } else {
                    // 사용자가 좋아요를 누르지 않았으므로 좋아요
                    likedBy[userId] = true
                    transaction.update(postRef, "likes", currentLikes + 1)
                    saveLikedStatus(postId, userId, true) // 좋아요 상태를 SharedPreferences에 저장
                }
                transaction.update(postRef, "likedBy", likedBy)
            }
            null
        }.addOnSuccessListener {
            // 좋아요가 성공적으로 업데이트됨
            // 여기서 필요한 추가 작업이나 메시지 출력을 수행
            refreshLikesCount(postId)
            // 하트 모양 업데이트
            updateHeartView(userId, heartImageView, likedBy)
        }.addOnFailureListener { exception ->
            // 업데이트에 실패한 경우
            Log.e("Board_View", "좋아요 업데이트 오류: $exception")
        }
    }

    private fun saveLikedStatus(postId: String, userId: String, liked: Boolean) {

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("LikedStatus", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // 사용자 ID를 키에 포함시킴
        editor.putBoolean("$postId-$userId", liked)

        editor.apply()
    }

    private fun refreshLikesCount(postId: String) {

        val fireStoreDatabase = FirebaseFirestore.getInstance()

        fireStoreDatabase.collection("post")
            .document(postId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val likes = documentSnapshot.getLong("likes") ?: 0
                    val likeCountTextView = view?.findViewById<TextView>(R.id.likeCountTextView)
                    likeCountTextView?.text = likes.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Board_View", "좋아요 수 갱신 오류: $exception")
            }
    }

    private fun updateHeartView(userId: String, heartImageView: ImageView, likedBy: Map<String, Boolean>) {

        if (likedBy.containsKey(userId)) {
            heartImageView.setImageResource(R.drawable.ic_filled_heart)
        } else {
            heartImageView.setImageResource(R.drawable.ic_empty_heart)
        }
    }

    // 메시지 출력
    private fun showToast(message: String) {

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}