package com.daydayup.ViewPager2_demo

import java.io.Serializable

data class QuizData(
    val question: String,
    val option_a: String? = null,
    val field2: String? = null,  // history.json 使用 field2 作为 option_a
    val option_b: String,
    val option_c: String,
    val option_d: String,
    val id: Int
) : Serializable {
    // 获取选项A，优先使用 option_a，如果没有则使用 field2
    fun getOptionA(): String {
        return option_a ?: field2 ?: ""
    }
}