package com.example.videoeditingline.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.videoeditingline.MainActivity
import com.example.videoeditingline.databinding.SplashScreenActivityBinding
import com.example.videoeditingline.utils.hideActionBar
import com.example.videoeditingline.utils.showFullSrc
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashSrcActivity : AppCompatActivity() {
    private lateinit var binding: SplashScreenActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashScreenActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initial()
        lifecycleScope.launch {
            delay(3000)
            val intent = Intent(this@SplashSrcActivity, MainActivity::class.java)
            startActivity(intent)
            this.cancel()
        }

    }

    private fun initial() {
        this.hideActionBar()
        this.showFullSrc()
    }
}