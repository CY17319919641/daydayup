package com.daydayup.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.daydayup.databinding.FragmentBaseBinding
import com.daydayup.module_page.HomeActivity
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import kotlin.reflect.KClass

abstract class BaseFragment<VB : ViewBinding> : Fragment(){

    protected lateinit var binding : VB

    //TODO 如果要使用MMKV 可以在此定义
    //TODO 如果要使用viewModelProvider 可以在此定义

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        App.instance.addFrag(this)
        //TOdo 使用EventBus可以在此注册
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .fullScreen(true)
            .statusBarDarkFont(true)
            .navigationBarDarkIcon(true)
            .transparentBar()
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()
        binding = provideViewBinding(inflater,container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initData()
    }
    abstract fun provideViewBinding(inflater : LayoutInflater,container : ViewGroup?) : VB
    abstract fun initUI ()
    abstract fun initData ()
    abstract fun onKeyCodeBack() : Boolean
    protected fun showToast(msg : String){
        //todo 等待CommonUtils类
        //msg.appToast(context)
    }
    fun showDialogByType(clazz : KClass<*>,params:Any? = null) {
        if(context is HomeActivity){
            (context as HomeActivity).showDialogByType(clazz,params)
        }
    }
    //Todo这里可以写一些 派生类都会用到的动画展示的相关方法
    override fun onDestroy() {
        super.onDestroy()
        App.instance.removeFrag(this)
        //TODO 使用EventBus可以在此注销
    }
    fun removeSelf(exitAnim : Int = -1 ){
        if (context is HomeActivity){
            (context as HomeActivity).removeFullScreenFrag(this::class.java.name,exitAnim)
        }
        //移除列表，判断是否在首页
        App.instance.removeFrag(this)
    }
    fun showFragment(fragment: BaseFragment<*>,enterAnim : Int = -1){
        if (context is HomeActivity){
            (context as HomeActivity).showFullScreenFrag( fragment,enterAnim)
        }
    }
    //判断当前是否有全屏 Fragment 在显示
    fun isExitsFragment(): Boolean {
        if (context is HomeActivity) {
            return (context as HomeActivity).isShowFragment()
        }
        return false
    }

    protected fun <T : AppCompatActivity> startActivityWithIntent(cls : Class<T>){
       val intent = Intent(requireActivity(), cls)
        startActivity( intent)
    }
    protected fun <T : AppCompatActivity> startActivityWithIntent(cls : Class<T>,bundle: Bundle){
        val intent = Intent (requireActivity(),cls)
        intent.putExtras(bundle)
        startActivity(intent)

    }

}