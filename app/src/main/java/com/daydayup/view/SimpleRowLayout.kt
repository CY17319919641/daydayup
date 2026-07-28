package com.daydayup.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.isGone

class SimpleRowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs,defStyleAttr){

    private val gap = dp(8f).toInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var usedWidth = paddingLeft + paddingRight
        var maxHeight = 0

        for(i in 0 until childCount){
            var child = getChildAt(i)
            if (child.visibility == GONE) continue
            //更新子View的测量结果 child.measure(childWidthMeasureSpec,childHeightMeasureSpec)
            //参数分别是 子View 父容器的宽度约束 已经被占用的宽度空间 父容器的高度约束
            measureChildWithMargins(child,widthMeasureSpec,usedWidth,heightMeasureSpec,0)
            var lp = child.layoutParams as MarginLayoutParams
            usedWidth += child.measuredWidth + lp.leftMargin + lp.rightMargin
            if(i != childCount - 1) usedWidth += gap

            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
            maxHeight = maxHeight.coerceAtLeast(childHeight)
        }

        val finalW = resolveSize(usedWidth,widthMeasureSpec)
        val finalH = resolveSize(maxHeight+paddingTop+paddingBottom,heightMeasureSpec)
        setMeasuredDimension(finalW,finalH)

    }
    //参数分别是当前ViewGroup相对应父容器的约束 分别是 left top right bottom
    //viewGroup 的对应的矩形
    override fun onLayout(
        p0: Boolean,
        p1: Int,
        p2: Int,
        p3: Int,
        p4: Int
    ) {
        var curLeft = paddingLeft
        val topBase = paddingTop

        for(i in 0 until childCount){
            val child = getChildAt(i)
            if (child.visibility == GONE) continue

            val lp = child.layoutParams as MarginLayoutParams
            val left = curLeft + lp.leftMargin
            val top = topBase + lp.topMargin
            val right = left + child.measuredWidth
            val bottom = top + child.measuredHeight

            child.layout(left,top,right,bottom)
            curLeft += child.measuredWidth + lp.leftMargin + lp.rightMargin + gap
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }
    //当子 View 没提供参数时，给默认 LayoutParams（这里是 WRAP_CONTENT）
    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }
    //把已有参数对象转换/拷贝成当前容器可用的 MarginLayoutParams。
    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }
    //校验子 View 的参数类型是否合法（这里要求是 MarginLayoutParams）
    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }


    private fun dp(v:Float): Float = v* resources.displayMetrics.density

}