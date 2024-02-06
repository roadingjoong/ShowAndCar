package com.joljak.showandcar_app.Fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.joljak.showandcar_app.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Edit_My_PW.newInstance] factory method to
 * create an instance of this fragment.
 */
class Edit_My_PW : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_edit__my__p_w, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = mAuth.currentUser

        val pwTextView = view.findViewById<TextView>(R.id.nowpw)
        val changepw = view.findViewById<TextView>(R.id.changenowpw)
        val changepw2 = view.findViewById<TextView>(R.id.changenowpw2)

        val changebutton = view.findViewById<Button>(R.id.changenowpwButton)
        changebutton.setOnClickListener(View.OnClickListener {

            val nowText = pwTextView.text.toString()
            val nextText = changepw.text.toString()
            val nextText2 = changepw2.text.toString()

            if(nowText.isNotEmpty() && nextText.isNotEmpty() && nextText2.isNotEmpty()) {

                if (currentUser != null) {
                    // Firestore에서 사용자 데이터 가져오기
                    db.collection("user")
                        .document(currentUser.uid) // 사용자의 고유 ID를 사용하여 문서를 참조합니다.
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val pw = document.getString("password") ?: ""

                                if(nowText.equals(pw)){

                                    if(nextText.equals(nextText2)){
                                        currentUser.updatePassword(nextText)
                                            .addOnCompleteListener{ task ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(requireContext(), "비밀번호가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val errorMessage = task.exception?.message
                                                    Toast.makeText(requireContext(), "비밀번호 업데이트에 실패했습니다. 이유: $errorMessage", Toast.LENGTH_SHORT).show()
                                                }
                                            }

                                    }else{
                                        Log.d(ContentValues.TAG, "try again now")
                                    }

                                }else{
                                    Log.d(ContentValues.TAG, "try again")
                                }

                            } else {
                                Log.d(ContentValues.TAG, "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(ContentValues.TAG, "get failed with ", exception)
                        }

                }

            }

        })
        return view
    }
}