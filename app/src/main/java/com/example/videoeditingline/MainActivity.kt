package com.example.videoeditingline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.videoeditingline.databinding.ActivityMainBinding
import com.example.videoeditingline.utils.hideActionBar


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.hideActionBar()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}