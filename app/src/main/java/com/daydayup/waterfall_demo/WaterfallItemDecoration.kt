package com.daydayup.waterfall_demo

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class WaterfallItemDecoration(
    private val spacingPx : Int
) : RecyclerView.ItemDecoration(){

    //告诉recyclerView  给item 四周留多少空间
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        if (params == null) {
            outRect.set(0, 0, 0, 0)
            return
        }

        //fullSpan是指 这个item是否跨越了所有列
        if (params.isFullSpan) {
            //设置四个方向的留白
            outRect.set(spacingPx, spacingPx, spacingPx, spacingPx)
            return

        }
        //当前item在第几列 分别是0 1
        val spanIndex = params.spanIndex
        //一共两列
        val spanCount = 2

        // 经典网格间距分配公式，保证列间距和边缘间距统一
        //第 0 列（i=0）：left=s，right=s/2
        // 第 1 列（i=1）：left=s/2，right=s
        outRect.left = spacingPx - spanIndex * spacingPx / spanCount
        outRect.right = (spanIndex + 1) * spacingPx / spanCount
        outRect.top = spacingPx

    }

}