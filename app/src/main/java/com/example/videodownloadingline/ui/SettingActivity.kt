package com.example.videodownloadingline.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.setting_adaptor.SettingAdaptorAdaptor
import com.example.videodownloadingline.adaptor.setting_adaptor.SettingDataHolder
import com.example.videodownloadingline.databinding.SettingActivityLayoutBinding
import com.example.videodownloadingline.utils.*
import java.io.File

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
                toastMsg(data.title)
            }
            adapter = policyAdaptor
        }
    }

    private fun setRecycleView1() {
        binding.settingRecycleView.apply {
            layoutManager = LinearLayoutManager(this@SettingActivity)
            settingAdaptorAdaptor = SettingAdaptorAdaptor(context = this@SettingActivity) { data ->
                checkData(data)
            }
            adapter = settingAdaptorAdaptor
        }
    }

    private fun checkData(data: SettingDataHolder) {
        when (settingOptionArr.indexOf(data.title)) {
            0 -> {//Download Location
                showDialogBox(
                    title = data.title,
                    desc = finPath("/Download/VideoDownload"),
                    flag = true
                ) {}
            }
            1 -> {}//Save Wi-fi
            2 -> {}//Save Password
            3 -> {//Save Engine Option
                toastMsg("Currently Google searching available")
            }
            4 -> {//Clear cache
                deleteCache().apply {
                    if (this) {
                        binding.root.showSandbar("Cache Memory is Deleted", color = Color.GREEN)
                    } else {
                        binding.root.showSandbar("Cannot delete Cache Memory", color = Color.RED)
                    }
                }
            }
            5 -> {//Clr browser history
                toastMsg("Browser history is Clear")
            }
        }
    }

    private fun setData(settingOptionArr: Array<String>): MutableList<SettingDataHolder> {
        val list = mutableListOf<SettingDataHolder>()
        settingOptionArr.forEach {
            list.add(SettingDataHolder(it, false))
        }
        return list
    }

    private fun deleteCache(): Boolean {
        return try {
            val dir: File = cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            Log.i(TAG, "deleteCache: ${e.localizedMessage}")
            false
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list() as Array<String>
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }


    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar?.title = "Setting"
    }

}