package com.joljak.showandcar_app.Fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.joljak.showandcar_app.CarsWon
import com.joljak.showandcar_app.MyAdapterWon
import com.joljak.showandcar_app.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CarManageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CarManageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapterCarsWon: MyAdapterWon
    private lateinit var newCarsWonRecyclerview: RecyclerView
    private var newCarsWonArrayList: ArrayList<CarsWon> = ArrayList()

    private lateinit var adapter: MyAdapterWon

    private lateinit var imageButton2: ImageButton

    private lateinit var deleteCar: Button
    private lateinit var updateCar: Button
    private lateinit var insertCar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeRecyclerView()

        val searchCarLayoutManager = LinearLayoutManager(context)

        newCarsWonRecyclerview = view.findViewById(R.id.recyclerView)
        newCarsWonRecyclerview.layoutManager = searchCarLayoutManager
        newCarsWonRecyclerview.setHasFixedSize(true)


        val fireStoreDatabase = FirebaseFirestore.getInstance()

        //val carInfoCollection = fireStoreDatabase.collection("carinfo")

        fireStoreDatabase.collection("carinfo")
            // 날짜 내림차순 정렬
            //.whereEqualTo("price", searchEditText)
            .orderBy("heading", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result != null && !result.isEmpty) {
                    // 게시물 목록 가져오기 성공
                    val carList = ArrayList<CarsWon>()

                    for (document in result) {
                        val image = document.getString("image") ?: ""
                        val heading = document.getString("heading") ?: ""
                        val carzong = document.getString("carzong") ?: ""
                        val oil = document.getString("oil") ?: ""
                        val content = document.getString("content") ?: ""
                        val price = document.getLong("price")?.toLong() ?: 0L


                        val carinfo = CarsWon(document.id, image, heading, carzong,
                            oil, content, price)
                        carList.add(carinfo)
                    }

                    // RecyclerView 어댑터 초기화 및 설정
                    adapter = MyAdapterWon(carList)
                    newCarsWonRecyclerview.adapter = adapter
                    adapter.notifyDataSetChanged()

                    // 아이템 클릭 이벤트 처리
                    adapter.setOnItemClickListener(object : MyAdapterWon.OnItemClickListener {
                        override fun onItemClick(heading: String) {

                            // Bundle을 사용하여 클릭한 게시물의 고유 ID를 car_view 프래그먼트로 전달
//                            val bundle = Bundle()
//                            bundle.putString("heading", heading)
//
//                            // 다음 프래그먼트로 전환하면서 Bundle을 전달
//                            val carViewFragment = Cars_View()
//                            carViewFragment.arguments = bundle
//
//                            // 프래그먼트 전환
//                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                            transaction.replace(R.id.frame, carViewFragment)
//                            transaction.addToBackStack(null)
//                            transaction.commit()
                        }
                    })
                } else {
                    // 게시물 목록이 비어있는 경우 처리
                }
            }
            .addOnFailureListener { exception ->
                // 게시물 목록 가져오기 실패 처리
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }


        imageButton2 = view.findViewById<ImageButton>(R.id.imageButton2)
        deleteCar = view.findViewById<Button>(R.id.deleteCar)
        updateCar = view.findViewById<Button>(R.id.updateCar)
        insertCar = view.findViewById<Button>(R.id.insertCar)

        imageButton2.setOnClickListener {
            requireActivity().onBackPressed()
        }

        deleteCar.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, DeleteFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        updateCar.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, ChooseUpdateTypeFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        insertCar.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, InsertFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }


    }

    private fun initializeRecyclerView() {
        val searchCarLayoutManager = LinearLayoutManager(context)
        newCarsWonRecyclerview = view?.findViewById(R.id.recyclerView) ?: return
        newCarsWonRecyclerview.layoutManager = searchCarLayoutManager
        newCarsWonRecyclerview.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_car_manage, container, false)
    }
}