package com.example.videodownloadingline.ui


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.iconadaptor.HomeSrcAdaptor
import com.example.videodownloadingline.databinding.HomeSrcFragmentBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.utils.*


class HomeScrFragment : Fragment(R.layout.home_src_fragment) {
    private lateinit var binding: HomeSrcFragmentBinding
    private lateinit var homeSrcAdaptor: HomeSrcAdaptor
    private var iconsDialogBox: AddIconsDialogBox? = null
    private var isDialogBoxIsVisible: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeSrcFragmentBinding.bind(view)
        initial()

        savedInstanceState?.let {
            isDialogBoxIsVisible = it.getBoolean(getString(R.string.add_to_home_src))
        }
        if (isDialogBoxIsVisible) {
            showDialogBox()
        }
        setTab(1)
        recycleAdaptor()
        setData()

        binding.toolBarMainActivity.searchBoxEd.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_ENTER -> {
                            Toast.makeText(
                                activity,
                                "Enter Key Board Clash",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            true
        }


        binding.srcTv.setOnClickListener {
            it.hide()
            binding.toolBarMainActivity.searchBoxEd.show()
        }
    }

    private fun showDialogBox() {
        iconsDialogBox = AddIconsDialogBox()
        iconsDialogBox?.show(context = requireActivity(), listenerForDismiss = {
            iconsDialogBox?.dismiss()
            isDialogBoxIsVisible = false
        }, listenerIcon = {
            Log.i(TAG, "showDialogBox: $it")
            iconsDialogBox?.dismiss()
        })
        isDialogBoxIsVisible = true
    }

    override fun onPause() {
        super.onPause()
        if (isDialogBoxIsVisible) {
            iconsDialogBox?.dismiss()
        }
    }

    private fun recycleAdaptor() {
        binding.homeSrcIcon.apply {
            layoutManager = GridLayoutManager(requireActivity(), 4)
            homeSrcAdaptor = HomeSrcAdaptor { data: HomeSrcIcon, isAddIcon: Boolean ->
                Log.i(TAG, "recycleAdaptor: $isAddIcon with $data")
                if (isAddIcon)
                    showDialogBox()
            }
            adapter = homeSrcAdaptor
        }
    }

    private fun setData() {
        val list = listOf(
            HomeSrcIcon(
                id = 0,
                name = "FaceBook",
                url = "https://www.google.com/"
            ), HomeSrcIcon(
                id = 1,
                name = "Instagram",
                url = "https://www.google.com/"
            ), HomeSrcIcon(
                id = 2,
                name = "WhatsApp",
                url = "https://www.google.com/"
            ), HomeSrcIcon(
                id = 3,
                name = "Twitter",
                url = "https://www.google.com/"
            ), HomeSrcIcon(
                id = 4,
                name = "DailyMotion",
                url = "https://www.google.com/"
            ), HomeSrcIcon(
                id = 5,
                name = "Vimeo",
                url = "https://www.google.com/"
            ), HomeSrcIcon(
                id = 6,
                name = null,
                url = null
            )
        )
        homeSrcAdaptor.submitList(list)
    }

    @SuppressLint("StringFormatMatches")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initial() {
        activity?.hideFullSrc()
        (activity as AppCompatActivity?)?.hideActionBar()
        activity?.changeStatusBarColor()
        binding.toolBarMainActivity.toolbarHomeBtn.setOnClickListener {
            MainActivity.viewPager2?.currentItem = 0
            setTab(MainActivity.viewPager2?.currentItem ?: 0)
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun setTab(value: Int) {
        binding.toolBarMainActivity.totalTabOp.apply {
            text = getString(
                R.string.num_of_tab,
                MainActivity.viewPager2?.currentItem?.plus(value) ?: 10
            )
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(getString(R.string.add_to_home_src), isDialogBoxIsVisible)
    }

}