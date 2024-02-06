package com.joljak.showandcar_app.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.google.firebase.auth.FirebaseAuth
import com.joljak.showandcar_app.MainActivity
import com.joljak.showandcar_app.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class App_manager : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var data_manage: Button
    private lateinit var comment_manage: Button
    private lateinit var post_manage: Button

    lateinit var mAuth: FirebaseAuth

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
        val manageView = inflater.inflate(R.layout.fragment_app_manager, container, false)
        val logoutButton = manageView.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener { logoutLink() }

        return manageView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data_manage = view.findViewById(R.id.data_manage)
        data_manage.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, CarManageFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
        comment_manage = view.findViewById(R.id.comment_manage)
        comment_manage.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, ChatManageFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        post_manage = view.findViewById(R.id.post_manage)
        post_manage.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, PostManageFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }


    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            App_manager().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun logoutLink() {
        try {
            mAuth = FirebaseAuth.getInstance()
            mAuth.signOut() // Log out

            // Navigate to the my page
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)

            // Clear the back stack and finish the current activity
            activity?.finishAffinity()
        } catch (e: Exception) {
            Log.e("AppManagerFragment", "로그아웃 중 오류 발생: ${e.message}")
        }
    }
}