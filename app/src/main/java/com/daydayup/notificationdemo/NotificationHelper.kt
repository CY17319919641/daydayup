package com.daydayup.notificationdemo

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(private val context: Context) {

    private val manager : NotificationManagerCompat = NotificationManagerCompat.from(context)

    fun createChannels(){
        //8.0一下以下不需要渠道
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        //

    }
}