package com.joljak.showandcar_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.imageview.ShapeableImageView

class MyAdapterWon(private val carsList: ArrayList<CarsWon>) : RecyclerView.Adapter<MyAdapterWon.MyViewHolder>() {

    private val carinfoDTOs: ArrayList<CarsWon> = ArrayList(carsList)
    private var onItemClickListener: OnItemClickListener? = null

    fun filter(query: String) {

        carsList.clear()

        if (query.isEmpty()) {
            carsList.addAll(carinfoDTOs)
        } else {
            val filteredList = carinfoDTOs.filter { car ->
                car.heading.contains(query, ignoreCase = true)
            }
            carsList.addAll(filteredList)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_test, parent, false)
        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {

        return carsList.size
    }

    fun submitList(newList: List<CarsWon>) {
        carsList.clear()
        carsList.addAll(newList)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(itemId: String) // 클릭한 아이템의 고유 ID를 전달
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = carsList[position]
        // Glide를 사용하여 이미지 로드 및 표시
        Glide.with(holder.itemView.context)
            .load(currentItem.image) // Contents 클래스에서 이미지 URL을 가져옴
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.titleImage)

        holder.tvHeading.text = currentItem.heading
        holder.tvBrand.text = currentItem.brand
        holder.tvOil.text = currentItem.oil
        holder.tvCarzong.text = currentItem.carzong
        holder.tvPrice.text = currentItem.price.toString()

        holder.itemView.setOnClickListener {
            val car = carsList[position]
            onItemClickListener?.onItemClick(car.heading)
        }

    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val titleImage : ShapeableImageView = itemView.findViewById(R.id.title_image_Won)
        val tvHeading : TextView = itemView.findViewById(R.id.tvHeadingWon)
        val tvBrand : TextView = itemView.findViewById(R.id.tvbrandWon)
        val tvOil : TextView = itemView.findViewById(R.id.tvOilWon)
        val tvCarzong : TextView = itemView.findViewById(R.id.tvCarzongWon)
        val tvPrice : TextView = itemView.findViewById(R.id.tvPriceWon)
    }
}