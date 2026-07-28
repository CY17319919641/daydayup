package com.daydayup.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.daydayup.data.QuizData
import com.daydayup.module_page.QuestionFragment

class QuestionPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val questionLists: List<List<QuizData>>
) : FragmentStateAdapter(fragmentActivity) {
    
    override fun getItemCount(): Int = questionLists.size
    
    override fun createFragment(position: Int): Fragment {
        // 获取对应位置的题目列表，随机选择一道题
        val questions = questionLists[position]
        if (questions.isEmpty()) {
            // 如果列表为空，返回一个空的Fragment
            return QuestionFragment.newInstance(
                QuizData("暂无题目", "", "", "", "", "",0)
            )
        }
        val randomQuestion = questions.random()
        return QuestionFragment.newInstance(randomQuestion)
    }
    
    // 刷新指定位置的Fragment（用于切换Tab时重新随机加载题目）
    fun refreshFragment(position: Int) {
        notifyItemChanged(position)
    }
}

