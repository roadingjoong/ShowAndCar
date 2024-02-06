package com.joljak.showandcar_app.Fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.joljak.showandcar_app.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.joljak.showandcar_app.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class login : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        // 사용자가 이미 로그인한 경우 My-Page로 이동
        Handler().postDelayed({
            if (user != null) {
                checkIfAdminAndNavigate(user.uid)
            }
        }, 1000)

        // IDP를 사용한 로그인
        binding.loginBtn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val loggedInUser = mAuth.currentUser
                        Toast.makeText(requireContext(), "로그인 하셨습니다.", Toast.LENGTH_SHORT).show()
                        checkIfAdminAndNavigate(loggedInUser?.uid)
                    } else {
                        Toast.makeText(requireContext(), "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "유효한 이메일 또는 비밀번호를 입력하세요", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "유효한 이메일 또는 비밀번호를 입력하세요", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.signUpLink.setOnClickListener {
            // 여기에 회원가입 페이지로 이동하는 코드를 추가
            val signUpFragment = join()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame, signUpFragment)
                .addToBackStack(null)
                .commit()
        }
        return view
    }

    private fun checkIfAdminAndNavigate(userId: String?) {
        val adminUid = "BNd4yKWZqWZ9Xn7qcHINkEtD2dO2"
        val user = mAuth.currentUser

        if (userId == adminUid) {
            // 관리자로 로그인한 경우
            navigateToAdminPage()
        } else {
            // 일반 사용자로 로그인한 경우
            navigateToMyPage(user?.displayName, user?.email)
        }
    }

    private fun navigateToAdminPage() {
        // 관리자 페이지로 이동
        val adminPageFragment = App_manager()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame, adminPageFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToMyPage(username: String?, userEmail: String?) {
        // 전달할 데이터를 Bundle에 담아 My_Page 프래그먼트로 전달
        val bundle = Bundle()
        bundle.putString("username", username)
        bundle.putString("userEmail", userEmail)

        val myPageFragment = My_Page()
        myPageFragment.arguments = bundle

        // 프래그먼트 전환
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame, myPageFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
