package com.joljak.showandcar_app.Fragment

import android.content.ContentValues.TAG
import android.graphics.Color
import android.location.GnssAntennaInfo.Listener
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
import com.google.android.material.internal.CheckableGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.joljak.showandcar_app.R
import kotlinx.coroutines.selects.select

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Men_BrandPreference.newInstance] factory method to
 * create an instance of this fragment.
 */
class Men_BrandPreference : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_men_brand_preference, container, false)

        val menBrandChart = view.findViewById<PieChart>(R.id.men_BrandChart)

        menBrandChart.setUsePercentValues(true)

        var selectedBrandIndex: Int = -1

        var MBData = ArrayList<PieEntry>()
        MBData.add(PieEntry(1f, "현대"))
        MBData.add(PieEntry(1f, "기아"))
        MBData.add(PieEntry(1f, "쌍용"))
        MBData.add(PieEntry(1f, "르노"))
        MBData.add(PieEntry(1f, "제네시스"))
        MBData.add(PieEntry(1f, "벤츠"))
        MBData.add(PieEntry(1f, "BMW"))
        MBData.add(PieEntry(1f, "아우디"))
        MBData.add(PieEntry(1f, "미니"))
        MBData.add(PieEntry(1f, "폭스바겐"))
        MBData.add(PieEntry(1f, "혼다"))
        MBData.add(PieEntry(1f, "링컨"))
        MBData.add(PieEntry(1f, "로터스"))
        MBData.add(PieEntry(1f, "랜드로버"))
        MBData.add(PieEntry(1f, "롤스로이스"))

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

        val MenSelectBrandList: MutableList<CheckBox> = mutableListOf()

        for (i in 1..15) {
            val MenSelectString = "select_brand$i"
            val MenSelectTrans = resources.getIdentifier(MenSelectString, "id", view.context.packageName)
            val MenSelectBrande = view.findViewById<CheckBox>(MenSelectTrans)
            MenSelectBrandList.add(MenSelectBrande)

            MenSelectBrande.setOnCheckedChangeListener{ buttonView, isChecked ->
                if(isChecked) {
                    for(Check in MenSelectBrandList){
                        if(Check != buttonView){
                            Check.isChecked = false
                        }
                    }
                }
            }
        }

        //---------------------------------------------------------------------------
        val Men_BrandcompleteButton = view.findViewById<Button>(R.id.brandprebutton)

        Men_BrandcompleteButton.setOnClickListener {

            if (selectedBrandIndex != -1) {
                val oldValue = MBData[selectedBrandIndex].value
                val newPieEntryValue = oldValue + 1f
                MBData[selectedBrandIndex] = PieEntry(oldValue + 1f, MBData[selectedBrandIndex].label)

                val updatedPieData = PieData(PieDataSet(MBData, "").apply { colors = MBColor })

//              val db = Firebase.firestore

                val brandData = ArrayList<Pair<String, Float>>()
                brandData.add(Pair("현대", MBData[0].value))
                brandData.add(Pair("기아", MBData[1].value))
                brandData.add(Pair("쌍용", MBData[2].value))
                brandData.add(Pair("르노", MBData[3].value))
                brandData.add(Pair("제네시스", MBData[4].value))
                brandData.add(Pair("벤츠", MBData[5].value))
                brandData.add(Pair("BMW", MBData[6].value))
                brandData.add(Pair("아우디", MBData[7].value))
                brandData.add(Pair("미니", MBData[8].value))
                brandData.add(Pair("폭스바겐", MBData[9].value))
                brandData.add(Pair("혼다", MBData[10].value))
                brandData.add(Pair("링컨", MBData[11].value))
                brandData.add(Pair("로터스", MBData[12].value))
                brandData.add(Pair("랜드로버", MBData[13].value))
                brandData.add(Pair("롤스로이스", MBData[14].value))

                val chartData = HashMap<String, Float>()
                for (entry in brandData) {
                    chartData[entry.first] = entry.second
                }

                db.collection("menbrandchart").document("changedBrandData")
                    .set(chartData)
                    .addOnSuccessListener {
                        Log.d(TAG, "Changed brand data added successfully to Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding changed brand data to Firestore.", e)
                    }

                menBrandChart.data = updatedPieData
                menBrandChart.notifyDataSetChanged()
                menBrandChart.invalidate()
            }
        }
        //------------------------체크 된 것 외의 체크박스 헤제----------------------
        for ((index, checkbox) in MenSelectBrandList.withIndex()) {
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    for (i in MenSelectBrandList.indices) {
                        if (i != index) {
                            MenSelectBrandList[i].isChecked = false
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
         * @return A new instance of fragment Men_BrandPreference.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Men_BrandPreference().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}