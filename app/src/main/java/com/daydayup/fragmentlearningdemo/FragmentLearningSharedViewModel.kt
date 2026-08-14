package com.daydayup.fragmentlearningdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FragmentLearningSharedViewModel : ViewModel() {

    private val _counter = MutableLiveData(0)
    val counter : LiveData<Int> = _counter

    private val _latestName = MutableLiveData("未命名")
    val latestName : LiveData<String> = _latestName

    private val _screenTitle = MutableLiveData("Fragment Learning")
    val screenTitle : LiveData<String> = _screenTitle

    private val _latestMessage = MutableLiveData("等待 Fragment 发送消息")
    val latestMessage : LiveData<String> = _latestMessage

    fun increaseCounter() {
        _counter.value = _counter.value?.plus(1)
    }

    fun updateName(newName: String) {
        _latestName.value = newName
    }

    fun updateTitle(title: String) {
        _screenTitle.value = title
    }

    fun postMessage(message: String) {
        _latestMessage.value = message
    }
}