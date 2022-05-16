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
import com.example.videodownloadingline.ui.whatsapp.WhatsappActivity
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.HomeSrcFragmentViewModel
import com.example.videodownloadingline.view_model.MainViewModel


class HomeScrFragment(private val isInWebView: Boolean = false) :
    Fragment(R.layout.home_src_fragment) {
    private lateinit var binding: HomeSrcFragmentBinding
    private lateinit var homeSrcAdaptor: HomeSrcAdaptor
    private var iconsDialogBox: AddIconsDialogBox? = null
    private var isDialogBoxIsVisible: Boolean = false
    private var isNewTab: Boolean = false
    private val viewModel: HomeSrcFragmentViewModel by viewModels()
    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = MainViewModel.getInstance()
    }

    @RequiresApi(Build.VERSION_CODES.N)
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
            currentTab()
        }
    }

    private fun currentTab() {
        mainViewModel?.noOfOpenTab?.observe(viewLifecycleOwner) {
            (requireActivity() as MainActivity).changeToolbar(it!!, listenForSearch = { url ->
                if (isInWebView) {
                    createNewTB(WebViewFragments("Searching..", url), url)?.also { size ->
                        BrowserFragment.viewPager?.currentItem = size - 1
                    }
                } else {
                    val action =
                        HomeScrFragmentDirections.actionHomeScrFragmentToBrowserFragment(
                            "Searching..",
                            url,
                        )
                    findNavController().navigate(action)
                }
            }, goTo = {
                findNavController().popBackStack()
            }, viewTab = {
                if (isInWebView) {
                    requireActivity().goToTbActivity<ViewTabActivity>((parentFragment as BrowserFragment).getTbList())
                }
            })
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
        } else {
            inflater.inflate(R.menu.web_frag_menu, menu)
            if (menu is MenuBuilder) {
                menu.setOptionalIconsVisible(true)
            }
            val newTab = menu.findItem(R.id.new_tab_option_mnu)

            newTab?.setOnMenuItemClickListener {
                mainViewModel?.addMoreTab()
                val size = createNewTB(HomeScrFragment(true), null)
                Log.i(TAG, "onCreateOptionsMenu: $size")
                BrowserFragment.viewPager?.currentItem = size!! - 1
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
            addHomeIcon(it)
            isDialogBoxIsVisible = false
            iconsDialogBox?.dismiss()
        })
        isDialogBoxIsVisible = true
    }

    private fun addHomeIcon(homeSrcIcon: HomeSrcIcon) {
        viewModel.addVideoItem(homeSrcIcon)
    }

    override fun onPause() {
        super.onPause()
        if (isDialogBoxIsVisible) {
            iconsDialogBox?.dismiss()
        }
        if (isInWebView && isNewTab) {
            val size = BrowserFragment.viewPager?.currentItem
            size?.let {
                (parentFragment as BrowserFragment).removeFragment(it)
            }
        }
    }

    private fun recycleAdaptor() {
        binding.homeSrcIcon.apply {
            layoutManager = GridLayoutManager(requireActivity(), 4)
            homeSrcAdaptor = HomeSrcAdaptor { data: HomeSrcIcon, isAddIcon: Boolean ->
                if (isAddIcon) {
                    showDialogBox()
                } else if (data.name?.equals(getString(R.string.whatsapp_name))!!) {
                    requireActivity().goToNextActivity<WhatsappActivity>()
                } else {
                    if (!isInWebView) {
                        openWebDialogBox(data)
                    } else {
                        isNewTab = true
                        createNewTB(
                            WebViewFragments(
                                data.name,
                                data.url!!
                            ), data.url
                        )?.also { size ->
                                BrowserFragment.viewPager?.currentItem = size - 1
                            }
                    }
                }
            }
            adapter = homeSrcAdaptor
        }
    }

    private fun openWebDialogBox(data: HomeSrcIcon) {
        val action =
            HomeScrFragmentDirections.actionHomeScrFragmentToBrowserFragment(
                data.name!!,
                data.url!!
            )
        findNavController().navigate(action)
    }


    private fun createNewTB(fragment: Fragment, url: String?): Int? {
        return (parentFragment as BrowserFragment).setFragment(
            fragment, url
        )
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setData() {
        viewModel.getBookMarkResponse.observe(viewLifecycleOwner) {
            when (it) {
                is RemoteResponse.Error -> {
                    Log.i(TAG, "setData: ${it.exception?.localizedMessage}")
                }
                is RemoteResponse.Loading -> Log.i(TAG, "setData: ${it.data}")
                is RemoteResponse.Success -> {
                    val item = it.data as MutableList<*>
                    val res = mutableListOf<HomeSrcIcon>()
                    item.forEach { value ->
                        (value as HomeSrcIcon?)?.let { home ->
                            res.add(home)
                        }
                    }
                    homeSrcAdaptor.notifyDataSetChanged()
                    homeSrcAdaptor.submitList(res)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(false)
        binding.srcTv.show()
        (requireActivity() as MainActivity).supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_SHOW_TITLE
        (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
        (requireActivity() as MainActivity).supportActionBar!!.title =
            getString(R.string.app_name)
        Log.i(TAG, "onResume: Home Frage Create NEW Tab ${mainViewModel?.createNewTab?.value}")
        mainViewModel?.changeStateForCreateNewTB(false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(getString(R.string.add_to_home_src), isDialogBoxIsVisible)
    }

}