package com.daydayup.waterfalldemo

import android.graphics.Color
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.daydayup.R

class WaterfallAdapter(
    initialItems: MutableList<WaterfallItem>
) : BaseQuickAdapter<WaterfallItem, BaseViewHolder>(
    R.layout.item_waterfall_card,
    initialItems
) {

    override fun convert(holder: BaseViewHolder, item: WaterfallItem) {
        val context = holder.itemView.context
        val cardContainer = holder.getView<LinearLayout>(R.id.card_container)

        // 动态高度：核心（模拟不同图片高度）
        val lp = cardContainer.layoutParams
        lp.height = dpToPx(context.resources.displayMetrics.density, item.heightDp)
        cardContainer.layoutParams = lp

        holder.setText(R.id.tv_title, item.title)
        cardContainer.setBackgroundColor(Color.parseColor(item.colorHex))

        holder.itemView.setOnClickListener {
            Toast.makeText(context, "点击：${item.title}", Toast.LENGTH_SHORT).show()
        }

        // fullSpan 控制：让某条目占满整行
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            layoutParams.isFullSpan = item.isFullSpan
            holder.itemView.layoutParams = layoutParams
        }
    }

    fun appendData(newData: List<WaterfallItem>) {
        if (newData.isNotEmpty()) {
            addData(newData)
        }
    }

    private fun dpToPx(density: Float, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            DisplayMetrics().apply { this.density = density }
        ).toInt()
    }
}