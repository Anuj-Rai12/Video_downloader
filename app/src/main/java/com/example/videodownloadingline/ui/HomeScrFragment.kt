package com.example.videodownloadingline.ui


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.iconadaptor.HomeSrcAdaptor
import com.example.videodownloadingline.databinding.HomeSrcFragmentBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.utils.changeStatusBarColor
import com.example.videodownloadingline.utils.hide


class HomeScrFragment : Fragment(R.layout.home_src_fragment) {
    private lateinit var binding: HomeSrcFragmentBinding
    private lateinit var homeSrcAdaptor: HomeSrcAdaptor
    private var iconsDialogBox: AddIconsDialogBox? = null
    private var isDialogBoxIsVisible: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeSrcFragmentBinding.bind(view)
        activity?.changeStatusBarColor()
        savedInstanceState?.let {
            isDialogBoxIsVisible = it.getBoolean(getString(R.string.add_to_home_src))
        }
        if (isDialogBoxIsVisible) {
            showDialogBox()
        }

        recycleAdaptor()
        setData()

        binding.srcTv.setOnClickListener {
            it.hide()
            setHasOptionsMenu(true)
            (requireActivity() as MainActivity).changeToolbar()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_frag_menu, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }

        val optionOne = menu.findItem(R.id.new_tab_option)
        optionOne.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener true
        }
        super.onCreateOptionsMenu(menu, inflater)
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


    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(false)
        (requireActivity() as MainActivity).supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_SHOW_TITLE
        (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
        (requireActivity() as MainActivity).supportActionBar!!.title = getString(R.string.app_name)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(getString(R.string.add_to_home_src), isDialogBoxIsVisible)
    }

}