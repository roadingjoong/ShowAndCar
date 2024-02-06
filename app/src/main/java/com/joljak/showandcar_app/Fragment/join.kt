package com.joljak.showandcar_app.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.joljak.showandcar_app.CategoryAdapter
import com.joljak.showandcar_app.R
import com.joljak.showandcar_app.databinding.FragmentJoinBinding

class join : Fragment() {

    private lateinit var binding: FragmentJoinBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val gender = binding.etGender

        val categories = resources.getStringArray(R.array.gender)
        val categoryAdapter =
            CategoryAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        gender.adapter = categoryAdapter

        gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 아무 것도 선택되지 않은 경우 방지
                if (position >= 0) {
                    val selectedCategory = parent?.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 것도 선택되지 않았을 때 처리
            }
        }

        binding.registerBtn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()
            val tel = binding.etTel.text.toString()

            val errorMessage = when {
                email.isEmpty() || !isEmailValid(email) ->
                    "올바른 이메일 주소를 입력하세요."
                password.isEmpty() || !isPasswordValid(password) ->
                    "올바른 비밀번호를 입력하세요. 비밀번호는 영문, 숫자 및 특수 문자를 포함해야 합니다."
                tel.isEmpty() || !isPhoneNumberValid(tel) ->
                    "올바른 전화번호를 입력하세요. 전화번호는 '010'으로 시작하고 8자리 숫자여야 합니다."
                else -> null
            }

            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Firebase Authentication에서 사용자 등록 성공
                            val user = mAuth.currentUser
                            val uid = user?.uid

                            if (uid != null) {
                                val selectedCategory = gender.selectedItem.toString()

                                // Firestore에 사용자 정보 추가
                                val userData = hashMapOf(
                                    "name" to name,
                                    "email" to email,
                                    "password" to password,
                                    "gender" to selectedCategory,
                                    "tel" to tel
                                )

                                db.collection("user").document(uid)
                                    .set(userData)
                                    .addOnSuccessListener { documentReference ->
                                        Toast.makeText(
                                            requireContext(),
                                            "계정이 성공적으로 등록되었습니다!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // 로그인 페이지로 전환
                                        val intent = Intent(requireContext(), login::class.java)
                                        requireActivity().startActivity(intent)
                                        requireActivity().finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            requireContext(),
                                            "오류 발생: $e",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            // Firebase Authentication에서 사용자 등록 실패
                            val errorMessage = authTask.exception?.message
                            Toast.makeText(
                                requireContext(),
                                "사용자 등록 실패: $errorMessage",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        return binding.root
    }

    // 비밀번호가 영어, 숫자 및 특수 문자를 모두 포함하는지 확인하는 함수
    private fun isPasswordValid(password: String): Boolean {
        val hasLetter = password.matches(Regex(".*[A-Za-z].*"))
        val hasDigit = password.matches(Regex(".*\\d.*"))
        val hasSpecialChar = password.matches(Regex(".*[!@#\$%^&*()].*"))

        return hasLetter && hasDigit && hasSpecialChar
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // 전화번호가 010으로 시작하고 8자리 숫자를 포함하는지 확인하는 함수
    private fun isPhoneNumberValid(tel: String): Boolean {
        return tel.matches(Regex("^010\\d{8}$"))
    }
}
