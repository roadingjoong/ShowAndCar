package com.joljak.showandcar_app.Fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
 * Use the [Wpreference_part.newInstance] factory method to
 * create an instance of this fragment.
 */
class Wpreference_part : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_wpreference_part, container, false)

        val womenBrandChart = view.findViewById<PieChart>(R.id.women_BrandChart)

        womenBrandChart.setUsePercentValues(true)

        val WBData = ArrayList<PieEntry>()
        WBData.add(PieEntry(40f, "현대"))
        WBData.add(PieEntry(20f, "제네시스"))
        WBData.add(PieEntry(20f, "벤츠"))
        WBData.add(PieEntry(15f, "BMW"))
        WBData.add(PieEntry(5f, "기아"))

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
        //-----------------
        //-----------------
        val womenModelChart = view.findViewById<PieChart>(R.id.women_ModelChart)

        womenModelChart.setUsePercentValues(true)

        val WMData = ArrayList<PieEntry>()
        WMData.add(PieEntry(40f, "세단"))
        WMData.add(PieEntry(20f, "SUV"))
        WMData.add(PieEntry(20f, "컨버터블"))
        WMData.add(PieEntry(15f, "해치백"))
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
            colors = WMColor
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

        val thisbrandbutton = view.findViewById<Button>(R.id.brandButton)
        thisbrandbutton.setOnClickListener(View.OnClickListener {
            val thisWomenBrandPreference = Women_BrandPreference()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, thisWomenBrandPreference)
            transaction.commit()
        })

        val thismodelbutton = view.findViewById<Button>(R.id.modelButton)
        thismodelbutton.setOnClickListener(View.OnClickListener{
            val thisWomenModelPreference = Women_ModelPreference()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, thisWomenModelPreference)
            transaction.commit()
        })

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Wpreference_part.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Wpreference_part().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}