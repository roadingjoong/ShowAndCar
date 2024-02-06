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
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.joljak.showandcar_app.CategoryAdapter
import com.joljak.showandcar_app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Board_Write : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private var userId: String? = null

    private var selectedCategory: String = ""
    private val PICK_IMAGE_REQUEST = 1
    private var imageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board__write, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser?.uid

        val postWriteButton = view.findViewById<Button>(R.id.postWriteButton)
        val imageUploadButton = view.findViewById<Button>(R.id.imageUploadButton)

        val titleEditText = view.findViewById<EditText>(R.id.titleEditText)
        val contentEditText = view.findViewById<EditText>(R.id.contentEditText)
        val categorySpinner = view.findViewById<Spinner>(R.id.categorySpinner)

        // Spinner 설정
        val categories = resources.getStringArray(R.array.category_array)
        val categoryAdapter = CategoryAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categorySpinner.adapter = categoryAdapter

        // categorySpinner에 OnItemSelectedListener 설정
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                // 선택된 카테고리 값을 selectedCategory에 저장
                selectedCategory = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 것도 선택되지 않았을 때 처리
            }
        }

        // 게시글 작성
        postWriteButton.setOnClickListener {

            val userCollection = FirebaseFirestore.getInstance().collection("user")

            userCollection.document(userId ?: "")
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {

                        // 현재 사용자의 정보가 존재하는 경우
                        val name = documentSnapshot.getString("name")
                        val category = selectedCategory
                        val title = titleEditText.text.toString()
                        val content = contentEditText.text.toString()

                        // FireStore 저장 Function 으로 넘김
                        if (name != null) {
                            saveFireStore(name, category, title, content, imageUrl)
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
        imageUploadButton.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "이미지 선택"), PICK_IMAGE_REQUEST)
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

    // FireStore에 저장
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveFireStore(name: String, category: String, title: String, content: String, imageUrl: String?) {

        if (category.isEmpty() || title.isEmpty() || content.isEmpty()) {
            // 카테고리가 선택되지 않은 경우에 대한 처리
            if (category.isEmpty()) {
                showToast("카테고리를 선택해 주세요")
            }
            // 제목이 입력되지 않은 경우에 대한 처리
            if (title.isEmpty()) {
                showToast("제목을 입력해 주세요")
            }
            // 내용이 입력되지 않은 경우에 대한 처리
            if (content.isEmpty()) {
                showToast("내용을 입력해 주세요")
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
        val post: MutableMap<String, Any> = hashMapOf()
        post["category"] = category
        post["title"] = title
        post["content"] = content
        if (imageUrl != null) {
            post["image"] = imageUrl
        }
        post["author"] = name
        post["likes"] = 0
        post["comments"] = 0
        post["postDate"] = formattedTime

        // FireStore에 있는 post 에 연결
        fireStoreDatabase.collection("post")
            // HasMap 에 저장된 데이터를 추가
            .add(post)
            // 성공
            .addOnSuccessListener { documentReference ->
                // documentReference.id에 새로 생성된 게시글의 고유 ID가 포함됨
                val postId = documentReference.id
                // 이전 페이지로 이동 (FragmentManager를 이용)
                requireActivity().supportFragmentManager.popBackStack()
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