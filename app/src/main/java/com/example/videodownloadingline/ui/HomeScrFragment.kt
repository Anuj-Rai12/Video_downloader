package com.example.videodownloadingline.ui


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import com.example.videodownloadingline.BuildConfig
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
    private var isFetchBookMarksDb: Boolean = false
    private var isNewTab: Boolean = false
    private var permissionManager: PermissionManager? = null
    private var deleteDialogBox: AddIconsDialogBox? = null
    private val viewModel: HomeSrcFragmentViewModel by viewModels()
    private var mainViewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = MainViewModel.getInstance()
        permissionManager = PermissionManager.from(this)
        savedInstanceState?.let {
            isFetchBookMarksDb = it.getBoolean(getString(R.string.add_to_home_src))
        }
        Log.i(TAG, "onCreate: isDialogBoxIsVisible  value is $isFetchBookMarksDb ")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeSrcFragmentBinding.bind(view)
        activity?.changeStatusBarColor()
        requestPermission()
        viewModel.eventForDeleteBookMarkIc.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { res ->
                Log.i(TAG, "onViewCreated: $res delete id ")
                binding.root.showSandbar("Book Mark is Deleted Successfully", color = Color.GREEN)
            }
        }

        recycleAdaptor()
        setData()
        binding.srcTv.setOnClickListener {
            it.hide()
            setHasOptionsMenu(true)
            currentTab()
        }
        permissionManager?.checkPermission {}
    }

    private fun requestPermission() {
        permissionManager?.request(Permission.Storage)
            ?.rationale(getString(R.string.permission_desc, "Storage"))
            ?.checkDetailedPermission { result ->
                if (!result.all { it.value }) {
                    Log.i(TAG, "showErrorDialog: ${result.keys} and ${result.values}")
                    showErrorDialog()
                }
            }
    }

    private fun showErrorDialog() {
        activity?.showDialogBox(desc = "We need this Permission to Manger the Files and Folder") {     //For Request Permission
            if (Build.VERSION.SDK_INT < 30) {
                // Open Setting Page
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri: Uri =
                    Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    private fun currentTab() {
        mainViewModel?.noOfOpenTab?.observe(viewLifecycleOwner) {
            (requireActivity() as MainActivity).changeToolbar(
                it!!,
                url = "",
                listenForSearch = { url ->
                    if (isInWebView) {
                        createNewTB(WebViewFragments("Searching..", url), url)?.also { size ->
                            BrowserFragment.viewPager?.currentItem = size - 1
                        }
                    } else {
                        val action =
                            HomeScrFragmentDirections.actionHomeScrFragmentToBrowserFragment(
                                "Searching..",
                                url
                            )
                        findNavController().navigate(action)
                    }
                },
                goTo = {
                    findNavController().popBackStack()
                },
                viewTab = {
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
            val settingTb = menu.findItem(R.id.setting_btn_mnu)
            settingTb.setOnMenuItemClickListener {
                activity?.goToNextActivity<SettingActivity>()
                return@setOnMenuItemClickListener true
            }
        } else {
            inflater.inflate(R.menu.web_frag_menu, menu)
            if (menu is MenuBuilder) {
                menu.setOptionalIconsVisible(true)
            }
            val newTab = menu.findItem(R.id.new_tab_option_mnu)
            val closeTab = menu.findItem(R.id.close_tab_mnu)

            newTab?.setOnMenuItemClickListener {
                mainViewModel?.addMoreTab()
                val size = createNewTB(HomeScrFragment(true), null)
                Log.i(TAG, "onCreateOptionsMenu: $size")
                BrowserFragment.viewPager?.currentItem = size!! - 1
                return@setOnMenuItemClickListener true
            }

            closeTab?.setOnMenuItemClickListener {
                val way = (parentFragment as BrowserFragment)
                if (way.getTbList().isNullOrEmpty()) {
                    findNavController().popBackStack()
                }
                way.getTbList()?.let {
                    mainViewModel?.removeTab()
                    val index = it.last().id - 1
                    way.removeFragment(index, true)
                    BrowserFragment.viewPager?.currentItem = index
                }
                if (way.getTbList().isNullOrEmpty()) {
                    findNavController().popBackStack()
                }
                return@setOnMenuItemClickListener true
            }

        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showDialogBox() {
        iconsDialogBox = AddIconsDialogBox()
        iconsDialogBox?.show(context = requireActivity(), listenerForDismiss = {
            iconsDialogBox?.dismiss()
        }, listenerIcon = {
            addHomeIcon(it)
            iconsDialogBox?.dismiss()
        })
    }

    private fun addHomeIcon(homeSrcIcon: HomeSrcIcon) {
        viewModel.addVideoItem(homeSrcIcon)
    }

    override fun onPause() {
        super.onPause()
        iconsDialogBox?.dismiss()
        deleteDialogBox?.dismiss()
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
            homeSrcAdaptor = HomeSrcAdaptor(itemClicked = { data: HomeSrcIcon, isAddIcon: Boolean ->
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
            }, itemLongClicked = { data, isAddIcon ->
                if (!isAddIcon) {
                    deleteItem(data)
                }
            })
            adapter = homeSrcAdaptor
        }
    }

    private fun deleteItem(data: HomeSrcIcon) {
        deleteDialogBox = AddIconsDialogBox()
        deleteDialogBox?.showDeleteVideoDialogBox(
            requireActivity(),
            title = "Are you sure you want to delete this BookMark.",
            listenerNoBtn = {
                deleteDialogBox?.dismiss()
            },
            listenerYesBtn = {
                deleteDialogBox?.dismiss()
                viewModel.deleteBookMarkIc(data)
            })
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
        permissionManager?.checkPermission {}
        if (!isFetchBookMarksDb) {
            viewModel.fetchBookMark()
            isFetchBookMarksDb = true
        }
        (requireActivity() as MainActivity).supportActionBar!!.displayOptions =
            ActionBar.DISPLAY_SHOW_TITLE
        (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowCustomEnabled(false)
        (requireActivity() as MainActivity).supportActionBar!!.title =
            getString(R.string.app_name)
        Log.i(TAG, "onResume: Home Fragment Create NEW Tab ${mainViewModel?.createNewTab?.value}")
        mainViewModel?.changeStateForCreateNewTB(false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(getString(R.string.add_to_home_src), isFetchBookMarksDb)
    }

}