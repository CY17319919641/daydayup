package com.daydayup.animation

import android.graphics.Camera
import android.view.animation.Animation
import android.view.animation.Transformation

class AnimationTranslationZ : Animation() {

    private var centerX = 0f
    private var centerY = 0f

    //这个指的不是相机 而是一个3D投影工具
    private var camera = Camera()

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        //获取目标视图的中心点坐标
        centerX = width / 2f
        centerY = height / 2f
    }

    //这个方法会在动画播放的每一帧执行，用于计算当前的变换矩阵
    //interpolatedTime: Float  是插值时间
    //t: Transformation?  是变换对象  指的是一个对象拥有每一帧的变换信息
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        val matrix = t?.matrix
        matrix?.let { _matrix->
            camera.save();
            //中心是绕Y轴旋转  这里可以自行设置X轴 Y轴 Z轴
            camera.rotateY(360 * interpolatedTime);
            //把我们的摄像头加在变换矩阵上
            camera.getMatrix(_matrix);
            //设置翻转中心点
            _matrix.preTranslate(-centerX, -centerY);
            _matrix.postTranslate(centerX, centerY);
            camera.restore()
        }
    }

}