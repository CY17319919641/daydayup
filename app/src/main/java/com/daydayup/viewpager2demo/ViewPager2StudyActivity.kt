package com.daydayup.viewpager2demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daydayup.databinding.ActivityViewPager2StudyBinding
import com.google.android.material.tabs.TabLayoutMediator

class ViewPager2StudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPager2StudyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPager2StudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val subjects = listOf(
            "item",
            "item1",
            "item2",
            "item3",
            "item4",
        )
        binding.viewPagerFirst.adapter = BasicPagerAdapter(subjects)
        binding.viewPagerSecond.adapter = BasicPagerAdapter(subjects)

        binding.viewPagerThird.adapter = StudyPagerAdapter(this, subjects)
        binding.viewPagerFourth.adapter = StudyPagerAdapter(this, subjects)

        val pagerAdapter = StudyPagerAdapter(this, subjects)
        binding.viewPagerFifth.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPagerFifth) { tab, position ->
            tab.text = pagerAdapter.getTitle(position)
        }.attach()

        binding.radioGroup.setOnClickListener{
            val intent = Intent(this, RadioGroupActivity::class.java)
            startActivity(intent)
        }

    }
}