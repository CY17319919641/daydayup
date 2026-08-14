package com.daydayup.viewpager2demo

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.daydayup.R
import com.daydayup.databinding.ActivityRadioGroupBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class RadioGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRadioGroupBinding
    private lateinit var adapter: QuestionPagerAdapter

    // Tab顺序：历史、文学、科学、地理、艺术
    private val tabTitles = listOf("历史", "文学", "科学", "地理", "艺术")
    private val jsonFiles = listOf(
        R.raw.history,
        R.raw.literature,
        R.raw.science,
        R.raw.geography,
        R.raw.art
    )

    private val questionLists = mutableListOf<List<QuizData>>()
    private val tabViews = mutableListOf<View>()
    private var isInitializing = true  // 标志位，防止初始化时触发刷新
    private var pendingRefreshRunnable: Runnable? = null  // 待执行的刷新任务

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

// 隐藏系统UI
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        val configuration = Configuration(resources.configuration)
        configuration.fontScale = 1.0f
        resources.updateConfiguration(configuration, resources.displayMetrics)
        binding = ActivityRadioGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        loadQuestions()
        setupViewPager()
        setupClickListeners()
    }

    private fun initViews() {
        // 初始化返回按钮
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadQuestions() {
        val gson = Gson()
        val type = object : TypeToken<List<QuizData>>() {}.type

        jsonFiles.forEach { resourceId ->
            try {
                val inputStream = resources.openRawResource(resourceId)
                val reader = InputStreamReader(inputStream, "UTF-8")
                val questions: List<QuizData> = gson.fromJson(reader, type)
                reader.close()
                inputStream.close()
                questionLists.add(questions)
            } catch (e: Exception) {
                e.printStackTrace()
                questionLists.add(emptyList())
            }
        }
    }

    private fun setupViewPager() {
        adapter = QuestionPagerAdapter(this, questionLists)
        binding.optionViewPager.adapter = adapter

        // 创建自定义Tab视图
        createCustomTabs()

        // 连接TabLayout和ViewPager2
        TabLayoutMediator(binding.subjectTab, binding.optionViewPager) { tab, position ->
            // 使用自定义视图
            tab.customView = tabViews[position]
        }.attach()

        // 监听ViewPager2的页面变化来更新题目
        binding.optionViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (!isInitializing) {
                    updateQuestionText(position)
                }
            }
        })

        // 监听Tab切换，每次切换时重新随机加载题目
        binding.subjectTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    // 更新选中状态样式
                    updateTabStyle(it.position, true)

                    // 如果不是初始化阶段，才刷新Fragment
                    if (!isInitializing) {
                        // 使用延迟执行，直接更新Fragment内容，避免触发Adapter刷新
                        binding.optionViewPager.post {
                            refreshCurrentFragment(it.position)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    // 更新未选中状态样式
                    updateTabStyle(it.position, false)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 重新点击同一个Tab时，也可以重新随机加载题目
                tab?.let {
                    binding.optionViewPager.post {
                        refreshCurrentFragment(it.position)
                    }
                }
            }
        })

        // 设置初始选中状态
        if (tabViews.isNotEmpty()) {
            updateTabStyle(0, true)
            updateQuestionText(0)
        }

        // 初始化完成，允许后续的刷新操作
        binding.optionViewPager.postDelayed({
            isInitializing = false
        }, 500)
    }

    private fun createCustomTabs() {
        tabTitles.forEach { title ->
            val tabView = LayoutInflater.from(this).inflate(R.layout.item_subject, null)
            val textView = tabView.findViewById<TextView>(R.id.tv_singer)
            textView.text = title
            tabViews.add(tabView)
        }
    }

    private fun updateTabStyle(position: Int, isSelected: Boolean) {
        if (position < tabViews.size) {
            val tabView = tabViews[position]
            val textView = tabView.findViewById<TextView>(R.id.tv_singer)

            if (isSelected) {
                // 选中状态
                textView.setTextColor(Color.parseColor("#B0704A"))
                textView.setBackgroundResource(R.drawable.bg_subject2)
            } else {
                // 未选中状态
                textView.setTextColor(Color.parseColor("#007F4A"))
                textView.setBackgroundResource(R.drawable.bg_subject1)
            }
        }
    }

    private fun updateQuestionText(position: Int) {
        // 延迟更新，确保Fragment已经创建
        binding.optionViewPager.postDelayed({
            try {
                // 从ViewPager2的Adapter获取当前题目数据
                if (position < questionLists.size && questionLists[position].isNotEmpty()) {
                    // 这里我们直接使用题目列表，因为Fragment会随机选择
                    // 如果需要显示当前Fragment的题目，需要通过其他方式获取
                    // 暂时先不更新，等Fragment创建后再通过回调更新
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 100)
    }

    // 供Fragment调用的方法，用于更新题目显示
    fun updateQuestionDisplay(question: String) {
        binding.question.text = question.replace("\\n", "\n")
    }

    // 处理答题结果
    fun onAnswerResult(isCorrect: Boolean) {

        // 取消之前的延迟任务（如果有）
        pendingRefreshRunnable?.let {
            binding.optionViewPager.removeCallbacks(it)
        }

        // 答题后延迟自动切换到下一题
        pendingRefreshRunnable = Runnable {
            val currentPosition = binding.subjectTab.selectedTabPosition
            refreshCurrentFragment(currentPosition)
            pendingRefreshRunnable = null
        }
        binding.optionViewPager.postDelayed(pendingRefreshRunnable!!, 300)
    }

    // 刷新当前Fragment的题目（直接更新Fragment内容，避免触发Adapter刷新）
    private fun refreshCurrentFragment(position: Int) {
        if (position < questionLists.size && questionLists[position].isNotEmpty()) {
            val questions = questionLists[position]
            val randomQuestion = questions.random()

            // 尝试获取当前Fragment并更新
            try {
                // 通过ViewPager2的当前Item ID来查找Fragment
                val fragmentTag = "f${adapter.getItemId(position)}"
                val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
                        as? QuestionFragment

                // 如果Fragment存在且是当前显示的Fragment，直接更新
                if (fragment != null && fragment.isAdded && fragment.isVisible) {
                    fragment.updateQuestion(randomQuestion)
                } else {
                    // 如果Fragment不存在或不可见，等待ViewPager2切换完成后再更新
                    binding.optionViewPager.postDelayed({
                        val delayedFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
                                as? QuestionFragment
                        delayedFragment?.updateQuestion(randomQuestion)
                    }, 200)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 如果出错，等待一下再尝试更新
                binding.optionViewPager.postDelayed({
                    try {
                        val fragmentTag = "f${adapter.getItemId(position)}"
                        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
                                as? QuestionFragment
                        fragment?.updateQuestion(randomQuestion)
                    } catch (e2: Exception) {
                        e2.printStackTrace()
                    }
                }, 300)
            }
        }
    }

    private fun setupClickListeners() {
        // "换一个"按钮点击事件
        binding.btnChange.setOnClickListener {
            // 取消待执行的自动刷新任务
            pendingRefreshRunnable?.let {
                binding.optionViewPager.removeCallbacks(it)
                pendingRefreshRunnable = null
            }

            // 重新加载当前Tab的题目
            val currentPosition = binding.subjectTab.selectedTabPosition
            refreshCurrentFragment(currentPosition)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 清理待执行的任务
        pendingRefreshRunnable?.let {
            binding.optionViewPager.removeCallbacks(it)
            pendingRefreshRunnable = null
        }
    }
}