package com.daydayup.customVew

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.daydayup.R

class StrokeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs,defStyleAttr){

    private var borderText: AppCompatTextView? = null

    init{
        borderText = AppCompatTextView(context, attrs, defStyleAttr)

        //把 StrokeTextView 相关的样式属性解析出来
        val a = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView, defStyleAttr, 0)
        val strokeColor = a.getColor(
            R.styleable.StrokeTextView_st_strokeColor,
            ContextCompat.getColor(context, R.color.white)
        )
        val strokeSize = a.getInt(R.styleable.StrokeTextView_st_strokeSize, 0).toFloat()
        a.recycle()

        val border = borderText
        if (border != null) {
            val paint: TextPaint = border.paint
            paint.strokeWidth = strokeSize
            paint.style = Paint.Style.STROKE
            border.setTextColor(strokeColor)

            syncTextStyle()
            border.text = text
        }
    }

    override fun setText(text : CharSequence?, type : BufferType?){
        super.setText(text, type ?: BufferType.NORMAL)
        val border = borderText ?: return
        if(border.text != text){
            border.text = text
        }
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        //先给TextView设置LayoutParams
        super.setLayoutParams(params)
        //再给borderText设置LayoutParams
        borderText?.layoutParams = params
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val border = borderText
        if(border != null && border.text != text){
            border.text = text
        }
        syncTextStyle()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        border?.measure(
            MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed : Boolean, left : Int, top : Int, right : Int, bottom : Int){
        super.onLayout(changed, left, top, right, bottom)
        borderText?.layout(0, 0, measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        //在这里增加了 移动描边文字位置的逻辑
        canvas.save()
        canvas.translate(10f, 10f)
        borderText?.draw(canvas)
        canvas.restore()
        super.onDraw(canvas)
    }

    private fun syncTextStyle(){
        val border = borderText ?: return
        border.gravity = gravity
        border.includeFontPadding = includeFontPadding
        border.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        border.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        border.typeface = typeface
        border.maxLines = maxLines
        border.ellipsize = ellipsize
        border.letterSpacing = letterSpacing
        border.setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
    }

}
