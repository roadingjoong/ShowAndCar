package com.joljak.showandcar_app.Fragment

import android.app.AlertDialog
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.joljak.showandcar_app.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeleteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


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
        return inflater.inflate(R.layout.fragment_delete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modelEdit = view.findViewById<EditText>(R.id.modelEdit)
        imageButton2 = view.findViewById<ImageButton>(R.id.imageButton2)
        val deleteButton = view.findViewById<Button>(R.id.buttonDelete)


        imageButton2.setOnClickListener {
            requireActivity().onBackPressed()
        }



        deleteButton.setOnClickListener {
            val modelName = modelEdit.text.toString()
            val docId = modelName // 삭제하려는 문서의 ID를 설정

            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setMessage("삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("삭제") { _, _ ->
                    // 사용자가 "삭제"를 선택한 경우
                    val fireStoreDatabase = FirebaseFirestore.getInstance()
                    fireStoreDatabase.collection("carinfo")
                        .document(docId)
                        .delete()
                        .addOnSuccessListener {
                            // 삭제 성공
                            Log.d("Firestore", "성공적으로 삭제됐습니다.")
                            Toast.makeText(requireContext(), "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                            // CarManageFragment로 이동
                            val carManageFragment = CarManageFragment()
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.frame, carManageFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                        .addOnFailureListener { e ->
                            // 삭제 실패
                            Log.w("Firestore", "Error deleting document", e)
                            Toast.makeText(requireContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("취소") { dialog, _ ->
                    // 사용자가 "취소"를 선택한 경우
                    dialog.cancel()
                }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
}