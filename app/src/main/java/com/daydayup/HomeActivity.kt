package com.daydayup

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.daydayup.viewpager2demo.ViewPager2StudyActivity
import com.daydayup.animationdemo.AnimationActivity
import com.daydayup.base.BaseActivity
import com.daydayup.customview.CustomViewActivity
import com.daydayup.databinding.ActivityHomeBinding
import com.daydayup.fragmentlearningdemo.FragmentLearningActivity
import com.daydayup.recyclerviewdemo.RecyclerActivity
import com.daydayup.waterfalldemo.WaterfallActivity
import kotlin.reflect.KClass

class HomeActivity : BaseActivity<ActivityHomeBinding>(){
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // BaseActivity 已经调用了 setContentView(binding.root)，不需要再次调用
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun provideViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun initUI() {
        binding.goAnimation.setOnClickListener {
            val intent = Intent(this, AnimationActivity::class.java)
            startActivity(intent)
        }
        binding.goViewpager.setOnClickListener {
            val intent = Intent(this, ViewPager2StudyActivity::class.java)
            startActivity(intent)
        }
        binding.goCustomview.setOnClickListener {
            val intent = Intent(this, CustomViewActivity::class.java)
            startActivity(intent)
        }
        binding.goWaterfall.setOnClickListener {
            val intent = Intent(this, WaterfallActivity::class.java)
            startActivity(intent)
        }
        binding.goFragment.setOnClickListener {
            val intent = Intent(this, FragmentLearningActivity::class.java)
            startActivity(intent)
        }
        binding.goRecyclerView.setOnClickListener {
            val intent = Intent(this, RecyclerActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initData() {
        // 初始化数据，目前暂无需要初始化的数据
    }

    fun showDialogByType(clazz: KClass<*>, params: Any? = null) {

    }
    fun removeFullScreenFrag(tag: String, exitAnim: Int = -1) {

    }
    fun showFullScreenFrag(frag: Fragment, enterAnim: Int = -1) {

    }
    fun isShowFragment(): Boolean {

        return  true
    }
}