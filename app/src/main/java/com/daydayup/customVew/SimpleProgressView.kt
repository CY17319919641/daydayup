package com.daydayup.customVew

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class SimpleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs,defStyleAttr){

    private var max = 100
    private var progress = 0

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.GRAY }
    private val fgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLUE }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)
        var r = height / 2f
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rect,r,r,bgPaint)

        val percent = (progress / max.toFloat()).coerceIn(0f,1f)
        val fgRect = RectF(0f, 0f, width.toFloat() * percent, height.toFloat())
        canvas.drawRoundRect(fgRect,r,r,fgPaint)
    }

    fun setProgress(value : Int){
        //value值小于0就置为0，大于max就置为max，否则保持不变
        val newValue = value.coerceIn(0,max)
        if(newValue == progress) return
        progress = newValue
        //通知重绘
        invalidate()
    }

}