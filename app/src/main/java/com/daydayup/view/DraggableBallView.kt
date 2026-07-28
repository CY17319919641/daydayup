package com.daydayup.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColorInt
import com.daydayup.R

class DraggableBallView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context,attrs,defStyleAttr) {
    private var radius = dp(24f)
    private var cx = dp(60f)
    private var cy = dp(60f)


    init {
        context.obtainStyledAttributes(attrs,R.styleable.DraggableBallView).apply {
            radius =getDimension(R.styleable.DraggableBallView_radius,radius)
            recycle()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#FF0000".toColorInt()
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(cx,cy,radius,paint)
    }

    override fun onTouchEvent(event: MotionEvent) : Boolean {
        when(event.actionMasked){
            MotionEvent.ACTION_DOWN -> {
                //请求父容器不拦截触摸事件
                parent.requestDisallowInterceptTouchEvent(true)
                cx = event.x
                cy = event.y
                invalidate()
                return true

            }
            MotionEvent.ACTION_MOVE -> {
                cx = event.x.coerceIn(radius,width-radius)
                cy = event.y.coerceIn(radius,height-radius)
                invalidate()
                return true
            }
            //触摸事件结束 手指抬起 或者事件被取消
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)

    }

    private fun dp(v:Float): Float = v* resources.displayMetrics.density
}