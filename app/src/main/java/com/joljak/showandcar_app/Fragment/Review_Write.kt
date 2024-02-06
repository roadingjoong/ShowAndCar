package com.joljak.showandcar_app.Fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.joljak.showandcar_app.R
import java.text.SimpleDateFormat

import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Review_Write : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private var userId: String? = null

    private val PICK_IMAGE_REQUEST = 1
    private var imageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review__write, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser?.uid

        val okButton = view.findViewById<Button>(R.id.okButton)
        val uploadButton = view.findViewById<Button>(R.id.UploadButton)
        val titleEdit = view.findViewById<EditText>(R.id.titleEdit)
        val reviewEdit = view.findViewById<EditText>(R.id.reviewEdit)
        val reviewRating = view.findViewById<RatingBar>(R.id.reviewRating)

        // 게시글 작성
        okButton.setOnClickListener {

            val userCollection = FirebaseFirestore.getInstance().collection("user")

            userCollection.document(userId ?: "")
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // 현재 사용자의 정보가 존재하는 경우
                        val name = documentSnapshot.getString("name")
                        val title = titleEdit.text.toString()
                        val content = reviewEdit.text.toString()
                        val rating = reviewRating.rating

                        // FireStore 저장 Function 으로 넘김
                        if (name != null) {
                            saveFireStore(name, title, content, rating, imageUrl)
                        }
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

        // 이미지 업로드 버튼 클릭 시 이미지 선택
        uploadButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "이미지 선택"),
                PICK_IMAGE_REQUEST
            )
        }
    }

    // Firebase Storage에 선택한 이미지 업로드
    private fun uploadImageToFirebase(selectedImage: Uri) {

        if (selectedImage != null) {

            // 한국 시간대로 TimeZone 설정 ("Asia/Seoul")
            val koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul")
            val sdf = SimpleDateFormat("yyyy_MM_dd HH:mm", Locale.getDefault())
            sdf.timeZone = koreaTimeZone

            // 현재 시간 가져오기
            val currentTime = Date()
            // 현재 시간을 "0000_00_00 00:00" 형식으로 포맷
            val formattedTime = sdf.format(currentTime)

            val fileName = "$formattedTime.jpg"
            val refStorage = FirebaseStorage.getInstance().reference.child("image/$fileName")

            refStorage.putFile(selectedImage)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl
                        .addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
                        }
                        .addOnFailureListener { e ->
                            // URL 가져오기 실패
                            showToast("이미지 업로드 URL을 가져오는 중 오류가 발생했습니다.")
                        }
                }
                .addOnFailureListener { e ->
                    // 이미지 업로드 실패
                    showToast("이미지 업로드 중 오류가 발생했습니다.")
                }
        }
    }

    // onActivityResult 메서드를 사용하여 이미지를 ImageView에 설정
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uploadedImageView = view?.findViewById<ImageView>(R.id.uploadedImageView)

        if (uploadedImageView != null) {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val selectedImage = data.data
                    if (selectedImage != null) {
                        // Uri에서 이미지를 가져와서 ImageView에 설정
                        uploadedImageView.setImageURI(selectedImage)
                        // ImageView를 보이게 설정
                        uploadedImageView.visibility = View.VISIBLE
                        uploadImageToFirebase(selectedImage)
                    }
                }
            }
        } else {
            // uploadedImageView가 null인 경우에 대한 처리
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveFireStore(name: String, title: String, content: String, rating: Float, imageUrl: String?) {

        if (title.isEmpty() || content.isEmpty() || imageUrl == null || imageUrl.isEmpty()) {
            // 제목이 입력되지 않은 경우에 대한 처리
            if (title.isEmpty()) {
                showToast("제목을 입력해 주세요")
            }
            // 내용이 입력되지 않은 경우에 대한 처리
            if (content.isEmpty()) {
                showToast("내용을 입력해 주세요")
            }
            // 이미지 URL(`imageUrl`)이 비어있을 경우에 대한 처리
            if (imageUrl == null || imageUrl.isEmpty()) {
                showToast("이미지를 선택해 주세요")
            }
            return // 입력되지 않은 항목이 있을 경우 함수를 종료
        }
        // 한국 시간대로 TimeZone 설정 ("Asia/Seoul")
        val koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul")
        val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
        sdf.timeZone = koreaTimeZone

        // 현재 시간 가져오기
        val currentTime = Date()

        // 현재 시간을 "00/00 00:00" 형식으로 포맷
        val formattedTime = sdf.format(currentTime)

        val fireStoreDatabase = FirebaseFirestore.getInstance()
        val review: MutableMap<String, Any> = hashMapOf()
        review["title"] = title
        if (imageUrl != null) {
            review["image"] = imageUrl
        }
        review["content"] = content
        review["author"] = name
        review["Rating"] = rating
        review["postDate"] = formattedTime

        // FireStore에 있는 review 에 연결
        fireStoreDatabase.collection("review")
            // HasMap 에 저장된 데이터를 추가
            .add(review)
            // 성공
            .addOnSuccessListener { documentReference ->

                // documentReference.id에 새로 생성된 게시글의 고유 ID가 포함됨
                val reviewId = documentReference.id

                // 게시글이 추가되었으므로 Community_User 프래그먼트로 전환
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, Review_List()) // Community_User 프래그먼트로 전환
                transaction.addToBackStack(null) // 백 스택에 트랜잭션 추가
                transaction.commit()
            }
            // 실패
            .addOnFailureListener {
            }
    }

    // 메시지 출력
    private fun showToast(message: String) {

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}