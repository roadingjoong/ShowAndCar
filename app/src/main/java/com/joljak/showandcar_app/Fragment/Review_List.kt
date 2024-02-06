package com.joljak.showandcar_app.Fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.joljak.showandcar_app.Post
import com.joljak.showandcar_app.R
import com.joljak.showandcar_app.Review
import com.joljak.showandcar_app.ReviewListAdapter

class Review_List : Fragment() {

    private lateinit var reviewAdapter: ReviewListAdapter
    private lateinit var reviewRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정은 먼저 수행하고, MyAdapter는 이후에 초기화합니다.
        val ReviewLayoutManager = LinearLayoutManager(context)

        reviewRecyclerView = view.findViewById(R.id.reviewListRecyclerView)
        reviewRecyclerView.layoutManager = ReviewLayoutManager
        reviewRecyclerView.setHasFixedSize(true)

        // Firestore 인스턴스 가져오기
        val fireStoreDatabase = FirebaseFirestore.getInstance()

        // Firestore에서 게시물 목록 가져오기
        fireStoreDatabase.collection("review")
            .orderBy("postDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    // 게시물 목록 가져오기 성공
                    val reviewList = ArrayList<Review>()

                    for (document in result) {
                        // Firestore 문서에서 필요한 데이터 추출
                        val image = document.getString("image") ?: ""
                        val title = document.getString("title") ?: ""
                        val author = document.getString("author") ?: ""
                        val ratingBar = document.getDouble("Rating")?.toFloat() ?: 0f
                        val postDate = document.getString("postDate") ?: ""

                        val review = Review(document.id, image, title, author, ratingBar, postDate)
                        reviewList.add(review)
                    }

                    // RecyclerView 어댑터 초기화 및 설정
                    reviewAdapter = ReviewListAdapter(reviewList)
                    reviewRecyclerView.adapter = reviewAdapter
                    reviewAdapter.notifyDataSetChanged()

                    // 아이템 클릭 이벤트 처리
                    reviewAdapter.setOnItemClickListener(object :
                        ReviewListAdapter.OnItemClickListener {
                        override fun onItemClick(reviewId: String) {

                            // Bundle을 사용하여 클릭한 게시물의 고유 ID를 Review_car 프래그먼트로 전달
                            val bundle = Bundle()
                            bundle.putString("reviewId", reviewId)

                            // Review_View 프래그먼트로 이동하면서 클릭한 게시물의 고유 ID를 전달
                            val reviewViewFragment = Review_View()
                            reviewViewFragment.arguments = bundle

                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.frame, reviewViewFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }
                    })
                } else {
                    // 게시물 목록이 비어있는 경우 처리
                }
            }
            .addOnFailureListener { exception ->
                // 게시물 목록 가져오기 실패 처리
                Log.d(TAG, "Error getting documents: ", exception)
            }

        // 검색 기능
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                reviewAdapter.filter(newText.orEmpty())

                return true
            }
        })

        // 게시글 작성 이동
        val reviewWriteButton = view.findViewById<Button>(R.id.reviewWriteButton)

        reviewWriteButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, Review_Write()) // Review_car 프래그먼트로 전환
            transaction.addToBackStack(null) // 백 스택에 트랜잭션 추가
            transaction.commit()
        }
    }
}