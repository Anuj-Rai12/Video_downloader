package com.example.videodownloadingline.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.viewpager_adaptor.ViewPagerAdapter
import com.example.videodownloadingline.databinding.DownloadMainFragmentBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.model.downloaditem.TypeOfDownload
import com.example.videodownloadingline.utils.goToNextActivity
import com.google.android.material.tabs.TabLayoutMediator


class MainDownloadFragment : Fragment(R.layout.download_main_fragment) {
    private lateinit var binding: DownloadMainFragmentBinding
    private var viewPagerAdaptor: ViewPagerAdapter? = null
    private var newFolderDialogBox: AddIconsDialogBox? = null
    private val getTabArr by lazy {
        resources.getStringArray(R.array.tab_item)
    }
    private val getStringArray by lazy {
        resources.getStringArray(R.array.sorting_item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadMainFragmentBinding.bind(view)
        setUpAdaptor()
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, pos ->
            tab.text = getTabArr[pos]
        }.attach()
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_SHOW_TITLE
        (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
        (requireActivity() as MainActivity).supportActionBar!!.title =
            getString(R.string.content_description_down)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.download_frag_menu, menu)
        val icSort = menu.findItem(R.id.menu_box)
        icSort.setOnMenuItemClickListener {
            showSortingDialog()
            return@setOnMenuItemClickListener true
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showSortingDialog() {
        if (newFolderDialogBox == null)
            newFolderDialogBox = AddIconsDialogBox()

        newFolderDialogBox?.displaySortingViewRecycle(
            context = requireActivity(),
            getStringArray,
            listenerForNewFolder = {
                //newFolderDialogBox?.dismiss()
            }
        )
    }

    private fun setUpAdaptor() {
        viewPagerAdaptor = ViewPagerAdapter(childFragmentManager, lifecycle)
        viewPagerAdaptor?.setFragment(DownloadFragment(TypeOfDownload.IsFiles.name))
        viewPagerAdaptor?.setFragment(DownloadFragment(TypeOfDownload.IsFolder.name))
        viewPagerAdaptor?.setFragment(DownloadFragment(TypeOfDownload.SecureFolder.name))
        binding.viewPager.adapter = viewPagerAdaptor
    }

    fun goToSetPin() {
        requireActivity().goToNextActivity<SetPinActivity>()
    }

    override fun onPause() {
        super.onPause()
        newFolderDialogBox?.dismiss()
    }

    override fun onDestroyView() {
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}