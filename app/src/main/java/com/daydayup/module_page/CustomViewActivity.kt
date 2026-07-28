package com.daydayup.module_page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.daydayup.databinding.ActivityCustomViewBinding

class CustomViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.view4.setProgress(50)
    }
}
