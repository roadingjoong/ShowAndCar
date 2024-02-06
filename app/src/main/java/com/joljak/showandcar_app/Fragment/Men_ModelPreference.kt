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
 * Use the [Men_ModelPreference.newInstance] factory method to
 * create an instance of this fragment.
 */
class Men_ModelPreference : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_men_model_preference, container, false)

        val menModelChart = view.findViewById<PieChart>(R.id.men_ModelChart)

        menModelChart.setUsePercentValues(true)

        var selectedModelIndex: Int = -1

        var MMData = ArrayList<PieEntry>()
        MMData.add(PieEntry(1f, "세단"))
        MMData.add(PieEntry(1f, "SUV"))
        MMData.add(PieEntry(1f, "쿠페"))
        MMData.add(PieEntry(1f, "헤치백"))

        val MMColor = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) MMColor.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) MMColor.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) MMColor.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) MMColor.add(c)
        MMColor.add(ColorTemplate.getHoloBlue())

        val db = Firebase.firestore
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

        val pieData = PieData(MMDataSet)
        menModelChart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = ""
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        menModelChart.legend.isEnabled = false

        val MenSelectModelList: MutableList<CheckBox> = mutableListOf()

        for (i in 1..4) {
            val MenSelectString = "select_Model$i"
            val MenSelectTrans = resources.getIdentifier(MenSelectString, "id", view.context.packageName)
            val MenSelectModel = view.findViewById<CheckBox>(MenSelectTrans)
            MenSelectModelList.add(MenSelectModel)

            MenSelectModel.setOnCheckedChangeListener{ buttonView, isChecked ->
                if(isChecked) {
                    for(Check in MenSelectModelList){
                        if(Check != buttonView){
                            Check.isChecked = false
                        }
                    }
                }
            }
        }

        //---------------------------------------------------------------------------
        val Men_ModelcompleteButton = view.findViewById<Button>(R.id.Modelprebutton)

        Men_ModelcompleteButton.setOnClickListener {

            if (selectedModelIndex != -1) {
                val oldValue = MMData[selectedModelIndex].value
                val newPieEntryValue = oldValue + 1f
                MMData[selectedModelIndex] = PieEntry(oldValue + 1f, MMData[selectedModelIndex].label)

                val updatedPieData = PieData(PieDataSet(MMData, "").apply { colors = MMColor })

//              val db = Firebase.firestore

                val ModelData = ArrayList<Pair<String, Float>>()
                ModelData.add(Pair("세단", MMData[0].value))
                ModelData.add(Pair("SUV", MMData[1].value))
                ModelData.add(Pair("쿠페", MMData[2].value))
                ModelData.add(Pair("헤치백", MMData[3].value))

                val chartData = HashMap<String, Float>()
                for (entry in ModelData) {
                    chartData[entry.first] = entry.second
                }

                db.collection("menModelchart").document("changedModelData")
                    .set(chartData)
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Changed Model data added successfully to Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding changed Model data to Firestore.", e)
                    }

                menModelChart.data = updatedPieData
                menModelChart.notifyDataSetChanged()
                menModelChart.invalidate()
            }
        }
//        ------------------------체크 된 것 외의 체크박스 헤제----------------------
        for ((index, checkbox) in MenSelectModelList.withIndex()) {
            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    for (i in MenSelectModelList.indices) {
                        if (i != index) {
                            MenSelectModelList[i].isChecked = false
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
         * @return A new instance of fragment Men_ModelPreference.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Men_ModelPreference().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}