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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.joljak.showandcar_app.Comment
import com.joljak.showandcar_app.CommentListAdapter
import com.joljak.showandcar_app.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Cars_View.newInstance] factory method to
 * create an instance of this fragment.
 */
class Cars_View : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cars__view, container, false)

        val heading = arguments?.getString("heading")

        if (heading != null) {
            // Firestore에서 데이터를 가져오는 코드
            val fireStoreDatabase = FirebaseFirestore.getInstance()
            val collectionRef = fireStoreDatabase.collection("carinfo")

            // heading 사용하여 해당 게시물의 Firestore 문서를 가져오도록 수정
            collectionRef.document(heading).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val model = documentSnapshot.id // 문서 이름 가져오기
                        val image = documentSnapshot.getString("image") ?: ""
                        val content = documentSnapshot.getString("content") ?: ""

                        // TextView에 데이터를 설정
                        val modelName = view.findViewById<TextView>(R.id.carModelView)
                        val contentTextView = view.findViewById<TextView>(R.id.contentTextView)
                        val postImageView = view.findViewById<ImageView>(R.id.postImageView)

                        modelName.text = "$model"
                        contentTextView.text = "$content"

                        Glide.with(this)
                            .load(image) // 이미지의 URL을 지정합니다.
                            // .placeholder(R.drawable.placeholder_image) // 이미지 로딩 중에 표시할 디폴트 이미지를 설정합니다.
                            // .error(R.drawable.error_image) // 이미지 로딩 실패 시 표시할 이미지를 설정합니다.
                            .centerCrop() // 이미지 크롭을 설정합니다.
                            .into(postImageView) // ImageView에 이미지를 표시합니다.

                    } else {
                        // 문서가 존재하지 않는 경우 처리
                        Log.e("Cars_View", "Document does not exist.")
                    }
                }
                .addOnFailureListener { exception ->
                    // 데이터 가져오기에 실패한 경우 처리
                    Log.e("Cars_View", "Error fetching Firestore data: $exception")
                }
            val previousButton = view.findViewById<Button>(R.id.preButton)
            previousButton.setOnClickListener {
                // 이전 페이지로 이동 (FragmentManager를 이용)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        // 해당 View 반환
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Cars_View.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Cars_View().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}