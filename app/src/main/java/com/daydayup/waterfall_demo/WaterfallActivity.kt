package com.daydayup.waterfall_demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.daydayup.waterfall_demo.WaterfallAdapter
import com.daydayup.waterfall_demo.WaterfallMockData
import com.daydayup.databinding.ActivityWaterfallBinding
import com.daydayup.waterfall_demo.WaterfallItemDecoration

class WaterfallActivity : AppCompatActivity() {


    private lateinit var binding: ActivityWaterfallBinding
    private lateinit var adapter: WaterfallAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager

    private var loading = false
    private var nextId = 1000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterfallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        initData()
        initLoadMore()
    }

    private fun initRecyclerView() {
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        //设置空隙处理政策 当两列高度不一致时，尽量补齐空隙
        layoutManager.gapStrategy =
            StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(false)

        val spacing = resources.displayMetrics.density.times(8).toInt()
        binding.recyclerView.addItemDecoration(WaterfallItemDecoration(spacing))
    }
    private fun initData() {
        val initialData = WaterfallMockData.createInitialList(30)
        adapter = WaterfallAdapter(initialData)
        binding.recyclerView.adapter = adapter
    }
    private fun initLoadMore() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            //dx > 0 ：向右滑动 dx < 0 ：向左滑动
            //dy > 0 ：向下滑动 dy < 0 ：向上滑动
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0 || loading) return

                // StaggeredGridLayoutManager 需要读取“最后可见位置数组”
                val lastVisiblePositions = layoutManager.findLastVisibleItemPositions(null)
                val lastVisible = lastVisiblePositions.maxOrNull() ?: 0
                val totalCount = adapter.itemCount

                if (lastVisible >= totalCount - 6) {
                    loadMore()
                }
            }
        })
    }
    private fun loadMore() {
        loading = true
        binding.recyclerView.postDelayed({
            val more = WaterfallMockData.createMoreList(nextId, 20)
            nextId += more.size
            adapter.appendData(more)
            loading = false
        }, 500)
    }
}