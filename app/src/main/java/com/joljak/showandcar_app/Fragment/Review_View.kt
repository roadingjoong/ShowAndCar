package com.joljak.showandcar_app.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.joljak.showandcar_app.R

class Review_View : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_review__view, container, false)

        // 게시물 고유 ID 가져오기
        val reviewId = arguments?.getString("reviewId")

        if (reviewId != null) {
            // Firestore에서 데이터를 가져오는 코드
            val fireStoreDatabase = FirebaseFirestore.getInstance()
            val collectionRef = fireStoreDatabase.collection("review")

            // postId를 사용하여 해당 게시물의 Firestore 문서를 가져오도록 수정
            collectionRef.document(reviewId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val title = documentSnapshot.getString("title") ?: ""
                        val author = documentSnapshot.getString("author") ?: ""
                        val postDate = documentSnapshot.getString("postDate") ?: ""
                        val imageUrl = documentSnapshot.getString("image") ?: ""
                        val content = documentSnapshot.getString("content") ?: ""

                        // TextView에 데이터를 설정
                        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
                        val authorTextView = view.findViewById<TextView>(R.id.authorTextView)
                        val postDateTextView = view.findViewById<TextView>(R.id.postDateTextView)
                        val postImageView = view.findViewById<ImageView>(R.id.postImageView)
                        val contentTextView = view.findViewById<TextView>(R.id.contentTextView)

                        titleTextView.text = "$title"
                        authorTextView.text = " 작성자: $author | "
                        postDateTextView.text = "작성일: $postDate"
                        Glide.with(this)
                            .load(imageUrl) // 이미지의 URL을 지정합니다.
                            // .placeholder(R.drawable.placeholder_image) // 이미지 로딩 중에 표시할 디폴트 이미지를 설정합니다.
                            // .error(R.drawable.error_image) // 이미지 로딩 실패 시 표시할 이미지를 설정합니다.
                            .centerCrop() // 이미지 크롭을 설정합니다.
                            .into(postImageView) // ImageView에 이미지를 표시합니다.
                        contentTextView.text = "$content"
                    } else {
                        // 문서가 존재하지 않는 경우 처리
                        Log.e("Board_View", "Document does not exist.")
                    }
                }
                .addOnFailureListener { exception ->
                    // 데이터 가져오기에 실패한 경우 처리
                    Log.e("Board_View", "Error fetching Firestore data: $exception")
                }
        }
        return view
    }
}