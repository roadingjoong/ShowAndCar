package com.joljak.showandcar_app.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
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
 * Use the [UpdateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateImage : Fragment() {
    // TODO: Rename and change types of parameters
    private val PICK_IMAGE_REQUEST = 1
    private var imageUrl: String? = null

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var carNameEdit:EditText
    private lateinit var brandEdit:EditText
    private lateinit var carzongEdit:EditText
    private lateinit var oilEdit:EditText
    private lateinit var priceEdit:EditText
    private lateinit var contentEdit:EditText

    private lateinit var UploadButton: Button

    private lateinit var uploadedImageView: ImageView

    private lateinit var imageButton2: ImageButton
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
        return inflater.inflate(R.layout.fragment_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        uploadedImageView = view.findViewById(R.id.uploadedImageView)
        UploadButton = view.findViewById(R.id.UploadButton)

        imageButton2 = view.findViewById(R.id.imageButton2)

        imageButton2.setOnClickListener {
            requireActivity().onBackPressed()
        }

        UploadButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "이미지 선택"),
                PICK_IMAGE_REQUEST
            )
        }

        contentEdit = view.findViewById(R.id.contentEdit)
        brandEdit = view.findViewById(R.id.brandEdit)
        carzongEdit = view.findViewById(R.id.carzongEdit)
        oilEdit = view.findViewById(R.id.oilEdit)
        priceEdit = view.findViewById(R.id.priceEdit)
        carNameEdit = view.findViewById(R.id.carNameEdit)
        val fireStoreDatabase = FirebaseFirestore.getInstance()

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val inputEditText = EditText(requireContext())
        inputEditText.hint = "모델명 입력"
        inputEditText.inputType = InputType.TYPE_CLASS_TEXT

        alertDialogBuilder.setView(inputEditText)
            .setMessage("수정할 모델명 입력")
            .setCancelable(false)
            .setPositiveButton("확인") { _, _ ->
                if (inputEditText.text.toString().isNotEmpty()) {
                    fireStoreDatabase.collection("carinfo")
                        .document(inputEditText.text.toString())
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {

                                val imageUrl = document.getString("image")

                                if (imageUrl != null) {
                                    Glide.with(requireContext())
                                        .load(imageUrl)
                                        .into(uploadedImageView)

                                } else {
                                    // 이미지 URL이 없는 경우 기본 이미지 또는 에러 처리를 수행하세요.
                                }

                                val model = document.id
                                val content = document.getString("content")
                                val brand = document.getString("brand")
                                val heading = document.getString("heading")
                                val oil = document.getString("oil")
                                val price = document.getLong("price")

                                // 기존 데이터를 각 EditText에 표시

                                contentEdit.setText(content)
                                carNameEdit.setText(model)
                                brandEdit.setText(brand)
                                carzongEdit.setText(heading)
                                oilEdit.setText(oil)
                                priceEdit.setText(price?.toString() ?: "")
                            } else {
                                Log.d("Firestore", "No such document")
                                // 모델명에 해당하는 문서가 없는 경우 여기에 대한 처리를 추가할 수 있습니다.
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("Firestore", "get failed with ", exception)
                            // 데이터를 가져오는 중에 오류가 발생한 경우 여기에 대한 처리를 추가할 수 있습니다.
                        }
                } else {
                    // 모델명이 비어있는 경우에 대한 처리를 추가할 수 있습니다.
                    Toast.makeText(requireContext(), "모델명을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소") { dialog, _ ->
                // 사용자가 "취소"를 선택한 경우
                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

        updateButtonLogic()
    }

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

                            updateButtonLogic()

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

    private fun updateButtonLogic() {
        val updateButton = view?.findViewById<Button>(R.id.updateButton)
        updateButton?.setOnClickListener {
            val updatedModel = carNameEdit.text.toString()
            val updatedBrand = brandEdit.text.toString()
            val updatedheading = carzongEdit.text.toString()
            val updatedOil = oilEdit.text.toString()
            val updatedPrice = priceEdit.text.toString().toLong()

            val fireStoreDatabase = FirebaseFirestore.getInstance()

            // 이미지 URL이 업로드됐을 때만 업데이트 데이터에 추가
            fireStoreDatabase.collection("carinfo")
                .document(carNameEdit.text.toString())
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val imageUrl = document.getString("image")

                        val updatedData = hashMapOf(
                            "model" to updatedModel,
                            "brand" to updatedBrand,
                            "heading" to updatedheading,
                            "oil" to updatedOil,
                            "price" to updatedPrice,
                            "image" to imageUrl
                        )

                        val modelName = carNameEdit.text.toString()

                        // AlertDialog를 생성하고 설정
                        val alertDialogBuilder = AlertDialog.Builder(requireContext())
                        alertDialogBuilder.setMessage("수정하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("확인") { _, _ ->
                                // 파이어베이스에 데이터 업데이트
                                val fireStoreDatabase = FirebaseFirestore.getInstance()
                                fireStoreDatabase.collection("carinfo")
                                    .document(modelName)
                                    .update(updatedData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        // 수정 성공
                                        Log.d("Firestore", "성공적으로 수정됐습니다.")
                                        Toast.makeText(requireContext(), "수정이 완료되었습니다.", Toast.LENGTH_SHORT)
                                            .show()

                                        // CarManageFragment로 이동
                                        val carManageFragment = CarManageFragment()
                                        requireActivity().supportFragmentManager.beginTransaction()
                                            .replace(R.id.frame, carManageFragment)
                                            .addToBackStack(null)
                                            .commit()
                                    }
                                    .addOnFailureListener { e ->
                                        // 수정 실패
                                        Log.w("Firestore", "Error updating document", e)
                                        Toast.makeText(requireContext(), "수정에 실패했습니다.", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                            .setNegativeButton("취소") { dialog, _ ->
                                // 사용자가 "취소"를 선택한 경우
                                dialog.cancel()
                            }

                        // AlertDialog를 생성하고 표시
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                    }

                }

            // modelName은 callDataButton 클릭 시에 설정되었을 것입니다.

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

    private fun showToast(message: String) {

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

//    val updateButton = view?.findViewById<Button>(R.id.updateButton)
//    updateButton?.setOnClickListener {
//
//        val updatedModel = carNameEdit.text.toString()
//        val updatedBrand = brandEdit.text.toString()
//        val updatedCarzong = carzongEdit.text.toString()
//        val updatedOil = oilEdit.text.toString()
//        val updatedPrice = priceEdit.text.toString().toLong()
//        val updatedImageUrl = imageUrl
//
//
//        val updatedData = hashMapOf(
//            "model" to updatedModel,
//            "brand" to updatedBrand,
//            "heading" to updatedCarzong,
//            "oil" to updatedOil,
//            "price" to updatedPrice,
//            "image" to updatedImageUrl
//        )
//
//        // modelName은 callDataButton 클릭 시에 설정되었을 것입니다.
//        val modelName = carNameEdit.text.toString()
//
//        // AlertDialog를 생성하고 설정
//        val alertDialogBuilder = AlertDialog.Builder(requireContext())
//        alertDialogBuilder.setMessage("수정하시겠습니까?")
//            .setCancelable(false)
//            .setPositiveButton("확인") { _, _ ->
//                // 파이어베이스에 데이터 업데이트
//                val fireStoreDatabase = FirebaseFirestore.getInstance()
//                fireStoreDatabase.collection("carinfo")
//                    .document(modelName)
//                    .update(updatedData as Map<String, Any>)
//                    .addOnSuccessListener {
//                        // 수정 성공
//                        Log.d("Firestore", "성공적으로 수정됐습니다.")
//                        Toast.makeText(requireContext(), "수정이 완료되었습니다.", Toast.LENGTH_SHORT)
//                            .show()
//
//                        // CarManageFragment로 이동
//                        val carManageFragment = CarManageFragment()
//                        requireActivity().supportFragmentManager.beginTransaction()
//                            .replace(R.id.frame, carManageFragment)
//                            .addToBackStack(null)
//                            .commit()
//                    }
//                    .addOnFailureListener { e ->
//                        // 수정 실패
//                        Log.w("Firestore", "Error updating document", e)
//                        Toast.makeText(requireContext(), "수정에 실패했습니다.", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//            }
//            .setNegativeButton("취소") { dialog, _ ->
//                // 사용자가 "취소"를 선택한 경우
//                dialog.cancel()
//            }
//
//        // AlertDialog를 생성하고 표시
//        val alertDialog = alertDialogBuilder.create()
//        alertDialog.show()
//    }
}
