package com.daydayup.base

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.util.LinkedList

class App: Application() {

    //Todo 这里更推荐使用 ArrayList (动态数组)
    //这里是双向链表
    private var appFragList = LinkedList<BaseFragment<*>>()
    private var start = System.currentTimeMillis()
    //定义静态变量
    companion object {
        lateinit var instance : App
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        //TODO 这里的逻辑等SplashActivity 一起完善

    }
    //判断当前代码是否运行在主进程中
    //如果当前代码运行在主进程中，
    // 那么系统为当前应用分配的进程名（通过 getProcessName(context)获取）应该等于应用的默认包名
    //Todo 这个方法还可以优化成效率更高的版本 sdk28以上的可以直接使用系统方法  也可以增加缓存
    private fun isMainProcess(context: Context) : Boolean{
        return try {
            packageName == getProcessName(context)
        }catch (e: Exception){
            false
        }
    }

    private fun getProcessName(cxt: Context): String?{
        val pid = Process.myPid()
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for(i in runningApps){
            if(i.pid == pid){
                return i.processName
            }
        }
        return null
    }
    //这个方法在SplashACtivity中调用
    fun initAppConnfig(action : ()-> Unit){
       action.invoke()
    }
    fun addFrag(fragment : BaseFragment<*>){
        appFragList.add(fragment)
    }
    fun removeFrag(frag : BaseFragment<*>){
        appFragList.remove(frag)
    }


    //分发返回键事件
    fun DiapatcherOnKeyCodeBack(): Boolean{
        //获取索引范围
        //然后进行反向遍历
        for (i in appFragList.indices.reversed()){
            if(appFragList[i].onKeyCodeBack()){
                return true
            }
        }
        return false
    }

    fun isHomePage() : Boolean{
        return appFragList.size ==1
    }

}