package com.example.videodownloadingline.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.SetLockSrcFragmentLayoutBinding
import com.example.videodownloadingline.model.downloaditem.Category
import com.example.videodownloadingline.model.downloaditem.DownloadItems
import com.example.videodownloadingline.model.securefolder.SecureFolderItem
import com.example.videodownloadingline.utils.toastMsg
import com.example.videodownloadingline.view_model.MainViewModel

class SetPinActivity : AppCompatActivity() {
    private val binding by lazy {
        SetLockSrcFragmentLayoutBinding.inflate(layoutInflater)
    }

    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = MainViewModel.getInstance()
        val extras = intent.extras

        if (extras != null) {
            val name = extras.getString(getString(R.string.set_pin_cat))
            if (name != null) {
                when (Category.valueOf(name)) {
                    Category.PinFolder -> {//Set Pin for Secured Folder
                        val valueList =
                            extras.getParcelableArrayList<SecureFolderItem>(getString(R.string.set_pin_txt))
                        Log.i("SecureFolder", "onCreate: $valueList")
                        toastMsg("$valueList")
                    }
                    Category.NormalFolder -> {//Set Pin of Normal File
                        val valueList =
                            extras.getParcelableArrayList<DownloadItems>(getString(R.string.set_pin_txt))
                        Log.i("NormalFolder", "onCreate: $valueList")
                    }
                }
            }
        }
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}