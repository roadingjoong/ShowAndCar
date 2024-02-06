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
import android.widget.CheckBox
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joljak.showandcar_app.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Women_BrandPreference.newInstance] factory method to
 * create an instance of this fragment.
 */
class Women_BrandPreference : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_women_brand_preference, container, false)

        val womenBrandChart = view.findViewById<PieChart>(R.id.women_BrandChart)

        womenBrandChart.setUsePercentValues(true)

        var selectedBrandIndex: Int = -1

        var WBData = ArrayList<PieEntry>()
        WBData.add(PieEntry(1f, "현대"))
        WBData.add(PieEntry(1f, "기아"))
        WBData.add(PieEntry(1f, "쌍용"))
        WBData.add(PieEntry(1f, "르노"))
        WBData.add(PieEntry(1f, "제네시스"))
        WBData.add(PieEntry(1f, "벤츠"))
        WBData.add(PieEntry(1f, "BMW"))
        WBData.add(PieEntry(1f, "아우디"))
        WBData.add(PieEntry(1f, "미니"))
        WBData.add(PieEntry(1f, "폭스바겐"))
        WBData.add(PieEntry(1f, "혼다"))
        WBData.add(PieEntry(1f, "링컨"))
        WBData.add(PieEntry(1f, "로터스"))
        WBData.add(PieEntry(1f, "랜드로버"))
        WBData.add(PieEntry(1f, "롤스로이스"))

        val WBColor = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) WBColor.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) WBColor.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) WBColor.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) WBColor.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) WBColor.add(c)
        WBColor.add(ColorTemplate.getHoloBlue())

        val db = Firebase.firestore
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
            colors = WBColor
            valueTextColor = Color.BLACK
            valueTextSize = 1f
        }
        WBDataSet.setDrawValues(false)

        val pieData = PieData(WBDataSet)
        womenBrandChart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = ""
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        womenBrandChart.legend.isEnabled = false

        val WomenSelectBrandList: MutableList<CheckBox> = mutableListOf()

        for (i in 1..15) {
            val WomenSelectString = "select_brand$i"
            val WomenSelectTrans = resources.getIdentifier(WomenSelectString, "id", view.context.packageName)
            val WomenSelectBrande = view.findViewById<CheckBox>(WomenSelectTrans)
            WomenSelectBrandList.add(WomenSelectBrande)

            WomenSelectBrande.setOnCheckedChangeListener{ buttonView, isChecked ->
                if(isChecked) {
                    for(Check in WomenSelectBrandList){
                        if(Check != buttonView){
                            Check.isChecked = false
                        }
                    }
                }
            }
        }

        //---------------------------------------------------------------------------
        val Women_BrandcompleteButton = view.findViewById<Button>(R.id.brandprebutton)

        Women_BrandcompleteButton.setOnClickListener {

            if (selectedBrandIndex != -1) {
                val oldValue = WBData[selectedBrandIndex].value
                val newPieEntryValue = oldValue + 1f
                WBData[selectedBrandIndex] = PieEntry(oldValue + 1f, WBData[selectedBrandIndex].label)

                val updatedPieData = PieData(PieDataSet(WBData, "").apply { colors = WBColor })

//              val db = Firebase.firestore

                val brandData = ArrayList<Pair<String, Float>>()
                brandData.add(Pair("현대", WBData[0].value))
                brandData.add(Pair("기아", WBData[1].value))
                brandData.add(Pair("쌍용", WBData[2].value))
                brandData.add(Pair("르노", WBData[3].value))
                brandData.add(Pair("제네시스", WBData[4].value))
                brandData.add(Pair("벤츠", WBData[5].value))
                brandData.add(Pair("BMW", WBData[6].value))
                brandData.add(Pair("아우디", WBData[7].value))
                brandData.add(Pair("미니", WBData[8].value))
                brandData.add(Pair("폭스바겐", WBData[9].value))
                brandData.add(Pair("혼다", WBData[10].value))
                brandData.add(Pair("링컨", WBData[11].value))
                brandData.add(Pair("로터스", WBData[12].value))
                brandData.add(Pair("랜드로버", WBData[13].value))
                brandData.add(Pair("롤스로이스", WBData[14].value))

                val chartData = HashMap<String, Float>()
                for (entry in brandData) {
                    chartData[entry.first] = entry.second
                }

                db.collection("womenbrandchart").document("changedBrandData")
                    .set(chartData)
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Changed brand data added successfully to Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding changed brand data to Firestore.", e)
                    }

                womenBrandChart.data = updatedPieData
                womenBrandChart.notifyDataSetChanged()
                womenBrandChart.invalidate()
            }
        }
        //------------------------체크 된 것 외의 체크박스 헤제----------------------
        for ((index, checkbox) in WomenSelectBrandList.withIndex()) {
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    for (i in WomenSelectBrandList.indices) {
                        if (i != index) {
                            WomenSelectBrandList[i].isChecked = false
                        } else {
                            selectedBrandIndex = index
                        }
                    }
                }
            }
        }
        //----------------------------------------------------

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Women_BrandPreference.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Women_BrandPreference().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}