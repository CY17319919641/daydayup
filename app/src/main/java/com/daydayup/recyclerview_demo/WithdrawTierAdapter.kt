package com.daydayup.recyclerview_demo

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.daydayup.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.temporal.TemporalAmount
import androidx.core.graphics.toColorInt

class WithdrawTierAdapter(
    layoutResId : Int = R.layout.item_withdraw_tier,
    data : MutableList<WithdrawTier>? = null,
    private var totalRedAmount: Double = 0.0
) : BaseQuickAdapter<WithdrawTier, BaseViewHolder>(layoutResId,data){

    private var footerView : View? = null

    init{
        //如果需要单独点击item里面的子控件 需要单独注册
        addChildClickViewIds(R.id.tvAction,R.id.bgAction)
    }

    fun addFooter(parent: ViewGroup){
        //防止重复添加
        if(footerView == null){
            footerView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_footer,parent,false)
            addFooterView(footerView!!)
        }
    }

    fun updateTotalAmount(amount:Double){
        totalRedAmount = amount
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder,item: WithdrawTier){
        val multiplierText = "${item.multiple}倍"
        holder.setText(R.id.tvMultiplier,multiplierText)
        holder.setText(R.id.tvLevel,"第${item.conditionLevel}关")

        // 2) 金额字段：到账中/已到账优先显示快照金额
        val amountText = item.fixedAmount ?: BigDecimal.valueOf(totalRedAmount)
            .multiply(BigDecimal.valueOf(item.multiple.toDouble()))
            .divide(BigDecimal.valueOf(10000L), 2, RoundingMode.DOWN)
            .toPlainString()
        holder.setText(R.id.tvAmount, amountText)

       val amountTv = holder.getView<TextView>(R.id.tvAmount)
        //使用这个写法可以永远拿到默认字号 而不是修改过后的字号
        //.also作用域永远返回的是it本身的值
        val defaultSizePx = (amountTv.getTag(R.id.tvAmount)as? Float)
            ?: amountTv.textSize.also{amountTv.setTag(R.id.tvAmount,it)}
        val scaledSizePx = when {
            amountText.length > 11 -> defaultSizePx * 0.75f
            amountText.length > 10 -> defaultSizePx * 0.8f
            amountText.length > 9 -> defaultSizePx * 0.85f
            amountText.length > 8 -> defaultSizePx * 0.9f
            else -> defaultSizePx
        }

        amountTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,scaledSizePx)

        val actionTextView = holder.getView<TextView>(R.id.tvAction)
        val actionBg = holder.getView<View>(R.id.bgAction)
        when (item.status){
            WithdrawTier.STATUS_PENDING->{
                actionTextView.text = "待打款"
                actionBg.setBackgroundColor("#FFA924".toColorInt())
                amountTv.setTextColor("#00B76E".toColorInt())
            }

            WithdrawTier.STATUS_PAYING -> {
                actionTextView.text = "自动打款中"
                actionBg.setBackgroundColor("#09BF62".toColorInt())
                amountTv.setTextColor("#00B76E".toColorInt())
            }

            WithdrawTier.STATUS_DELIVERING -> {
                actionTextView.text = "到账中"
                actionBg.setBackgroundColor("#03ACE5".toColorInt())
                amountTv.setTextColor("#00B76E".toColorInt())
            }

            WithdrawTier.STATUS_DONE -> {
                actionTextView.text = "已到账"
                actionBg.setBackgroundColor("#D0D0D0".toColorInt())
                amountTv.setTextColor("#D0D0D0".toColorInt())
            }
        }

    }

}