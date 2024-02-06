package com.joljak.showandcar_app.Fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.joljak.showandcar_app.R

class Edit_My_Name : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit__my__name, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = mAuth.currentUser

        val changename = view.findViewById<EditText>(R.id.changemyname)

        val changeNbt = view.findViewById<Button>(R.id.changenamebt)

        changeNbt.setOnClickListener(View.OnClickListener {

            val newchangename = changename.text.toString() // 새로운 닉네임

            currentUser?.let { user ->
                // 사용자가 로그인되어 있는 경우에만 프로필 업데이트를 시도
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newchangename)
                    .build()

                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 프로필 업데이트 성공
                            Toast.makeText(requireContext(), "프로필이 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            // 프로필 업데이트 실패
                            val errorMessage = task.exception?.message
                            Toast.makeText(
                                requireContext(),
                                "프로필 업데이트에 실패했습니다. 이유: $errorMessage",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                // Firestore에서 사용자 데이터 업데이트
                val userDocRef = db.collection("user").document(user.uid)

                userDocRef.update("name", newchangename)
                    .addOnSuccessListener {
                        // Firestore 업데이트 성공
                        Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                    }
                    .addOnFailureListener { e ->
                        // Firestore 업데이트 실패
                        Log.w(ContentValues.TAG, "Error updating document", e)
                    }
            } ?: run {
                // 사용자가 로그인되어 있지 않은 경우
                // 필요에 따라 사용자에게 로그인하라는 메시지를 보여줄 수 있음
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }
}