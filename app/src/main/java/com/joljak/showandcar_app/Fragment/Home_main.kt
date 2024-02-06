package com.joljak.showandcar_app.Fragment


import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
//import android.widget.ArrayAdapter
//import android.widget.ListView
//import android.widget.Toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joljak.showandcar_app.R
import kotlin.random.Random

class Home_main : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_main, container, false)
        //------------

        val menBrandChart = view.findViewById<PieChart>(R.id.men_BrandChart)

        menBrandChart.setUsePercentValues(true)

        val MBData = ArrayList<PieEntry>()
        MBData.add(PieEntry(40f, "현대"))
        MBData.add(PieEntry(20f, "제네시스"))
        MBData.add(PieEntry(20f, "벤츠"))
        MBData.add(PieEntry(15f, "BMW"))
        MBData.add(PieEntry(5f, "기아"))

        val MBColor = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) MBColor.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) MBColor.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) MBColor.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) MBColor.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) MBColor.add(c)
        MBColor.add(ColorTemplate.getHoloBlue())

        val db = Firebase.firestore
        val thismenbrandfireData = db.collection("menbrandchart").document("changedBrandData")

        thismenbrandfireData.get()
            .addOnSuccessListener { document->
                if(document != null && document.data != null){
                    val thismenbrandData = document.data as Map<String, Float>
                    MBData.clear()
                    for ((key, value) in thismenbrandData) {
                        MBData.add(PieEntry(value, key))
                    }
                    val updatedMBDataSet = PieDataSet(MBData, "")
                    updatedMBDataSet.colors = MBColor
                    updatedMBDataSet.valueTextColor = Color.BLACK
                    updatedMBDataSet.valueTextSize = 1f
                    updatedMBDataSet.setDrawValues(false)

                    val updatedPieData = PieData(updatedMBDataSet)

                    menBrandChart.data = updatedPieData
                    menBrandChart.notifyDataSetChanged()
                    menBrandChart.invalidate()
                }
            }

        val MBDataSet = PieDataSet(MBData, "")
        MBDataSet.apply {
            colors = MBColor
            valueTextColor = Color.BLACK
            valueTextSize = 1f
        }
        MBDataSet.setDrawValues(false)

        val pieData = PieData(MBDataSet)
        menBrandChart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = ""
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        menBrandChart.legend.isEnabled = false
        //-----------------
        //-----------------
        val menModelChart = view.findViewById<PieChart>(R.id.men_ModelChart)

        menBrandChart.setUsePercentValues(true)

        val MMData = ArrayList<PieEntry>()
        MMData.add(PieEntry(40f, "세단"))
        MMData.add(PieEntry(20f, "SUV"))
        MMData.add(PieEntry(20f, "컨버터블"))
        MMData.add(PieEntry(15f, "해치백"))
        MMData.add(PieEntry(5f, "밴"))

        val MMColor = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) MMColor.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) MMColor.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) MMColor.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) MMColor.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) MMColor.add(c)
        MMColor.add(ColorTemplate.getHoloBlue())


        val thismenModelfireData = db.collection("menModelchart").document("changedModelData")

        thismenModelfireData.get()
            .addOnSuccessListener { document->
                if(document != null && document.data != null){
                    val thismenModelData = document.data as Map<String, Float>
                    MMData.clear()
                    for ((key, value) in thismenModelData) {
                        MMData.add(PieEntry(value, key))
                    }
                    val updatedMMDataSet = PieDataSet(MMData, "")
                    updatedMMDataSet.colors = MMColor
                    updatedMMDataSet.valueTextColor = Color.BLACK
                    updatedMMDataSet.valueTextSize = 1f
                    updatedMMDataSet.setDrawValues(false)

                    val updatedPieData = PieData(updatedMMDataSet)

                    menModelChart.data = updatedPieData
                    menModelChart.notifyDataSetChanged()
                    menModelChart.invalidate()
                }
            }


        val MMDataSet = PieDataSet(MMData, "")
        MMDataSet.apply {
            colors = MMColor
            valueTextColor = Color.BLACK
            valueTextSize = 1f
        }

        MMDataSet.setDrawValues(false)

        val MMpieData = PieData(MMDataSet)
        menModelChart.apply {
            data = MMpieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = ""
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        menModelChart.legend.isEnabled = false
        //-------------------------
        //-------------------------
        val womenBrandChart = view.findViewById<PieChart>(R.id.women_BrandChart)

        womenBrandChart.setUsePercentValues(true)

        val WBData = ArrayList<PieEntry>()
        WBData.add(PieEntry(40f, "제네시스"))
        WBData.add(PieEntry(30f, "벤츠"))
        WBData.add(PieEntry(20f, "현대"))
        WBData.add(PieEntry(30f, "BMW"))
        WBData.add(PieEntry(20f, "기아"))

        val WBColor = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) WBColor.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) WBColor.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) WBColor.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) WBColor.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) WBColor.add(c)
        WBColor.add(ColorTemplate.getHoloBlue())

        val thiswomenbrandfireData = db.collection("womenbrandchart").document("changedBrandData")

        thiswomenbrandfireData.get()
            .addOnSuccessListener { document->
                if(document != null && document.data != null){
                    val thiswomenbrandData = document.data as Map<String, Float>
                    WBData.clear()
                    for ((key, value) in thiswomenbrandData) {
                        WBData.add(PieEntry(value, key))
                    }
                    val updatedWBDataSet = PieDataSet(WBData, "")
                    updatedWBDataSet.colors = WBColor
                    updatedWBDataSet.valueTextColor = Color.BLACK
                    updatedWBDataSet.valueTextSize = 1f
                    updatedWBDataSet.setDrawValues(false)

                    val updatedPieData = PieData(updatedWBDataSet)

                    womenBrandChart.data = updatedPieData
                    womenBrandChart.notifyDataSetChanged()
                    womenBrandChart.invalidate()
                }
            }


        val WBDataSet = PieDataSet(WBData, "")
        WBDataSet.apply {
            colors = MBColor
            valueTextColor = Color.BLACK
            valueTextSize = 1f
        }
        WBDataSet.setDrawValues(false)

        val WBpieData = PieData(WBDataSet)
        womenBrandChart.apply {
            data = WBpieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = ""
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        womenBrandChart.legend.isEnabled = false
        //---------------------
        //---------------------
        val womenModelChart = view.findViewById<PieChart>(R.id.women_ModelChart)

        womenBrandChart.setUsePercentValues(true)


        val WMData = ArrayList<PieEntry>()
        WMData.add(PieEntry(50f, "세단"))
        WMData.add(PieEntry(80f, "SUV"))
        WMData.add(PieEntry(20f, "컨버터블"))
        WMData.add(PieEntry(10f, "해치백"))
        WMData.add(PieEntry(5f, "밴"))

        val WMColor = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) WMColor.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) WMColor.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) WMColor.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) WMColor.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) WMColor.add(c)
        WMColor.add(ColorTemplate.getHoloBlue())


        val thiswomenModelfireData = db.collection("womenModelchart").document("changedModelData")

        thiswomenModelfireData.get()
            .addOnSuccessListener { document->
                if(document != null && document.data != null){
                    val thiswomenModelData = document.data as Map<String, Float>
                    WMData.clear()
                    for ((key, value) in thiswomenModelData) {
                        WMData.add(PieEntry(value, key))
                    }
                    val updatedWMDataSet = PieDataSet(WMData, "")
                    updatedWMDataSet.colors = WMColor
                    updatedWMDataSet.valueTextColor = Color.BLACK
                    updatedWMDataSet.valueTextSize = 1f
                    updatedWMDataSet.setDrawValues(false)

                    val updatedPieData = PieData(updatedWMDataSet)

                    womenModelChart.data = updatedPieData
                    womenModelChart.notifyDataSetChanged()
                    womenModelChart.invalidate()
                }
            }


        val WMDataSet = PieDataSet(WMData, "")
        WMDataSet.apply {
            colors = WBColor
            valueTextColor = Color.BLACK
            valueTextSize = 1f
        }

        WMDataSet.setDrawValues(false)

        val WMpieData = PieData(WMDataSet)
        womenModelChart.apply {
            data = WMpieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = ""
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        womenModelChart.legend.isEnabled = false

        //----------

        val thisreferenceButton = view.findViewById<Button>(R.id.reference_Button)

        thisreferenceButton.setOnClickListener(View.OnClickListener {
            val thispreference_part = preference_part()
            val thisWpreference_part = Wpreference_part()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            mAuth = FirebaseAuth.getInstance()
            this.db = FirebaseFirestore.getInstance()
            val currentUser = mAuth.currentUser

            if (currentUser != null) {
                // Firestore에서 사용자 데이터 가져오기
                db.collection("user")
                    .document(currentUser.uid) // 사용자의 고유 ID를 사용하여 문서를 참조합니다.
                    .get()
                    .addOnSuccessListener { document ->

                        if (document != null) {
                            val gender = document.getString("gender") ?: ""

                            if(gender.equals("남성")){
                                transaction.replace(R.id.frame, thispreference_part)
                                transaction.commit()
                            }else if (gender.equals("여성")){
                                transaction.replace(R.id.frame, thisWpreference_part)
                                transaction.commit()
                            }else{
                                Toast.makeText(requireContext(), "로그인 후 이용 해 주세요..", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Log.d(ContentValues.TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(ContentValues.TAG, "get failed with ", exception)
                    }
            }

        })
        //-----------

        return view

    }

}