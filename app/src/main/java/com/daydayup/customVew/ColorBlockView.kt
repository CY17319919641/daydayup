package com.daydayup.customVew

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View




class ColorBlockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : View(context, attrs,defStyleAttr){

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4CAF50")
            style = Paint.Style.FILL
        }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.drawRect(0f,0f,width.toFloat(),height.toFloat(),paint)
    }
}