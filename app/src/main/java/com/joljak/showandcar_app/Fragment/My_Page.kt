package com.joljak.showandcar_app.Fragment

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.joljak.showandcar_app.CategoryAdapter
import com.joljak.showandcar_app.R
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.isNotEmpty
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joljak.showandcar_app.databinding.FragmentJoinBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class My_Page : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var signUpLink : TextView
    private lateinit var loginLink : TextView
    private lateinit var logoutLink: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        checkLoginStatus()

        val currentUser = mAuth.currentUser

        db = FirebaseFirestore.getInstance()


        if (currentUser != null) {
            // Firestore에서 사용자 데이터 가져오기
            db.collection("user")
                .document(currentUser.uid) // 사용자의 고유 ID를 사용하여 문서를 참조합니다.
                .get()
                .addOnSuccessListener { document ->

                    if (document != null) {
                        val email = document.getString("email") ?: ""
                        val name = document.getString("name") ?: ""
                        val gender = document.getString("gender") ?: ""
                        val tel = document.getString("tel") ?: ""

                        // 가져온 데이터를 UI에 표시
                        val idTextView = view.findViewById<TextView>(R.id.idTextView)
                        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
                        val genderTextView = view.findViewById<TextView>(R.id.genderTextView)
                        val telTextView = view.findViewById<TextView>(R.id.telTextView)

                        idTextView.text = email
                        nameTextView.text = name
                        genderTextView.text = gender
                        telTextView.text = tel
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }
    private fun setupViews(view: View) {
        // 여기에 UI 초기화 또는 설정 로직 추가
        val idTextView = view.findViewById<TextView>(R.id.idTextView)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val genderTextView = view.findViewById<TextView>(R.id.genderTextView)
        val telTextView = view.findViewById<TextView>(R.id.telTextView)

        // 초기화
        idTextView.text = ""
        nameTextView.text = ""
        genderTextView.text = ""
        telTextView.text = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my__page, container, false)

        logoutLink = view.findViewById(R.id.logoutLink)
        signUpLink = view.findViewById(R.id.signUpLink)
        loginLink = view.findViewById(R.id.loginLink)

        val db = Firebase.firestore
        val dbuser = db.collection("user")

        mAuth = FirebaseAuth.getInstance()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            // 로그인 상태일 때
            showLogoutButton()
        } else {
            // 로그아웃 상태일 때
            showLoginSignUpButtons()
        }

        val EditButton = view.findViewById<Button>(R.id.editMy)

        EditButton.setOnClickListener(View.OnClickListener {
            val EditMyInformation = Edit_MyInfo()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame ,EditMyInformation)
            transaction.commit()
        })

        logoutLink.setOnClickListener {
            mAuth.signOut() // 로그아웃

            // UI 초기화
            setupViews(view)

            // 처음의 마이페이지로 이동
            replaceFragment(My_Page.newInstance("", ""))
        }

        signUpLink.setOnClickListener {
            val signUpFragment = com.joljak.showandcar_app.Fragment.join()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame, signUpFragment)
                .addToBackStack(null)
                .commit()
        }

        loginLink.setOnClickListener {
            val loginFragment = login()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame, loginFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
    private fun checkLoginStatus() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            // 사용자가 로그인한 경우
            showLogoutButton()
        } else {
            // 사용자가 로그아웃한 경우
            showLoginSignUpButtons()
        }
    }

    private fun signIn() {
        // 예시: 이메일과 비밀번호를 사용한 로그인
        mAuth.signInWithEmailAndPassword("user@example.com", "password")
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공 시
                    showLogoutButton()
                } else {
                    // 로그인 실패 시
                    // 여기에 실패 처리 코드를 추가
                }
            }
    }
    private fun showLoginSignUpButtons() {
        signUpLink.visibility = View.VISIBLE
        loginLink.visibility = View.VISIBLE
        logoutLink.visibility = View.GONE
    }

    private fun showLogoutButton() {
        logoutLink.visibility = View.VISIBLE
        signUpLink.visibility = View.GONE
        loginLink.visibility = View.GONE
    }
    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
    }

    class join : Fragment() {
        private var param1: String? = null
        private var param2: String? = null
        private var selectedCategory: String = ""

        private lateinit var binding: FragmentJoinBinding
        private lateinit var mAuth: FirebaseAuth
        private lateinit var db: FirebaseFirestore

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_home_main, container, false)

            return view
        }
        companion object {

            // TODO: Rename and change types and number of parameters
            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                join().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            My_Page().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}