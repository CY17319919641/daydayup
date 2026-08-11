package com.daydayup.animation_demo

import android.animation.Animator
import android.graphics.PointF
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.daydayup.R
import com.daydayup.base.BaseFragment
import com.daydayup.databinding.FragmentAnimation3Binding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnimationFragment3.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnimationFragment3 : BaseFragment<FragmentAnimation3Binding>() {


    override fun provideViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAnimation3Binding {
        return FragmentAnimation3Binding.inflate(inflater,container,false)

    }

    override fun initUI() {
        binding.animation1.setOnClickListener {
            showWxAnim(binding.start.getViewCenterLocationInScreen(),
                binding.end.getViewCenterLocationInScreen(),
                binding.main,
                animationFinish = null)
        }
        binding.animation2.setOnClickListener {
            showRedAnim(binding.start.getViewCenterLocationInScreen(),
                binding.end.getViewCenterLocationInScreen(),
                binding.main,
                animationFinish = null)
        }
        binding.animation3.setOnClickListener {
            showCoinAnim(binding.start.getViewCenterLocationInScreen(),
                binding.end.getViewCenterLocationInScreen(),
                binding.main,
                animationFinish = null)
        }



    }

    override fun initData() {
        // 初始化数据，目前暂无需要初始化的数据
    }

    override fun onKeyCodeBack(): Boolean {
        // 返回 false 表示不处理返回键，让系统默认处理
        return false
    }
    fun showWxAnim(
        coinPointStart: PointF,
        coinPointEnd: PointF,
        windowRootView: ViewGroup,
        drawableRes: Int = R.drawable.ic_icon,
        animationFinish: (() -> Unit)?,
        isNoAnimation: Boolean? = false
    ) {
        if (isNoAnimation == true){
            lifecycleScope.launch {
                delay(1000)
                animationFinish?.invoke()
            }
            return
        }
        val coinDrawable =
            ResourcesCompat.getDrawable(windowRootView.context.resources, drawableRes, null)
        var cnt = 3
        var soundPlayFlag = false
        // 设置 z 属性
//        windowRootView.z = 999f
        // 调用 bringToFront() 确保视图在父布局中最后绘制
        windowRootView.bringToFront()
        // 强制父布局重新排列子视图
        windowRootView.parent.requestLayout()
        // 设置 elevation 属性（如果需要阴影效果）
//        windowRootView.elevation = 999f
        // 如果需要自定义阴影形状，设置 ViewOutlineProvider
        windowRootView.outlineProvider = ViewOutlineProvider.BACKGROUND
        windowRootView.clipToOutline = true

        // 强制视图重绘，应用新的层级设置
        windowRootView.invalidate()
        coinDrawable?.let { _coinDrawable ->
            // 分裂动画的中心点
            val centerPoint = PointF(coinPointStart.x, coinPointStart.y)

            // 计算6个金币的位置
            val positions = mutableListOf<PointF>()
            for (i in 0 until cnt) {
                val angle = Math.toRadians((i * 80).toDouble())
                val distance = 100f // 金币与中心点的距离
                val x = centerPoint.x + distance * Math.cos(angle).toFloat()
                val y = centerPoint.y + distance * Math.sin(angle).toFloat()
                positions.add(PointF(x, y))
            }

            // 创建初始的金币
            val initialCoin = ImageView(windowRootView.context)
            initialCoin.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            initialCoin.setImageDrawable(_coinDrawable)
            initialCoin.x = centerPoint.x - _coinDrawable.intrinsicWidth / 2f
            initialCoin.y = centerPoint.y - _coinDrawable.intrinsicHeight / 2f
            windowRootView.addView(initialCoin)

            // 增加一个过渡动画，使初始金币先放大再缩小
            initialCoin.animate()
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(200)
                .withEndAction {
                    initialCoin.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {}

                            override fun onAnimationEnd(animation: Animator) {
                                // 分裂动画
                                initialCoin.animate()
                                    .setDuration(100)
                                    .setListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(animation: Animator) {}

                                        override fun onAnimationEnd(animation: Animator) {
                                            windowRootView.removeView(initialCoin)
                                            for (i in 0 until cnt) {
                                                val ivCoin = ImageView(windowRootView.context)
                                                ivCoin.layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                                )
                                                ivCoin.setImageDrawable(_coinDrawable)
                                                ivCoin.x =
                                                    positions[i].x - _coinDrawable.intrinsicWidth / 2f
                                                ivCoin.y =
                                                    positions[i].y - _coinDrawable.intrinsicHeight / 2f
                                                windowRootView.addView(ivCoin)

                                                ivCoin.animate()
                                                    .x(coinPointEnd.x)
                                                    .y(coinPointEnd.y)
                                                    .setInterpolator(AccelerateInterpolator())
                                                    .setDuration(200)
                                                    .setStartDelay(i * 80L)
                                                    .setListener(object :
                                                        Animator.AnimatorListener {
                                                        override fun onAnimationStart(animation: Animator) {
                                                            if (!soundPlayFlag) {
                                                                soundPlayFlag = true
//                                                                if (showCoinSound) {
//                                                                    SoundPoolManager.showSound(R.raw.fly_red)
//                                                                }
                                                            }
                                                        }

                                                        override fun onAnimationEnd(animation: Animator) {
                                                            windowRootView.removeView(ivCoin)
                                                            cnt--
                                                            if (cnt == 0) {
                                                                animationFinish?.invoke()
                                                            }
                                                        }

                                                        override fun onAnimationCancel(animation: Animator) {}

                                                        override fun onAnimationRepeat(animation: Animator) {}
                                                    })
                                                    .start()
                                            }
                                        }

                                        override fun onAnimationCancel(animation: Animator) {}

                                        override fun onAnimationRepeat(animation: Animator) {}
                                    })
                                    .start()
                            }

                            override fun onAnimationCancel(animation: Animator) {}

                            override fun onAnimationRepeat(animation: Animator) {}
                        })
                        .start()
                }
                .start()
        }
    }


    fun showRedAnim(
        coinPointStart: PointF,
        coinPointEnd: PointF,
        windowRootView: ViewGroup,
        drawableRes: Int = R.drawable.ic_icon,
        animationFinish: (() -> Unit)?,
        isNoAnimation: Boolean? = false
    ) {
        if (isNoAnimation == true){
            lifecycleScope.launch {
                delay(1000)
                animationFinish?.invoke()
            }
            return
        }
        val coinDrawable =
            ResourcesCompat.getDrawable(windowRootView.context.resources, drawableRes, null)
        var cnt = 3
        var soundPlayFlag = false
//        windowRootView.z= 999f
        coinDrawable?.let { _coinDrawable ->
            // 分裂动画的中心点
            val centerPoint = PointF(coinPointStart.x, coinPointStart.y)

            // 计算6个金币的位置
            val positions = mutableListOf<PointF>()
            for (i in 0 until cnt) {
                val angle = Math.toRadians((i * 60).toDouble())
                val distance = 100f // 金币与中心点的距离
                val x = centerPoint.x + distance * Math.cos(angle).toFloat()
                val y = centerPoint.y + distance * Math.sin(angle).toFloat()
                positions.add(PointF(x, y))
            }

            // 创建初始的金币
            val initialCoin = ImageView(windowRootView.context)
            initialCoin.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            initialCoin.setImageDrawable(_coinDrawable)
            initialCoin.x = centerPoint.x - _coinDrawable.intrinsicWidth / 2f
            initialCoin.y = centerPoint.y - _coinDrawable.intrinsicHeight / 2f
            windowRootView.addView(initialCoin)

            // 增加一个过渡动画，使初始金币先放大再缩小
            initialCoin.animate()
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(200)
                .withEndAction {
                    initialCoin.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {}

                            override fun onAnimationEnd(animation: Animator) {
                                // 分裂动画
                                initialCoin.animate()
                                    .setDuration(100)
                                    .setListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(animation: Animator) {}

                                        override fun onAnimationEnd(animation: Animator) {
                                            windowRootView.removeView(initialCoin)
                                            for (i in 0 until cnt) {
                                                val ivCoin = ImageView(windowRootView.context)
                                                ivCoin.layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                                )
                                                ivCoin.setImageDrawable(_coinDrawable)
                                                ivCoin.x =
                                                    positions[i].x - _coinDrawable.intrinsicWidth / 2f
                                                ivCoin.y =
                                                    positions[i].y - _coinDrawable.intrinsicHeight / 2f
                                                windowRootView.addView(ivCoin)

                                                ivCoin.animate()
                                                    .x(coinPointEnd.x)
                                                    .y(coinPointEnd.y)
                                                    .setInterpolator(AccelerateInterpolator())
                                                    .setDuration(200)
                                                    .setStartDelay(i * 80L)
                                                    .setListener(object :
                                                        Animator.AnimatorListener {
                                                        override fun onAnimationStart(animation: Animator) {
                                                            if (!soundPlayFlag) {
                                                                soundPlayFlag = true
//                                                                if (showCoinSound) {
//                                                                    SoundPoolManager.showSound(R.raw.fly_red)
//                                                                }
                                                            }
                                                        }

                                                        override fun onAnimationEnd(animation: Animator) {
                                                            windowRootView.removeView(ivCoin)
                                                            cnt--
                                                            if (cnt == 0) {
                                                                animationFinish?.invoke()
                                                            }
                                                        }

                                                        override fun onAnimationCancel(animation: Animator) {}

                                                        override fun onAnimationRepeat(animation: Animator) {}
                                                    })
                                                    .start()
                                            }
                                        }

                                        override fun onAnimationCancel(animation: Animator) {}

                                        override fun onAnimationRepeat(animation: Animator) {}
                                    })
                                    .start()
                            }

                            override fun onAnimationCancel(animation: Animator) {}

                            override fun onAnimationRepeat(animation: Animator) {}
                        })
                        .start()
                }
                .start()
        }
    }



    fun showCoinAnim(
        coinPointStart: PointF,
        coinPointEnd: PointF,
        windowRootView: ViewGroup,
        drawableRes: Int = R.drawable.ic_icon,
        animationFinish: (() -> Unit)?
    ) {
        val coinDrawable =
            ResourcesCompat.getDrawable(windowRootView.context.resources, drawableRes, null)
        var cnt = 6
        var soundPlayFlag = false

        coinDrawable?.let { _coinDrawable ->
            // 分裂动画的中心点
            val centerPoint = PointF(coinPointStart.x, coinPointStart.y)

            // 计算6个金币的位置
            val positions = mutableListOf<PointF>()
            for (i in 0 until cnt) {
                val angle = Math.toRadians((i * 60).toDouble())
                val distance = 100f // 金币与中心点的距离
                val x = centerPoint.x + distance * Math.cos(angle).toFloat()
                val y = centerPoint.y + distance * Math.sin(angle).toFloat()
                positions.add(PointF(x, y))
            }

            // 创建初始的金币
            val initialCoin = ImageView(windowRootView.context)
            initialCoin.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            initialCoin.setImageDrawable(_coinDrawable)
            initialCoin.x = centerPoint.x - _coinDrawable.intrinsicWidth / 2f
            initialCoin.y = centerPoint.y - _coinDrawable.intrinsicHeight / 2f
            windowRootView.addView(initialCoin)

            // 增加一个过渡动画，使初始金币先放大再缩小
            initialCoin.animate()
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(200)
                .withEndAction {
                    initialCoin.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {}

                            override fun onAnimationEnd(animation: Animator) {
                                // 分裂动画
                                initialCoin.animate()
                                    .setDuration(500)
                                    .setListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(animation: Animator) {}

                                        override fun onAnimationEnd(animation: Animator) {
                                            windowRootView.removeView(initialCoin)
                                            for (i in 0 until cnt) {
                                                val ivCoin = ImageView(windowRootView.context)
                                                ivCoin.layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                                )
                                                ivCoin.setImageDrawable(_coinDrawable)
                                                ivCoin.x =
                                                    positions[i].x - _coinDrawable.intrinsicWidth / 2f
                                                ivCoin.y =
                                                    positions[i].y - _coinDrawable.intrinsicHeight / 2f
                                                windowRootView.addView(ivCoin)

                                                ivCoin.animate()
                                                    .x(coinPointEnd.x)
                                                    .y(coinPointEnd.y)
                                                    .setInterpolator(AccelerateInterpolator())
                                                    .setDuration(400)
                                                    .setStartDelay(i * 80L)
                                                    .setListener(object :
                                                        Animator.AnimatorListener {
                                                        override fun onAnimationStart(animation: Animator) {
                                                            if (!soundPlayFlag) {
                                                                soundPlayFlag = true
//                                                                if (showCoinSound) {
//                                                                    SoundPoolManager.showSound(R.raw.fly_red)
//                                                                }
                                                            }
                                                        }

                                                        override fun onAnimationEnd(animation: Animator) {
                                                            windowRootView.removeView(ivCoin)
                                                            cnt--
                                                            if (cnt == 0) {
                                                                animationFinish?.invoke()
                                                            }
                                                        }

                                                        override fun onAnimationCancel(animation: Animator) {}

                                                        override fun onAnimationRepeat(animation: Animator) {}
                                                    })
                                                    .start()
                                            }
                                        }

                                        override fun onAnimationCancel(animation: Animator) {}

                                        override fun onAnimationRepeat(animation: Animator) {}
                                    })
                                    .start()
                            }

                            override fun onAnimationCancel(animation: Animator) {}

                            override fun onAnimationRepeat(animation: Animator) {}
                        })
                        .start()
                }
                .start()
        }
    }

}