package com.daydayup.animation_demo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.PointF
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.CycleInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

fun View.slideInFRomTopAndOut(){
    //这里的起点是以被动画的 View 自身左上角为原点
    val slidInAnimation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 0f,//x轴起点
        Animation.RELATIVE_TO_SELF, 0f,//x轴终点
        Animation.RELATIVE_TO_SELF, -2f,//y轴起点
        Animation.RELATIVE_TO_SELF, 0f//y轴终点
    )
    slidInAnimation.duration = 1000
    //保持视图停留在动画播放结束的地方
    slidInAnimation.fillAfter = true
    startAnimation(slidInAnimation)


    //View部分的延迟方法  {}部分的内容在主线程中执行
    // 这里其实执行的是view.postDelayed()
    postDelayed({
        val slidOutAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,//x轴起点
            Animation.RELATIVE_TO_SELF, 0f,//x轴终点
            Animation.RELATIVE_TO_SELF, 0f,//y轴起点
            Animation.RELATIVE_TO_SELF, 1f//y轴终点
        )
        slidOutAnimation.duration = 500
        slidOutAnimation.fillAfter = false
        startAnimation(slidOutAnimation)
    }, 1000)

}


fun View.shakeLeftToRight(){
    //这里的起点是以被动画的 View 自身左上角为原点
    //但是单位变成像素
    val shakeAnimation = TranslateAnimation(0f,25f,0f,0f)
    shakeAnimation.duration = 500
    //三次 一正一反循环播放动画
    shakeAnimation.interpolator = CycleInterpolator(3f)
    startAnimation(shakeAnimation)

}

fun View.fadeInThenOut(onAnimationEnd:()-> Unit ={}){
    val fadeInAnimation = AlphaAnimation(0f,1f)
    val fadeOutAnimation = AlphaAnimation(1f,0f)
    fadeInAnimation.duration = 1500
    fadeInAnimation.fillAfter = true
    startAnimation(fadeInAnimation)

    postDelayed( {
        fadeOutAnimation.duration = 1500
        fadeOutAnimation.fillAfter = false
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationEnd(animation: Animation?) {
                onAnimationEnd()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {

            }

        })
        startAnimation(fadeOutAnimation)
    },1000)
}
fun View.rotateClockwise(){
    val rotateAnimation = RotateAnimation(
        0f,
        360f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )
    rotateAnimation.duration = 500
    rotateAnimation.fillAfter = true
    rotateAnimation.repeatCount = 5
    rotateAnimation.repeatMode = Animation.RESTART
    rotateAnimation.interpolator = LinearInterpolator ()
    startAnimation(rotateAnimation)
}
fun View.slightFloat(){
    val slightFloatAnimation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF,
        0f,
        Animation.RELATIVE_TO_SELF,
        0f,
        Animation.RELATIVE_TO_SELF,
        0f,
        Animation.RELATIVE_TO_SELF,
        0.1f
    )
    slightFloatAnimation.duration = 1000
    slightFloatAnimation.fillAfter = false
    slightFloatAnimation.repeatMode = Animation.REVERSE
    slightFloatAnimation.interpolator = AccelerateDecelerateInterpolator()
    slightFloatAnimation.repeatCount = 10
    startAnimation(slightFloatAnimation)
}

fun View.bounce(){
    val bounceAnimation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF,
        0f,
        Animation.RELATIVE_TO_SELF,
        0f,
        Animation.RELATIVE_TO_SELF,
        0f,
        Animation.RELATIVE_TO_SELF,
        1f
    )
    bounceAnimation.duration = 1000
    bounceAnimation.fillAfter = false
    bounceAnimation.repeatMode = Animation.RESTART
    bounceAnimation.interpolator = BounceInterpolator()
    bounceAnimation.repeatCount = 1
    startAnimation(bounceAnimation)
}

fun View.slideInFromLeftAndOut(){
    //这里的起点是以被动画的 View 自身左上角为原点
    val slidInAnimation = TranslateAnimation(
        Animation.RELATIVE_TO_SELF, -2f,//x轴起点
        Animation.RELATIVE_TO_SELF, 0f,//x轴终点
        Animation.RELATIVE_TO_SELF, 0f,//y轴起点
        Animation.RELATIVE_TO_SELF, 0f//y轴终点
    )
    slidInAnimation.duration = 1000
    slidInAnimation.fillAfter = true
    startAnimation(slidInAnimation)
    postDelayed({
        val slidOutAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,//x轴起点
            Animation.RELATIVE_TO_SELF, 2f,//x轴终点
            Animation.RELATIVE_TO_SELF, 0f,//y轴起点
            Animation.RELATIVE_TO_SELF, 0f//y轴终点
        )
        slidOutAnimation.duration = 1000
        slidOutAnimation.fillAfter = false
        startAnimation(slidOutAnimation)
    },1500)
}

//z轴是朝屏幕外的
fun View.shrinkAndDisappear(){
    val translationZ = ObjectAnimator.ofFloat(this, View.TRANSLATION_Z, 10f, 1f)
    var scaleX1 = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.5f)
    var scaleY1 = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.5f)
    val alpha1 = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f)
    val animSet = AnimatorSet()
    animSet.play(translationZ).with(scaleX1).with(scaleY1).with(alpha1)
    animSet.interpolator = AccelerateDecelerateInterpolator()
    animSet.duration = 1000
    animSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            scaleX = 1f
            scaleY = 1f;
            alpha = 1f
        }
    })
    animSet.start()

}

fun View.shrinkAndDisappear1() {
    animate()
        .scaleX(0.5f).scaleY(0.5f)
        .alpha(0f)
        .translationZ(1f)
        .setDuration(1000)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .withEndAction {
            // 需要可见就恢复属性；需要隐藏就用 GONE
            scaleX = 1f; scaleY = 1f; alpha = 1f
            visibility = View.VISIBLE // 或 GONE
        }
        .start()
}
fun View.growAndAppear(){

        val translationZ = ObjectAnimator.ofFloat(this, View.TRANSLATION_Z, 1f,10f)
        val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0.5f,1.0f)
        val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0.5f,1.0f)
        val alpha1 = ObjectAnimator.ofFloat(this, "alpha", 0.0f,1.0f)
        val animSet = AnimatorSet()
        animSet.play(translationZ).with(scaleX).with(scaleY).with(alpha1)
        animSet.interpolator = AccelerateDecelerateInterpolator()
        animSet.duration = 1000
        animSet.start()

}

//视图获取焦点时用于强调
//Z轴压入式缩放淡入
fun View.animationScaleDownFromZAxis() {
    val tran = ObjectAnimator.ofFloat(this, "translationZ", 300f, 1.0f)
    val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1.5f, 1.0f)
    val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1.5f, 1.0f)
    val alpha1 = ObjectAnimator.ofFloat(this, "alpha", 0.5f, 1.0f)
    val animSet = AnimatorSet()
    animSet.play(tran).with(scaleX).with(scaleY).with(alpha1)
    animSet.duration = 300
    animSet.interpolator = AccelerateDecelerateInterpolator()
    animSet.start()
}

fun View.breath(){
    val breathAnimation = ScaleAnimation(
        1f,1.1f,
        1f,1.1f,
        Animation.RELATIVE_TO_SELF,0.5f,
        Animation.RELATIVE_TO_SELF,0.5f
    )
    breathAnimation.duration = 500
    breathAnimation.repeatCount = 5
    breathAnimation.repeatMode = Animation.REVERSE
    startAnimation(breathAnimation)
}
fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    clearAnimation()
    this.visibility = View.GONE
}

fun View.hide() {
    clearAnimation()
    this.visibility = View.INVISIBLE
}
//为什么要专门写一个方法来控制动画播放速度
//因为动画速度通常受系统设置影响，如果用户在开发者选项里设置了动画速度，
// 那么动画就会变快或者变慢，或者停止
//为了保证关键动画的加载不受系统设置的影响，就需要调用setDurationScale
//但 setDurationScale 是隐藏 API，不能直接调用，所以用反射来调用。
fun ValueAnimator?.resetDurationScale() {
    try {
        if (this != null) {
            val setAnimationScale: Method =
                ValueAnimator::class.java.getMethod(
                    "setDurationScale",
                    Float::class.javaPrimitiveType
                )
            setAnimationScale.invoke(this, 1)
        }
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun digitalSmoothChange(targetTextView : TextView, start : Float, end : Float){

    val animator = ValueAnimator.ofFloat(start,end)
    animator.duration = 1000
    //给 ValueAnimator 添加一个“更新监听器”，在动画每一帧刷新时被调用
    animator.addUpdateListener {animation ->

        val value = animation.animatedValue as Float
        targetTextView.text = String.format("%.2f",value)
    }
    animator.resetDurationScale()
    animator.start()

}

fun setLottieAnimationWithFrameRange(
    lottieView : LottieAnimationView,
    startFrame : Int,
    endFrame : Int,
    onFirstEnd : (() -> Unit)? = null
){
    lottieView.cancelAnimation()
    lottieView.progress = 0f
    lottieView.repeatCount = 0

    val animatorListener = object : AnimatorListenerAdapter(){
        override fun onAnimationEnd(animation: Animator) {
           onFirstEnd?.invoke()
            lottieView.repeatCount = LottieDrawable.INFINITE
            lottieView.setMinAndMaxFrame(startFrame,endFrame)
            lottieView.playAnimation()
        }
    }

    lottieView.addAnimatorListener(animatorListener)
    lottieView.addLottieOnCompositionLoadedListener {
        lottieView.setMinAndMaxFrame(0,100)
        lottieView.playAnimation()
    }


}

fun View.rotateShake(){
    val rotateShakeAnimation = RotateAnimation(
        -12f,
        12f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )
    rotateShakeAnimation.duration = 100
    rotateShakeAnimation.repeatCount = 10
    rotateShakeAnimation.repeatMode = Animation.REVERSE
    rotateShakeAnimation.fillAfter = false
    startAnimation(rotateShakeAnimation)
}

//用自定义动画类 实现3D 旋转
fun View.rotateByY(block: () -> Unit) {
    val animation = AnimationTranslationZ()
    animation.duration = 800L
    animation.interpolator = AccelerateInterpolator()
    animation.setAnimationListener(object :
        Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            block.invoke()
        }

        override fun onAnimationRepeat(animation: Animation?) {

        }

    })
    startAnimation(animation)
}

fun View.getViewCenterLocationInScreen(): PointF {
    val position = IntArray(2)
    this.getLocationOnScreen(position)
    return PointF(position[0] + this.width / 2f, position[1] + this.height / 3.5f)
}


