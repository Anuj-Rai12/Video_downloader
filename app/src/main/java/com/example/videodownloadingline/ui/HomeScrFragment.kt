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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.videodownloadingline.MainActivity
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.iconadaptor.HomeSrcAdaptor
import com.example.videodownloadingline.databinding.HomeSrcFragmentBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.HomeSrcFragmentViewModel
import com.example.videodownloadingline.view_model.MainViewModel


class HomeScrFragment(private val isInWebView: Boolean = false) :
    Fragment(R.layout.home_src_fragment) {
    private lateinit var binding: HomeSrcFragmentBinding
    private lateinit var homeSrcAdaptor: HomeSrcAdaptor
    private var iconsDialogBox: AddIconsDialogBox? = null
    private var isDialogBoxIsVisible: Boolean = false
    private val viewModel: HomeSrcFragmentViewModel by viewModels()
    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = MainViewModel.getInstance()
    }

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
            if (!isInWebView)
                setHasOptionsMenu(true)
            currentTab()
        }
    }

    private fun currentTab() {
        mainViewModel?.noOfOpenTab?.observe(viewLifecycleOwner) {
            if (!isInWebView) {
                (requireActivity() as MainActivity).changeToolbar(it!!) { url ->
                    //mainViewModel?.addMoreTab()
                    val action =
                        HomeScrFragmentDirections.actionHomeScrFragmentToWebActivity(
                            url,
                            "Searching.."
                        )
                    findNavController().navigate(action)
                }
            } else {
                (requireActivity() as WebActivity).changeToolbar(it!!, "") { url ->
                    //mainViewModel?.addMoreTab()
                    (requireActivity() as WebActivity).also { activity ->
                        activity.setFragment(
                            WebViewFragments(
                                "Searching..",
                                url
                            )
                        )?.also { size ->
                            WebActivity.viewPager?.currentItem = size - 1
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!isInWebView) {
            inflater.inflate(R.menu.home_frag_menu, menu)
            if (menu is MenuBuilder) {
                menu.setOptionalIconsVisible(true)
            }
            val optionOne = menu.findItem(R.id.new_tab_option)
            optionOne.setOnMenuItemClickListener {
                //mainViewModel?.addMoreTab()
                return@setOnMenuItemClickListener true
            }
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
        if (isInWebView) {
            val size = WebActivity.viewPager?.currentItem
            size?.let {
                (requireActivity() as WebActivity).removeFragment(it)
            }
        }
    }

    private fun recycleAdaptor() {
        binding.homeSrcIcon.apply {
            layoutManager = GridLayoutManager(requireActivity(), 4)
            homeSrcAdaptor = HomeSrcAdaptor { data: HomeSrcIcon, isAddIcon: Boolean ->
                Log.i(TAG, "recycleAdaptor: $isAddIcon with $data")
                if (isAddIcon)
                    showDialogBox()
                else {
                    //mainViewModel?.addMoreTab()
                    if (!isInWebView) {
                        openWebDialogBox(data)
                    } else {
                        (requireActivity() as WebActivity).setFragment(
                            WebViewFragments(
                                data.name!!,
                                data.url!!
                            )
                        )?.also { size ->
                            WebActivity.viewPager?.currentItem = size - 1
                        }
                    }
                }
            }
            adapter = homeSrcAdaptor
        }
    }

    private fun openWebDialogBox(data: HomeSrcIcon) {
        val action =
            HomeScrFragmentDirections.actionHomeScrFragmentToWebActivity(data.url!!, data.name!!)
        findNavController().navigate(action)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setData() {
        viewModel.geBookMarkItem.observe(viewLifecycleOwner) {
            when (it) {
                is RemoteResponse.Error -> {
                    Log.i(TAG, "setData: ${it.exception?.localizedMessage}")
                }
                is RemoteResponse.Loading -> Log.i(TAG, "setData: ${it.data}")
                is RemoteResponse.Success -> {
                    val item = it.data as MutableList<HomeSrcIcon>
                    homeSrcAdaptor.notifyDataSetChanged()
                    homeSrcAdaptor.submitList(item)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(false)
        binding.srcTv.show()
        if (!isInWebView) {
            (requireActivity() as MainActivity).supportActionBar!!.displayOptions =
                ActionBar.DISPLAY_SHOW_TITLE
            (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
            (requireActivity() as MainActivity).supportActionBar!!.title =
                getString(R.string.app_name)
        } else {
            (requireActivity() as WebActivity).supportActionBar!!.displayOptions =
                ActionBar.DISPLAY_SHOW_TITLE
            (requireActivity() as WebActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
            (requireActivity() as WebActivity).supportActionBar!!.title =
                getString(R.string.app_name)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(getString(R.string.add_to_home_src), isDialogBoxIsVisible)
    }

}