package com.example.videodownloadingline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.videodownloadingline.databinding.ActivityMainBinding
import com.example.videodownloadingline.utils.hideActionBar


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.hideActionBar()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}