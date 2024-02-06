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
 * Use the [preference_part.newInstance] factory method to
 * create an instance of this fragment.
 */
class preference_part : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_preference_part, container, false)

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

        menModelChart.setUsePercentValues(true)

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

        val thisbrandbutton = view.findViewById<Button>(R.id.brandButton)
        thisbrandbutton.setOnClickListener(View.OnClickListener {
            val thisMenBrandPreference = Men_BrandPreference()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, thisMenBrandPreference)
            transaction.commit()
        })

        val thismodelbutton = view.findViewById<Button>(R.id.modelButton)
        thismodelbutton.setOnClickListener(View.OnClickListener{
            val thisMenModelPreference = Men_ModelPreference()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, thisMenModelPreference)
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
         * @return A new instance of fragment preference_part.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            preference_part().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}