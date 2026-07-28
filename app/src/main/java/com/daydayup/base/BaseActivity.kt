package com.daydayup.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.daydayup.R
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.toast.ToastParams
import com.hjq.toast.Toaster
import com.hjq.toast.style.CustomToastStyle


//这个类应该是抽象类
//抽象类的要素是
//
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {


    //TODO如果要使用MMKV 可以在此定义


    //TODO 如果要使用viewModelProvider 可以在此定义


    protected val binding : VB by lazy{
        provideViewBinding()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //禁止投屏 录屏 截屏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        immersionBar {
            fullScreen(true)// 1. 全屏模式
            statusBarDarkFont(true)// 2. 状态栏深色字体
            navigationBarDarkIcon(true)// 3. 导航栏深色图标
            hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)// 4. 隐藏导航栏

        }
        //TOdo 如果要使用eventbus 可以在此定义 和注册
        initUI()
        initData()


    }
    abstract fun provideViewBinding() : VB
    abstract fun initUI()
    abstract fun initData()

    //三种跳转函数
    //因为使用了泛型参数cls: Class<T>
    //所以要有泛型参数说明来约束这个类是ACtivity
    // 说明类的上界<T : AppCompatActivity>  该类必须为AppCompatActivity的子类

    //无数据 有动画
    protected fun <T : AppCompatActivity> startActivityWithIntent(cls : Class<T>){
        val intent = Intent(this, cls)
        startActivity(intent)
    }
    //
    protected fun <T : AppCompatActivity> startActivityWithIntentNoAnim(cls : Class<T>){
        val intent = Intent(this, cls)
        startActivity(intent)
        overridePendingTransition(0,0)
    }
    protected fun <T : AppCompatActivity> startActivityWithIntent(cls : Class<T>,bundle: Bundle){
        val intent = Intent(this, cls).apply {
            putExtras(bundle)
        }
        startActivity(intent)
    }

    //d点击飞编辑区收起键盘

    @CallSuper
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if(ev.action == MotionEvent.ACTION_DOWN){
            val view = currentFocus
//            if(KeyboarUtils.isShouldHideKeyboard(view,ev)){
//                if(view != null){
//                    keyboardUtils.hideKeyboard(view)
//                }
//            }
        }
        return super.dispatchTouchEvent(ev)
    }
    protected fun showToast(msg:String){
        val params = ToastParams()
        params.text = msg
        params.style = CustomToastStyle(R.layout.toast_success)
        Toaster.show(params)
    }
    override fun onDestroy(){
        super.onDestroy()
        //TODO 如果要使用eventbus 在这里进行注销
    }
    //设置字体不被影响
    override fun attachBaseContext(newBase: Context?) {
        val configuration = newBase?.resources?.configuration
        if (configuration != null) {
            configuration.fontScale = 1.0f
        }
        val context = configuration?.let { newBase.createConfigurationContext(it) }
        super.attachBaseContext(newBase)
    }

}