package com.daydayup.animationdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.daydayup.base.BaseFragment
import com.daydayup.databinding.FragmentAnimation1Binding

class AnimationFragment1 : BaseFragment<FragmentAnimation1Binding>() {


    override fun provideViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAnimation1Binding {
        return FragmentAnimation1Binding.inflate(inflater,container,false)
    }

    override fun initUI() {
        binding.animation1.setOnClickListener {
            it.slideInFRomTopAndOut()
        }
        binding.animation2.setOnClickListener {
            it.shakeLeftToRight()
        }
        binding.animation3.setOnClickListener {
            it.fadeInThenOut() {
                Toast.makeText(requireActivity(), "结束淡出动画", Toast.LENGTH_SHORT).show()
            }
        }
        binding.animation4.setOnClickListener {
            it.rotateClockwise()
        }
        binding.animation5.setOnClickListener {
            it.slightFloat()
        }
        binding.animation6.setOnClickListener {
            it.bounce()
        }
        binding.animation7.setOnClickListener {
            it.slideInFromLeftAndOut()
        }
        binding.animation8.setOnClickListener {
            it.shrinkAndDisappear()
        }
        binding.animation9.setOnClickListener {
            it.growAndAppear()
        }
        binding.animation10.setOnClickListener {
            it.animationScaleDownFromZAxis()
        }
        binding.animation11.setOnClickListener {
            it.breath()
        }
        binding.animation12.setOnClickListener {
            digitalSmoothChange(binding.animation13,15.57f,17.68f)
        }
        binding.animation14.setOnClickListener {
            it.rotateShake()
        }
        binding.animation15.setOnClickListener {
            it.rotateByY {
                Toast.makeText(requireActivity(), "结束旋转动画", Toast.LENGTH_SHORT).show()
            }
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