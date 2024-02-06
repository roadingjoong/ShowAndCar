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
 * Use the [Women_ModelPreference.newInstance] factory method to
 * create an instance of this fragment.
 */
class Women_ModelPreference : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_women_model_preference, container, false)

        val womenModelChart = view.findViewById<PieChart>(R.id.women_ModelChart)

        womenModelChart.setUsePercentValues(true)

        var selectedModelIndex: Int = -1

        var WMData = ArrayList<PieEntry>()
        WMData.add(PieEntry(1f, "세단"))
        WMData.add(PieEntry(1f, "SUV"))
        WMData.add(PieEntry(1f, "쿠페"))
        WMData.add(PieEntry(1f, "헤치백"))

        val WMColor = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) WMColor.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) WMColor.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) WMColor.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) WMColor.add(c)
        WMColor.add(ColorTemplate.getHoloBlue())

        val db = Firebase.firestore
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

        val pieData = PieData(WMDataSet)
        womenModelChart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = ""
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        womenModelChart.legend.isEnabled = false

        val WomenSelectModelList: MutableList<CheckBox> = mutableListOf()

        for (i in 1..4) {
            val WomenSelectString = "select_Model$i"
            val WomenSelectTrans = resources.getIdentifier(WomenSelectString, "id", view.context.packageName)
            val WomenSelectModel = view.findViewById<CheckBox>(WomenSelectTrans)
            WomenSelectModelList.add(WomenSelectModel)

            WomenSelectModel.setOnCheckedChangeListener{ buttonView, isChecked ->
                if(isChecked) {
                    for(Check in WomenSelectModelList){
                        if(Check != buttonView){
                            Check.isChecked = false
                        }
                    }
                }
            }
        }

        //---------------------------------------------------------------------------
        val Women_ModelcompleteButton = view.findViewById<Button>(R.id.Modelprebutton)

        Women_ModelcompleteButton.setOnClickListener {

            if (selectedModelIndex != -1) {
                val oldValue = WMData[selectedModelIndex].value
                val newPieEntryValue = oldValue + 1f
                WMData[selectedModelIndex] = PieEntry(oldValue + 1f, WMData[selectedModelIndex].label)

                val updatedPieData = PieData(PieDataSet(WMData, "").apply { colors = WMColor })

//              val db = Firebase.firestore

                val ModelData = ArrayList<Pair<String, Float>>()
                ModelData.add(Pair("세단", WMData[0].value))
                ModelData.add(Pair("SUV", WMData[1].value))
                ModelData.add(Pair("쿠페", WMData[2].value))
                ModelData.add(Pair("헤치백", WMData[3].value))

                val chartData = HashMap<String, Float>()
                for (entry in ModelData) {
                    chartData[entry.first] = entry.second
                }

                db.collection("womenModelchart").document("changedModelData")
                    .set(chartData)
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Changed Model data added successfully to Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding changed Model data to Firestore.", e)
                    }

                womenModelChart.data = updatedPieData
                womenModelChart.notifyDataSetChanged()
                womenModelChart.invalidate()
            }
        }
//        ------------------------체크 된 것 외의 체크박스 헤제----------------------
        for ((index, checkbox) in WomenSelectModelList.withIndex()) {
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    for (i in WomenSelectModelList.indices) {
                        if (i != index) {
                            WomenSelectModelList[i].isChecked = false
                        } else {
                            selectedModelIndex = index
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
         * @return A new instance of fragment Women_ModelPreference.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Women_ModelPreference().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}