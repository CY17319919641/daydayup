package com.daydayup.recyclerview_demo

import android.databinding.tool.writer.ViewBinding
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daydayup.R
import com.daydayup.databinding.ActivityRecyclerBinding
import java.math.BigDecimal
import java.math.RoundingMode

class RecyclerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerBinding

    private lateinit var recyclerView : RecyclerView
    private lateinit var tvTopAmount : TextView

    private var currentTotalAmount =123456.0

    private val adapter by lazy{
        WithdrawTierAdapter(
            data = mutableListOf(),
            totalRedAmount = currentTotalAmount
        )
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerView
        tvTopAmount = binding.tvTopAmount

        initRecyclerView()
        loadInitData()
        renderTopAmount()

        binding.btnAddAmount.setOnClickListener {
            currentTotalAmount += 8888
            renderTopAmount()
            adapter.updateTotalAmount(currentTotalAmount)
            Toast.makeText(this, "已模拟增加余额", Toast.LENGTH_SHORT).show()
        }

    }

    private fun initRecyclerView(){

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.addFooter(recyclerView)

        adapter.setOnItemChildClickListener{ baseAdapter,clickedView,position ->

            if(clickedView.id != R.id.tvAction && clickedView.id != R.id.bgAction){
                return@setOnItemChildClickListener
            }

            val item = baseAdapter.getItem(position) as? WithdrawTier ?: return@setOnItemChildClickListener

            when(item.status){
                WithdrawTier.STATUS_PENDING -> {
                    item.status = WithdrawTier.STATUS_PAYING
                    adapter.notifyItemChanged(position)
                    toast("第${item.conditionLevel}关：开始自动打款")
                }

                WithdrawTier.STATUS_PAYING -> {
                    item.status = WithdrawTier.STATUS_DELIVERING
                    item.fixedAmount = calcTierAmountText(item.multiple)
                    adapter.notifyItemChanged(position)
                    toast("第${item.conditionLevel}关：进入到账中")
                }

                WithdrawTier.STATUS_DELIVERING -> {
                    item.status = WithdrawTier.STATUS_DONE
                    adapter.notifyItemChanged(position)
                    toast("第${item.conditionLevel}关：已到账")
                }

                WithdrawTier.STATUS_DONE -> {
                    toast("第${item.conditionLevel}关：已到账，无需重复操作")
                }
            }

        }

    }
    private fun loadInitData() {
        val seed = mutableListOf(
            WithdrawTier(conditionLevel = 5, multiple = 120f, status = WithdrawTier.STATUS_PENDING),
            WithdrawTier(conditionLevel = 10, multiple = 150f, status = WithdrawTier.STATUS_PENDING),
            WithdrawTier(conditionLevel = 15, multiple = 180f, status = WithdrawTier.STATUS_DELIVERING, fixedAmount = "2.22"),
            WithdrawTier(conditionLevel = 20, multiple = 220f, status = WithdrawTier.STATUS_DONE, fixedAmount = "5.66")
        )
        adapter.setNewInstance(seed)
    }
    private fun renderTopAmount() {
        val text = BigDecimal.valueOf(currentTotalAmount)
            .divide(BigDecimal.valueOf(10000L), 2, RoundingMode.DOWN)
            .toPlainString()
        tvTopAmount.text = "当前红包总额：$text"
    }
    private fun calcTierAmountText(multiple: Float): String {
        return BigDecimal.valueOf(currentTotalAmount)
            .multiply(BigDecimal.valueOf(multiple.toDouble()))
            .divide(BigDecimal.valueOf(10000L), 2, RoundingMode.DOWN)
            .toPlainString()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}