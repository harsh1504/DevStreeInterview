package com.example.devstreepraticaltask.adapter

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.devstreepraticaltask.R
import com.example.devstreepraticaltask.databinding.DeletePlaceDialogBinding
import com.example.devstreepraticaltask.databinding.RowMapDataLayoutBinding
import com.example.devstreepraticaltask.model.MapModel

class MapDataAdapter(val context: Context, val allDataList: ArrayList<MapModel>, val adapterClick: AdapterClick):
    RecyclerView.Adapter<MapDataAdapter.MapDataViewHolder>() {


    inner class MapDataViewHolder(val rowMapDataLayoutBinding: RowMapDataLayoutBinding):
        RecyclerView.ViewHolder(rowMapDataLayoutBinding.root) {

    }

    fun notify(id: Int, position: Int) {
        allDataList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapDataViewHolder {
        val rowMapDataLayoutBinding = RowMapDataLayoutBinding.inflate(LayoutInflater.from(context), parent, false);
        return MapDataViewHolder(rowMapDataLayoutBinding)
    }

    override fun onBindViewHolder(holder: MapDataViewHolder, position: Int) {
        holder.rowMapDataLayoutBinding.txtPlaceName.text = allDataList.get(position).placeName

        holder.rowMapDataLayoutBinding.imgDelete.setOnClickListener {
            adapterClick.delete(allDataList.get(position).id, position)
        }

        holder.rowMapDataLayoutBinding.imgEdit.setOnClickListener {
            adapterClick.updateData(allDataList.get(position).id, allDataList.get(position).placeName,
                allDataList.get(position).latLng, allDataList.get(position).latitude, allDataList.get(position).longitude)
        }

    }

    override fun getItemCount(): Int {
        return allDataList.size
    }

    interface AdapterClick {
        fun delete(id: Int, position: Int)
        fun updateData(id: Int, pName: String, latlng: String, lat: Double, longt: Double)
    }

}