package com.joljak.showandcar_app.Fragment

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
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
 * Use the [Search_car.newInstance] factory method to
 * create an instance of this fragment.
 */


class Search_car : Fragment() {

    private var isInitialFilterDone = false
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var searchEditText: EditText
    private val filteredCarsList = ArrayList<CarsWon>()

    private lateinit var adapterCarsWon: MyAdapterWon
    private lateinit var newCarsWonRecyclerview: RecyclerView
    private var newCarsWonArrayList: ArrayList<CarsWon> = ArrayList()

    private lateinit var adapter: MyAdapterWon

    private lateinit var searchView: SearchView

    data class CheckBoxState(val checkBox: CheckBox, var isChecked: Boolean = false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(requireContext())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Search_car.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Search_car().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        return inflater.inflate(R.layout.fragment_search_car, container, false)
    }

    private var priceFilter: Int = 0
    private val selectedCarTypes = mutableListOf<String>()
    private val selectedBrands = mutableListOf<String>()
    private val selectedFuels = mutableListOf<String>()
    private val checkBoxStates = mutableMapOf<Int, CheckBoxState>()

    ///////////////////////////////////////////////////////////////////////////////////////
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val db = Firebase.firestore

        initializeCheckBoxes()

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
                            val bundle = Bundle()
                            bundle.putString("heading", heading)

                            // 다음 프래그먼트로 전환하면서 Bundle을 전달
                            val carViewFragment = Cars_View()
                            carViewFragment.arguments = bundle

                            // 프래그먼트 전환
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.frame, carViewFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
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

        val map_button = view.findViewById<Button>(R.id.map_Button)

        map_button.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, GoogleMapFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val searchView = view.findViewById<SearchView>(R.id.searchView)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                adapter.filter(newText.orEmpty())

                return true
            }
        })

        val searchEditText = view.findViewById<EditText>(R.id.pricewhat)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 변경 전
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                priceFilter = s.toString().toIntOrNull() ?: 0
                // 변경 후

                // 다른 필터링 옵션 가져오기
                val selectedCarTypes = getSelectedCarTypes()
                val selectedBrands = getSelectedBrands()
                val selectedFuels = getSelectedFuels()

                // Firestore에서 검색 및 결과 업데이트
                filterCars(priceFilter, selectedCarTypes, selectedBrands, selectedFuels)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        // 다른 필터링 옵션을 가져오는 도우미 함수
        for (checkBoxId in checkBoxStates.keys) {
            val checkBox = view?.findViewById<CheckBox>(checkBoxId)
            checkBox?.setOnCheckedChangeListener { _, isChecked ->
                // 체크박스 상태가 변경될 때 필터링 작업 실행
                applyFilters()
            }
        }
    }








    private fun initializeCheckBoxes() {
        val carzongCheckBoxIds = arrayOf(
            R.id.checkSedan, R.id.checkSUV, R.id.checkCoupe, R.id.checkHatchback
        )

        for (checkBoxId in carzongCheckBoxIds) {
            val checkBox = view?.findViewById<CheckBox>(checkBoxId)
            checkBox?.setOnCheckedChangeListener { _, isChecked ->
                applyFilters()
            }
        }

        val oilCheckBoxIds = arrayOf(
            R.id.checkGasoline, R.id.checkDiesel, R.id.checkElectric, R.id.checkHybrid
        )

        for (checkBoxId in oilCheckBoxIds) {
            val checkBox = view?.findViewById<CheckBox>(checkBoxId)
            checkBox?.setOnCheckedChangeListener { _, isChecked ->
                applyFilters()
            }
        }

        val brandCheckBoxIds = arrayOf(
            R.id.checkHyundai, R.id.checkKia, R.id.checkRenault, R.id.checkSsangYong, R.id.checkLincoln,
            R.id.checkGenesis, R.id.checkMercedes, R.id.checkAudi, R.id.checkBMW, R.id.checkVolkswagen,
            R.id.checkMini, R.id.checkRollsRoyce, R.id.checkLandRover, R.id.checkLotus, R.id.checkHonda
        )

        for (checkBoxId in brandCheckBoxIds) {
            val checkBox = view?.findViewById<CheckBox>(checkBoxId)
            checkBox?.setOnCheckedChangeListener { _, isChecked ->
                applyFilters()
            }
        }
    }

    private fun applyFilters() {
        // applyFilters 함수에서 선택한 체크박스를 저장하고 필터링 작업 수행
        val searchEditText = view?.findViewById<EditText>(R.id.pricewhat)
        val priceFilter = searchEditText?.text.toString().toIntOrNull() ?: 0

        selectedCarTypes.clear()
        if (view?.findViewById<CheckBox>(R.id.checkSedan)?.isChecked == true) {
            selectedCarTypes.add("세단")
        }
        if (view?.findViewById<CheckBox>(R.id.checkSUV)?.isChecked == true) {
            selectedCarTypes.add("SUV")
        }
        if (view?.findViewById<CheckBox>(R.id.checkCoupe)?.isChecked == true) {
            selectedCarTypes.add("쿠페")
        }
        if (view?.findViewById<CheckBox>(R.id.checkHatchback)?.isChecked == true) {
            selectedCarTypes.add("헤치백")
        }

        selectedBrands.clear()
        // 수정: 브랜드 체크박스 선택 처리 추가
        val brandCheckBoxIds = arrayOf(
            R.id.checkHyundai, R.id.checkKia, R.id.checkRenault, R.id.checkSsangYong, R.id.checkLincoln,
            R.id.checkGenesis, R.id.checkMercedes, R.id.checkAudi, R.id.checkBMW, R.id.checkVolkswagen,
            R.id.checkMini, R.id.checkRollsRoyce, R.id.checkLandRover, R.id.checkLotus, R.id.checkHonda
        )
        for (checkBoxId in brandCheckBoxIds) {
            val checkBox = view?.findViewById<CheckBox>(checkBoxId)
            val brandName = checkBox?.text.toString()
            if (checkBox?.isChecked == true) {
                selectedBrands.add(brandName)
            }
        }

        selectedFuels.clear()
        if (view?.findViewById<CheckBox>(R.id.checkGasoline)?.isChecked == true) {
            selectedFuels.add("가솔린")
        }
        if (view?.findViewById<CheckBox>(R.id.checkDiesel)?.isChecked == true) {
            selectedFuels.add("디젤")
        }
        if (view?.findViewById<CheckBox>(R.id.checkElectric)?.isChecked == true) {
            selectedFuels.add("전기")
        }
        if (view?.findViewById<CheckBox>(R.id.checkHybrid)?.isChecked == true) {
            selectedFuels.add("하이브리드")
        }

        // 수정: 선택한 체크박스를 기반으로 필터링 작업 수행
        filterCars(priceFilter, selectedCarTypes, selectedBrands, selectedFuels)
        //filterCars(0, selectedCarTypes, selectedBrands, selectedFuels)
    }

    private fun getSelectedCarTypes(): List<String> {
        val carTypes = ArrayList<String>()
        // 체크된 차종을 carTypes에 추가
        if (view?.findViewById<CheckBox>(R.id.checkSedan)!!.isChecked) {
            carTypes.add("세단")
        }
        if (view?.findViewById<CheckBox>(R.id.checkSUV)!!.isChecked) {
            carTypes.add("SUV")
        }
        if (view?.findViewById<CheckBox>(R.id.checkCoupe)!!.isChecked) {
            carTypes.add("쿠페")
        }
        if (view?.findViewById<CheckBox>(R.id.checkHatchback)!!.isChecked) {
            carTypes.add("헤치백")
        }

        return carTypes
    }

    private fun getSelectedBrands(): List<String> {
        val brands = ArrayList<String>()
        // 체크된 브랜드를 brands에 추가

        val brandCheckBoxIds = arrayOf(
            R.id.checkHyundai, R.id.checkKia, R.id.checkRenault, R.id.checkSsangYong, R.id.checkLincoln,
            R.id.checkGenesis, R.id.checkMercedes, R.id.checkAudi, R.id.checkBMW, R.id.checkVolkswagen,
            R.id.checkMini, R.id.checkRollsRoyce, R.id.checkLandRover, R.id.checkLotus, R.id.checkHonda
        )

        for (checkBoxId in brandCheckBoxIds) {
            val checkBox = view?.findViewById<CheckBox>(checkBoxId)
            val brandName = checkBox?.text.toString()
            if (checkBox?.isChecked == true) {
                brands.add(brandName)
            }
        }

        return brands
    }

    private fun getSelectedFuels(): List<String> {
        val fuels = ArrayList<String>()
        // 체크된 연료 유형을 fuels에 추가
        if (view?.findViewById<CheckBox>(R.id.checkGasoline)!!.isChecked) {
            fuels.add("가솔린")
        }
        if (view?.findViewById<CheckBox>(R.id.checkDiesel)!!.isChecked) {
            fuels.add("디젤")
        }
        if (view?.findViewById<CheckBox>(R.id.checkElectric)!!.isChecked) {
            fuels.add("전기")
        }
        if (view?.findViewById<CheckBox>(R.id.checkHybrid)!!.isChecked) {
            fuels.add("하이브리드")
        }
        return fuels
    }

    private fun filterCars(
        priceFilter: Int,
        carTypes: List<String>,
        brands: List<String>,
        fuels: List<String>
    ) {
        val fireStoreDatabase = FirebaseFirestore.getInstance()
        val carInfoCollection = fireStoreDatabase.collection("carinfo")

        var query: Query = carInfoCollection

        // 가격 필터링
        if (priceFilter > 0) {
            query = query.whereEqualTo("price", priceFilter)
        }

        // 2. 추가: 차종, 브랜드, 연료 필터링
        if (selectedCarTypes.isNotEmpty()) {
            query = query.whereIn("carzong", selectedCarTypes)
        }

        if (selectedBrands.isNotEmpty()) {
            query = query.whereIn("brand", selectedBrands)
        }

        if (selectedFuels.isNotEmpty()) {
            query = query.whereIn("oil", selectedFuels)
        }

        query.get()
            .addOnSuccessListener { result ->
                if (result != null && !result.isEmpty) {
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
                            // 클릭 이벤트 처리
                        }
                    })
                } else {
                    // 검색 결과가 없는 경우 처리
                }
            }
            .addOnFailureListener { exception ->
                // 검색 실패 처리
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

}