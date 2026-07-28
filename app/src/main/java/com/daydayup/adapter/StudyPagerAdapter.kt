package com.daydayup.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.daydayup.module_page.QuizPageFragment

class StudyPagerAdapter(
    activity : FragmentActivity,
    private val titles: List<String>

) : FragmentStateAdapter(activity){
    override fun getItemCount(): Int {
        return titles.size
    }
    //这里可以根据position 创建不同的Fragment
    override fun createFragment(position: Int): Fragment {
       return QuizPageFragment.newInstance("Fragment 页：${titles[position]}")
    }
    fun getTitle(position: Int): String = titles[position]
}