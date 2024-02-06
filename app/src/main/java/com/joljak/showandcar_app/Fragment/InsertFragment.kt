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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.joljak.showandcar_app.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InsertFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var imageButton2: ImageButton

    private val PICK_IMAGE_REQUEST = 1
    private var imageUrl: String? = null

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insert, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val okButton = view.findViewById<Button>(R.id.okButton)
        val uploadButton = view.findViewById<Button>(R.id.UploadButton)
        val contentEdit = view.findViewById<EditText>(R.id.contentEdit)
        val brandEdit = view.findViewById<EditText>(R.id.brandEdit)
        val carzongEdit = view.findViewById<EditText>(R.id.carzongEdit)
        val carNameEdit = view.findViewById<EditText>(R.id.carNameEdit)
        val oilEdit = view.findViewById<EditText>(R.id.oilEdit)
        val priceEdit = view.findViewById<EditText>(R.id.priceEdit)

        okButton.setOnClickListener {
            // 게시글 작성화면에서 불러옴
            val carName = carNameEdit.text.toString()
            val brand = brandEdit.text.toString()
            val content = contentEdit.text.toString()
            val carzong = carzongEdit.text.toString()
            val oil = oilEdit.text.toString()
            val price = priceEdit.text.toString().toLong()

            // FireStore 저장 Function 으로 넘김
            saveFireStore(carName, content, brand, carzong, oil, price, imageUrl)
        }


        imageButton2 = view.findViewById<ImageButton>(R.id.imageButton2)

        imageButton2.setOnClickListener {
            requireActivity().onBackPressed()
        }

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

private fun uploadImageToFirebase(selectedImage: Uri) {
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
        .continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            // 이미지 업로드 성공 후 URL을 가져오는 작업을 계속 진행
            return@continueWithTask refStorage.downloadUrl
        }
        .addOnSuccessListener { uri ->
            // URL 가져오기 성공
            imageUrl = uri.toString()
        }
        .addOnFailureListener { e ->
            // URL 가져오기 실패
            showToast("이미지 업로드 URL을 가져오는 중 오류가 발생했습니다.")
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
    private fun saveFireStore(
        carName: String, content: String, brand: String,
        carzong: String, oil: String, price: Long, imageUrl: String?) {

        if (carName.isEmpty() || content.isEmpty() || imageUrl == null ||
            brand.isEmpty() || carzong.isEmpty() || oil.isEmpty() || price.toString().isEmpty() ||  imageUrl.isEmpty()) {
            if (carName.isEmpty()) {
                showToast("차이름을 입력해 주세요")
            }

            if (content.isEmpty()) {
                showToast("내용을 입력해 주세요")
            }

            if (carzong.isEmpty()) {
                showToast("차종을 입력해 주세요")
            }

            if (brand.isEmpty()) {
                showToast("브랜드를 입력해 주세요")
            }

            if (price.toString().isEmpty()) {
                showToast("가격을 입력해 주세요")
            }

            if (oil.isEmpty()) {
                showToast("연료를 입력해 주세요")
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

        val carinfoRef = fireStoreDatabase.collection("carinfo").document(carName)

        val carinfo: MutableMap<String, Any> = hashMapOf("price" to price.toLong())


        carinfo["heading"] = carName

        if (imageUrl != null) {
            carinfo["image"] = imageUrl
        }

        carinfo["content"] = content
        carinfo["heading"] = carzong
        carinfo["brand"] = brand
        carinfo["price"] = price
        carinfo["oil"] = oil

        carinfoRef
            .set(carinfo)
            .addOnSuccessListener {
                // 성공
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, CarManageFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            .addOnFailureListener {
                // 실패
            }
    }

    // 메시지 출력
    private fun showToast(message: String) {

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }



}