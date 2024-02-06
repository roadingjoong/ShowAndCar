package com.joljak.showandcar_app.Fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.joljak.showandcar_app.BestPostListAdapter
import com.joljak.showandcar_app.PostListAdapter
import com.joljak.showandcar_app.Post
import com.joljak.showandcar_app.R

class Community_User : Fragment() {

    // bestBoardRecyclerView
    private lateinit var bestBoardAdapter: BestPostListAdapter
    private lateinit var bestBoardRecyclerView: RecyclerView

    // BoardRecyclerView
    private lateinit var boardAdapter : PostListAdapter
    private lateinit var boardRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community__user, container, false)
    }

    override fun onViewCreated(view : View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firestore 인스턴스 가져오기
        val fireStoreDatabase = FirebaseFirestore.getInstance()

        // bestBoardRecyclerView 설정은 먼저 수행, bestBoardAdapter는 이후에 초기화.
        val bestBoardLayoutManager = LinearLayoutManager(context)

        bestBoardRecyclerView = view.findViewById(R.id.bestBoardRecyclerView)
        bestBoardRecyclerView.layoutManager = bestBoardLayoutManager
        bestBoardRecyclerView.setHasFixedSize(true)

        // Firestore에서 best 게시물 목록 가져오기
        fireStoreDatabase.collection("post")
            // 좋아요 내림차순 정렬
            .orderBy("likes", Query.Direction.DESCENDING)
            // 3개의 값만 출력하도록 제한
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                if (result != null && !result.isEmpty) {
                    // best 게시물 가져오기 성공
                    val postList = ArrayList<Post>()

                    for (document in result) {
                        // Firestore 문서에서 필요한 데이터 추출
                        val image = document.getString("image") ?: ""
                        val title = document.getString("title") ?: ""
                        val comments = document.getLong("comments")?.toString() ?: "0"
                        val likes = document.getLong("likes")?.toInt() ?: 0
                        val author = document.getString("author") ?: ""
                        val postDate = document.getString("postDate") ?: ""

                        val commentsText = "[$comments]"

                        val post = Post(document.id, image, title, commentsText, likes, author, postDate)
                        postList.add(post)
                    }

                    // RecyclerView 어댑터 초기화 및 설정
                    bestBoardAdapter = BestPostListAdapter(postList)
                    bestBoardRecyclerView.adapter = bestBoardAdapter
                    bestBoardAdapter.notifyDataSetChanged()

                    // 아이템 클릭 이벤트 처리
                    bestBoardAdapter.setOnItemClickListener(object : BestPostListAdapter.OnItemClickListener {
                        override fun onItemClick(postId: String) {

                            // Bundle을 사용하여 클릭한 게시물의 고유 ID를 Board_View 프래그먼트로 전달
                            val bundle = Bundle()
                            bundle.putString("postId", postId)

                            // Board_View 프래그먼트로 이동하면서 클릭한 게시물의 고유 ID를 전달
                            val boardViewFragment = Board_View()
                            boardViewFragment.arguments = bundle

                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.frame, boardViewFragment)
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

        // boardRecyclerView 설정은 먼저 수행, boardAdapter는 이후에 초기화.
        val boardLayoutManager = LinearLayoutManager(context)

        boardRecyclerView = view.findViewById(R.id.boardRecyclerView)
        boardRecyclerView.layoutManager = boardLayoutManager
        boardRecyclerView.setHasFixedSize(true)

        // Firestore에서 게시물 목록 가져오기
        fireStoreDatabase.collection("post")
            // 날짜 내림차순 정렬
            .orderBy("postDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result != null && !result.isEmpty) {
                    // 게시물 목록 가져오기 성공
                    val postList = ArrayList<Post>()

                    for (document in result) {
                        // Firestore 문서에서 필요한 데이터 추출
                        val image = document.getString("image") ?: ""
                        val title = document.getString("title") ?: ""
                        val comments = document.getLong("comments")?.toString() ?: "0"
                        val likes = document.getLong("likes")?.toInt() ?: 0
                        val author = document.getString("author") ?: ""
                        val postDate = document.getString("postDate") ?: ""

                        val commentsText = "[$comments]"

                        val post = Post(document.id, image, title, commentsText, likes, author, postDate)
                        postList.add(post)
                    }

                    // RecyclerView 어댑터 초기화 및 설정
                    boardAdapter = PostListAdapter(postList)
                    boardRecyclerView.adapter = boardAdapter
                    boardAdapter.notifyDataSetChanged()

                    // 아이템 클릭 이벤트 처리
                    boardAdapter.setOnItemClickListener(object : PostListAdapter.OnItemClickListener {
                        override fun onItemClick(postId: String) {

                            // Bundle을 사용하여 클릭한 게시물의 고유 ID를 Board_View 프래그먼트로 전달
                            val bundle = Bundle()
                            bundle.putString("postId", postId)

                            // Board_View 프래그먼트로 이동하면서 클릭한 게시물의 고유 ID를 전달
                            val boardViewFragment = Board_View()
                            boardViewFragment.arguments = bundle

                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.frame, boardViewFragment)
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

                boardAdapter.filter(newText.orEmpty())

                return true
            }
        })

        // 자유게시판 이동
        val freeBoardButton = view.findViewById<Button>(R.id.freeBoardButton)

        freeBoardButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, Free_Board()) // Free_Board 프래그먼트로 전환
            transaction.addToBackStack(null) // 백 스택에 트랜잭션 추가
            transaction.commit()
        }

        // 질문게시판 이동
        val questionBoardButton = view.findViewById<Button>(R.id.questionBoardButton)

        questionBoardButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, Question_Board()) // Question_Board 프래그먼트로 전환
            transaction.addToBackStack(null) // 백 스택에 트랜잭션 추가
            transaction.commit()
        }

        // 건의게시판 이동
        val suggestionBulletinBoardButton = view.findViewById<Button>(R.id.suggestionBulletinBoardButton)

        suggestionBulletinBoardButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, Suggestion_Bulletin_Board()) // Suggestion_Bulletin_Board 프래그먼트로 전환
            transaction.addToBackStack(null) // 백 스택에 트랜잭션 추가
            transaction.commit()
        }

        // 유머게시판 이동
        val humorBulletinBoardButton = view.findViewById<Button>(R.id.humorBulletinBoardButton)

        humorBulletinBoardButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, Humor_Bulletin_Board()) // Humor_Bulletin_Board 프래그먼트로 전환
            transaction.addToBackStack(null) // 백 스택에 트랜잭션 추가
            transaction.commit()
        }

        // 게시글 작성 이동
        val boardWriteButton = view.findViewById<Button>(R.id.boardWriteButton)

        boardWriteButton.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, Board_Write()) // Board_Write 프래그먼트로 전환
            transaction.addToBackStack(null) // 백 스택에 트랜잭션 추가
            transaction.commit()
        }
    }
}