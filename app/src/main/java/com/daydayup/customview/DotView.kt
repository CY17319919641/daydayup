package com.daydayup.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.daydayup.R

class DotView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs,defStyleAttr){

    //颜色
    private var dotColor = Color.RED

    private var dotRadius = 10f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
    }

    init{
        //从传入的参数集合中读值 否则使用默认值
        context.obtainStyledAttributes(attrs,R.styleable.DotView).apply {
            dotColor = getColor(R.styleable.DotView_dotColor,Color.RED)
            dotRadius = getDimension(R.styleable.DotView_dotRadius,10f)
            //这里是非常重要的内存回收逻辑
            recycle()
        }
        paint.color = dotColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desired = (dotRadius * 2  + paddingLeft + paddingRight).toInt()
        //根据测量规则和期望值进行测量
        val measuredW = resolveSize(desired,widthMeasureSpec)
        val measuredH = resolveSize(desired,heightMeasureSpec)
        //保存测量结果 通知父布局
        setMeasuredDimension(measuredW,measuredH)
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        canvas.drawCircle(centerX,centerY,dotRadius,paint)
    }


}