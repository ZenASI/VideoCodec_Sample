package com.example.videocodec_sample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.videocodec_sample.R
import com.example.videocodec_sample.model.FilterItem

class FilterAdapter(private val dataSet: List<FilterItem>) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_camera_filter, parent, false)
        return ViewHolder(view)
    }

    var listener: ((pos: Int) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].filterName
        holder.imageView.setImageResource(R.drawable.ic_camera)
        holder.root.setOnClickListener {
            listener?.invoke(position)
        }
    }

    override fun getItemCount() = dataSet.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: AppCompatTextView
        val imageView: AppCompatImageView
        val root: LinearLayoutCompat

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.filterName)
            imageView = view.findViewById(R.id.filterThumb)
            root = view.findViewById(R.id.filterRoot)
        }
    }
}