package com.joljak.showandcar_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joljak.showandcar_app.Fragment.Community_User
import com.joljak.showandcar_app.Fragment.Home_main
import com.joljak.showandcar_app.Fragment.My_Page
import com.joljak.showandcar_app.Fragment.Review_List
import com.joljak.showandcar_app.Fragment.Search_car
import com.joljak.showandcar_app.Fragment.login

class MainActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(itmes: MenuItem):Boolean {

        when (itmes.itemId) {
            R.id.home -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, Home_main())
                transaction.commit()
                return true
            }

            R.id.search -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, Search_car())
                transaction.commit()
                return true
            }

            R.id.review -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, Review_List())
                transaction.commit()
                return true
            }

            R.id.community -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, Community_User())
                transaction.commit()
                return true
            }

            R.id.mypage -> {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, login())
                transaction.commit()
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val initialFragment = Home_main()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, initialFragment)
        transaction.addToBackStack(null)
        transaction.commit()

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)

        navigation.setOnNavigationItemSelectedListener(this)

        FirebaseApp.initializeApp(this)

        val db = Firebase.firestore
    }
}