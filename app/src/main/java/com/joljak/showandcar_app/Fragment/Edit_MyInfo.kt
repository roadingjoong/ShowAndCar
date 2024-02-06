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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.joljak.showandcar_app.R

class Edit_MyInfo : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)

        mAuth = FirebaseAuth.getInstance()

        setupViews(view)

        val currentUser = mAuth.currentUser


        db = FirebaseFirestore.getInstance()

        if (currentUser != null) {
            // Firestore에서 사용자 데이터 가져오기
            db.collection("user")
                .document(currentUser.uid) // 사용자의 고유 ID를 사용하여 문서를 참조합니다.
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val pw = document.getString("password") ?: ""
                        val name = document.getString("name") ?: ""
                        val tel = document.getString("tel") ?: ""

                        // 가져온 데이터를 UI에 표시
                        val pwTextView = view.findViewById<TextView>(R.id.mypw)
                        val nameTextView = view.findViewById<TextView>(R.id.myname)
                        val telTextView = view.findViewById<TextView>(R.id.mytel)

                        pwTextView.text = pw
                        nameTextView.text = name
                        telTextView.text = tel

                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                }
        }
    }

    private fun setupViews(view: View) {
        // 여기에 UI 초기화 또는 설정 로직 추가
        val pwTextView = view.findViewById<TextView>(R.id.mypw)
        val nameTextView = view.findViewById<TextView>(R.id.myname)
        val telTextView = view.findViewById<TextView>(R.id.mytel)

        // 초기화
        pwTextView.text = ""
        nameTextView.text = ""
        telTextView.text = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit__my_info, container, false)

        val editpw = view.findViewById<Button>(R.id.pw_change)
        editpw.setOnClickListener(View.OnClickListener {
            val EditmyPW = Edit_My_PW()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, EditmyPW)
            transaction.commit()
        })

        val editname = view.findViewById<Button>(R.id.name_change)
        editname.setOnClickListener(View.OnClickListener {
            val EditmyName = Edit_My_Name()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, EditmyName)
            transaction.commit()
        })

        val edittel = view.findViewById<Button>(R.id.tel_change)
        edittel.setOnClickListener(View.OnClickListener {
            val EditmyTel = Edit_My_Tel()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, EditmyTel)
            transaction.commit()
        })

        return view
    }

}