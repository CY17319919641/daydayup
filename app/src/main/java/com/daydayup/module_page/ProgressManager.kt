package com.daydayup.module_page

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.also

class ProgressManager(context: Context){
    private val sharedPref : SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object{
        private const val PREFS_NAME = "AppPreferences"
        //注解用于保证每个线程拿到的都是最新值
        @Volatile private var instance : ProgressManager ? = null

        fun getInstance(context: Context): ProgressManager =
            //函数意思是加锁 防止多线程使用同一段代码
            instance ?: synchronized(this){
                instance ?: ProgressManager(context.applicationContext).also { instance = it }
            }

        private const val PRIVACY_AGREED = "privacyAgreed"
        private const val DICE_RECORDS = "diceRecords"
        private const val TURNTABLE_RECORDS = "turntableRecords"
        
        // 统计数据相关常量
        private const val TOTAL_CORRECT = "totalCorrect"
        private const val TOTAL_WRONG = "totalWrong"
        private const val CURRENT_STREAK = "currentStreak"
        private const val HISTORY_BEST_STREAK = "historyBestStreak"
        private const val TODAY_BEST_STREAK = "todayBestStreak"
        private const val TODAY_STREAK = "todayStreak"
        private const val LAST_DATE = "lastDate"
    }
    
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())



    fun savePrivacyAgreed(context: Context, agreed: Boolean) {
        context.getSharedPreferences(PRIVACY_AGREED, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(PRIVACY_AGREED, agreed)
            .apply()
    }

    fun isPrivacyAgreed(context: Context): Boolean {
        return context.getSharedPreferences(PRIVACY_AGREED, Context.MODE_PRIVATE)
            .getBoolean(PRIVACY_AGREED, false)
    }


    

    

    

    
    /**
     * 记录答题结果
     * @param isCorrect 是否答对
     */
    fun recordAnswer(isCorrect: Boolean) {
        val today = dateFormat.format(Date())
        val lastDate = sharedPref.getString(LAST_DATE, "")
        
        // 如果是新的一天，重置今日连对数
        if (lastDate != today) {
            sharedPref.edit()
                .putString(LAST_DATE, today)
                .putInt(TODAY_STREAK, 0)
                .apply()
        }
        
        val currentStreak = sharedPref.getInt(CURRENT_STREAK, 0)
        val historyBestStreak = sharedPref.getInt(HISTORY_BEST_STREAK, 0)
        val todayStreak = sharedPref.getInt(TODAY_STREAK, 0)
        val totalCorrect = sharedPref.getInt(TOTAL_CORRECT, 0)
        val totalWrong = sharedPref.getInt(TOTAL_WRONG, 0)
        
        if (isCorrect) {
            // 答对了
            val newStreak = currentStreak + 1
            val newTodayStreak = todayStreak + 1
            
            sharedPref.edit()
                .putInt(TOTAL_CORRECT, totalCorrect + 1)
                .putInt(CURRENT_STREAK, newStreak)
                .putInt(TODAY_STREAK, newTodayStreak)
                .apply()
            
            // 更新历史最佳连对数
            if (newStreak > historyBestStreak) {
                sharedPref.edit()
                    .putInt(HISTORY_BEST_STREAK, newStreak)
                    .apply()
            }
            
            // 更新今日最佳连对数
            val todayBestStreak = sharedPref.getInt(TODAY_BEST_STREAK, 0)
            if (newTodayStreak > todayBestStreak) {
                sharedPref.edit()
                    .putInt(TODAY_BEST_STREAK, newTodayStreak)
                    .apply()
            }
        } else {
            // 答错了，重置连对数
            sharedPref.edit()
                .putInt(TOTAL_WRONG, totalWrong + 1)
                .putInt(CURRENT_STREAK, 0)
                .putInt(TODAY_STREAK, 0)
                .apply()
        }
    }
    
    /**
     * 获取累计答对数
     */
    fun getTotalCorrect(): Int {
        return sharedPref.getInt(TOTAL_CORRECT, 0)
    }
    
    /**
     * 获取累计答错数
     */
    fun getTotalWrong(): Int {
        return sharedPref.getInt(TOTAL_WRONG, 0)
    }
    
    /**
     * 获取正确率（百分比）
     */
    fun getAccuracy(): Int {
        val correct = getTotalCorrect()
        val wrong = getTotalWrong()
        val total = correct + wrong
        return if (total > 0) {
            (correct * 100 / total)
        } else {
            0
        }
    }
    
    /**
     * 获取历史最佳连对数
     */
    fun getHistoryBestStreak(): Int {
        return sharedPref.getInt(HISTORY_BEST_STREAK, 0)
    }
    
    /**
     * 获取今日最佳连对数
     */
    fun getTodayBestStreak(): Int {
        val today = dateFormat.format(Date())
        val lastDate = sharedPref.getString(LAST_DATE, "")
        
        // 如果是新的一天，返回0
        if (lastDate != today) {
            return 0
        }
        
        return sharedPref.getInt(TODAY_BEST_STREAK, 0)
    }
    
    /**
     * 获取连对率（当前连对数）
     */
    fun getStreakAccuracy(): Int {
        return sharedPref.getInt(CURRENT_STREAK, 0)
    }
    
    /**
     * 重置所有统计数据
     */
    fun resetStatistics() {
        sharedPref.edit()
            .putInt(TOTAL_CORRECT, 0)
            .putInt(TOTAL_WRONG, 0)
            .putInt(CURRENT_STREAK, 0)
            .putInt(HISTORY_BEST_STREAK, 0)
            .putInt(TODAY_BEST_STREAK, 0)
            .putInt(TODAY_STREAK, 0)
            .remove(LAST_DATE)
            .apply()
    }
    
    /**
     * 检查徽章1是否点亮（答对10题）
     */
    fun isBadge1Unlocked(): Boolean {
        return getTotalCorrect() >= 10
    }
    
    /**
     * 检查徽章2是否点亮（答对50题）
     */
    fun isBadge2Unlocked(): Boolean {
        return getTotalCorrect() >= 50
    }
    
    /**
     * 检查徽章3是否点亮（答对100题）
     */
    fun isBadge3Unlocked(): Boolean {
        return getTotalCorrect() >= 100
    }
    
    /**
     * 检查徽章4是否点亮（答对200题）
     */
    fun isBadge4Unlocked(): Boolean {
        return getTotalCorrect() >= 200
    }










}