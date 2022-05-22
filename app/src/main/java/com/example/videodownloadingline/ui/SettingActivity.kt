package com.example.videodownloadingline.ui

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.setting_adaptor.SettingAdaptorAdaptor
import com.example.videodownloadingline.adaptor.setting_adaptor.SettingDataHolder
import com.example.videodownloadingline.databinding.SettingActivityLayoutBinding
import com.example.videodownloadingline.utils.toastMsg

class SettingActivity : AppCompatActivity() {
    private val binding by lazy {
        SettingActivityLayoutBinding.inflate(layoutInflater)
    }
    private val settingOptionArr by lazy {
        resources.getStringArray(R.array.setting_option)
    }
    private val appPrivacyArr by lazy {
        resources.getStringArray(R.array.app_privacy)
    }

    private lateinit var settingAdaptorAdaptor: SettingAdaptorAdaptor
    private lateinit var policyAdaptor: SettingAdaptorAdaptor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setRecycleView1()
        setRecycleView2()
        settingAdaptorAdaptor.submitList(setData(settingOptionArr))
        policyAdaptor.submitList(setData(appPrivacyArr))
    }

    private fun setRecycleView2() {
        binding.otherRecycleView.apply {
            layoutManager = LinearLayoutManager(this@SettingActivity)
            policyAdaptor = SettingAdaptorAdaptor(context = this@SettingActivity) { data ->
                toastMsg(data.toString())
            }
            adapter = policyAdaptor
        }
    }

    private fun setRecycleView1() {
        binding.settingRecycleView.apply {
            layoutManager = LinearLayoutManager(this@SettingActivity)
            settingAdaptorAdaptor = SettingAdaptorAdaptor(context = this@SettingActivity) { data ->
                toastMsg(data.toString())
            }
            adapter = settingAdaptorAdaptor
        }
    }

    private fun setData(settingOptionArr: Array<String>): MutableList<SettingDataHolder> {
        val list = mutableListOf<SettingDataHolder>()
        settingOptionArr.forEach {
            list.add(SettingDataHolder(it, false))
        }
        return list
    }


    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar?.title = "Setting"
    }

}