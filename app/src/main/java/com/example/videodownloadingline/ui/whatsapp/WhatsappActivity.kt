package com.example.videodownloadingline.ui.whatsapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdapter
import com.example.videodownloadingline.databinding.ActivityWhatsappBinding
import com.google.android.material.tabs.TabLayoutMediator

class WhatsappActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityWhatsappBinding.inflate(layoutInflater)
    }

    private var viewPagerAdaptor: ViewPagerAdapter? = null

    private val whatsAppTab by lazy {
        resources.getStringArray(R.array.whatsapp_tab_item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpAdaptor()
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, pos ->
            tab.text = whatsAppTab[pos]
        }.attach()
    }

    private fun setUpAdaptor() {
        viewPagerAdaptor = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPagerAdaptor?.setFragment(WhatsAppFragment(WhatsappClick.IsImage.name))
        viewPagerAdaptor?.setFragment(WhatsAppFragment(WhatsappClick.IsVideo.name))
        binding.viewPager.adapter = viewPagerAdaptor
    }


    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.whatsapp_name)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        enum class WhatsappClick {
            IsImage,
            IsVideo
        }
    }

}