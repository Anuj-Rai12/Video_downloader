package com.example.videodownloadingline

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdaptor
import com.example.videodownloadingline.databinding.ActivityMainBinding
import com.example.videodownloadingline.ui.DownloadFragment
import com.example.videodownloadingline.ui.HomeScrFragment
import com.example.videodownloadingline.ui.ProgressFragment
import com.example.videodownloadingline.utils.hideActionBar
import com.example.videodownloadingline.utils.show


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.hideActionBar()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adaptor = ViewPagerAdaptor(this)
        adaptor.getTotalFragment.add(HomeScrFragment {
            binding.toolBarMainActivity.searchBoxEd.show()
        })
        adaptor.getTotalFragment.add(ProgressFragment())
        adaptor.getTotalFragment.add(DownloadFragment())
        binding.viewPager.adapter = adaptor
        binding.viewPager.isUserInputEnabled=false




        binding.toolBarMainActivity.totalTabOp.apply {
            text = getString(
                R.string.num_of_tab,
                binding.viewPager.currentItem
            )
        }


        // Testing Code

        binding.toolBarMainActivity.threeBotMnuBtn.setOnClickListener {
            binding.viewPager.currentItem=1
        }
        binding.toolBarMainActivity.toolbarHomeBtn.setOnClickListener {
            binding.viewPager.currentItem=2
        }



    }
}