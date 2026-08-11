package com.daydayup.ViewPager2_demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daydayup.R

class BasicPagerAdapter (
    private val data : List<String>
): RecyclerView.Adapter<BasicPagerAdapter.BasicPageVH>() {

    class BasicPageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPage: TextView = itemView.findViewById(R.id.tv_page)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicPageVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_basic_page, parent, false)
        return BasicPageVH(view)
    }

    override fun onBindViewHolder(holder: BasicPageVH, position: Int) {
        holder.tvPage.text = "第 ${position + 1} 页：${data[position]}"
    }

    override fun getItemCount(): Int {
        return data.size
    }


}