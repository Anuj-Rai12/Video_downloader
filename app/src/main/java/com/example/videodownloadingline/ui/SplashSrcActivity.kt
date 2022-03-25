package com.example.videodownloadingline.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.databinding.SplashScreenActivityBinding
import com.example.videodownloadingline.utils.hideActionBar
import com.example.videodownloadingline.utils.showFullSrc
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
            finish()
        }

    }

    private fun initial() {
        this.hideActionBar()
        this.showFullSrc()
    }
}