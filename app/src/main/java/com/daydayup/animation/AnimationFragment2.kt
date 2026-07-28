package com.daydayup.animation

import android.animation.Animator
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.daydayup.R
import com.daydayup.base.BaseFragment
import com.daydayup.databinding.FragmentAnimation1Binding
import com.daydayup.databinding.FragmentAnimation2Binding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnimationFragment2 : BaseFragment<FragmentAnimation2Binding>() {


    override fun provideViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAnimation2Binding {
        return FragmentAnimation2Binding.inflate(inflater,container,false)
    }

    override fun initUI() {
        binding.animation1.setOnClickListener {
            binding.lottie.progress = 0f
            binding.lottie.playAnimation()
        }
        binding.animation2.setOnClickListener {
            setLottieAnimationWithFrameRange(binding.lottie,24,45)
        }
        binding.animation3.setOnClickListener {
            binding.lottie.cancelAnimation()
        }

    }

    override fun initData() {
        // 初始化数据，目前暂无需要初始化的数据
    }

    override fun onKeyCodeBack(): Boolean {
        // 返回 false 表示不处理返回键，让系统默认处理
        return false
    }

}