package com.daydayup.fragment_learning_demo.contract

interface FragmentLearningHost {

    fun openDetailByReplace(itemId: Int, itemName: String)
    fun openToolsByAddHide()
    fun openEditName(defaultName: String)
    fun updateTitleFromFragment(title: String)
    fun showTipsDialog()
    fun sendMessageToActivity(message: String)

}