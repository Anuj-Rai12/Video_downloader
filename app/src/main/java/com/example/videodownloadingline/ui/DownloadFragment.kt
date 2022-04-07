package com.example.videodownloadingline.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemGridAdaptor
import com.example.videodownloadingline.adaptor.download_item_adaptor.DownloadItemLinearAdaptor
import com.example.videodownloadingline.databinding.DownloadFragmentLayoutBinding
import com.example.videodownloadingline.dialog.AddIconsDialogBox
import com.example.videodownloadingline.model.downloaditem.SampleDownloadItem
import com.example.videodownloadingline.utils.TAG
import com.example.videodownloadingline.utils.hide
import com.example.videodownloadingline.utils.show


class DownloadFragment : Fragment(R.layout.download_fragment_layout) {
    private lateinit var binding: DownloadFragmentLayoutBinding
    private var gridAdaptor: DownloadItemGridAdaptor? = null
    private var linearAdaptor: DownloadItemLinearAdaptor? = null
    private var newFolderDialogBox: AddIconsDialogBox? = null
    private var isDialogBoxIsVisible: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DownloadFragmentLayoutBinding.bind(view)
        initial()
        menuClickListener()
        binding.toolBarMainActivity.searchBoxEd.doOnTextChanged { text, _, _, _ ->
            Log.i(TAG, "onViewCreated: $text")
        }
        setUpRecycleView(GridLayoutManager(requireActivity(), 2))
        setUpData()
        changeLayoutView()
        binding.addNewFolderBtn.setOnClickListener {
            showDialogBox()
        }
    }

    override fun onPause() {
        super.onPause()
        newFolderDialogBox?.dismiss()
    }
    private fun showDialogBox() {
        newFolderDialogBox = AddIconsDialogBox()
        newFolderDialogBox?.createNewFolder(context = requireActivity(), listenerForDismiss = {
            newFolderDialogBox?.dismiss()
            isDialogBoxIsVisible = false
        }, listenerForNewFolder = {
            Log.i(TAG, "showDialogBox: $it")
            newFolderDialogBox?.dismiss()
        })
        isDialogBoxIsVisible = true
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setUpData() {
        val list = listOf(
            SampleDownloadItem(
                1, "File 1", DEFAULT_BUFFER_SIZE
            ), SampleDownloadItem(
                2, "File 2", DEFAULT_BUFFER_SIZE
            ), SampleDownloadItem(
                3, "File 3", DEFAULT_BUFFER_SIZE
            ), SampleDownloadItem(
                4, "File 4", DEFAULT_BUFFER_SIZE
            ), SampleDownloadItem(
                5, "File 5", DEFAULT_BUFFER_SIZE
            ), SampleDownloadItem(
                6, "File 6", DEFAULT_BUFFER_SIZE
            )
        )
        gridAdaptor?.notifyDataSetChanged()
        gridAdaptor?.submitList(list)
        linearAdaptor?.notifyDataSetChanged()
        linearAdaptor?.submitList(list)
    }

    private fun setUpRecycleView(layoutManager: GridLayoutManager) {
        linearAdaptor = null
        binding.recycleView.apply {
            setHasFixedSize(true)
            this.layoutManager = layoutManager
            gridAdaptor = DownloadItemGridAdaptor {

            }
            adapter = gridAdaptor
        }
    }

    private fun setUpRecycleView(layoutManager: LinearLayoutManager) {
        gridAdaptor = null
        binding.recycleView.apply {
            setHasFixedSize(true)
            linearAdaptor = DownloadItemLinearAdaptor {

            }
            this.layoutManager = layoutManager
            adapter = linearAdaptor
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeLayoutView() {
        binding.btnForGridView.setOnClickListener {
            setBtnColor(binding.btnForListView, R.color.white)
            setBtnColor(binding.btnForGridView)
            setUpRecycleView(GridLayoutManager(requireActivity(), 2))
            setUpData()
        }
        binding.btnForListView.setOnClickListener {
            setBtnColor(binding.btnForGridView, R.color.white)
            setBtnColor(binding.btnForListView)
            setUpRecycleView((LinearLayoutManager(requireActivity())))
            setUpData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setBtnColor(view: AppCompatImageButton, color: Int = R.color.Surfie_Green_color) {
        view.setColorFilter(
            requireActivity().getColor(color),
            android.graphics.PorterDuff.Mode.MULTIPLY
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun menuClickListener() {

        binding.toolBarMainActivity.srcBtn.setOnClickListener {
            binding.toolBarMainActivity.toolbarHomeBtn.apply {
                setImageResource(R.drawable.ic_arrow_back)
                show()
            }
            binding.toolBarMainActivity.searchBoxEd.apply {
                updateLayoutParams<ConstraintLayout.LayoutParams> {
                    rightToLeft = it.id
                }
                binding.toolBarMainActivity.toolBarLayout.title = ""
                show()
            }
            it.hide()
        }


        binding.toolBarMainActivity.toolbarHomeBtn.setOnClickListener {
            it.hide()
            binding.toolBarMainActivity.searchBoxEd.hide()
            binding.toolBarMainActivity.toolBarLayout.title =
                getString(R.string.content_description_down)
            binding.toolBarMainActivity.srcBtn.show()
        }

        binding.toolBarMainActivity.threeBotMnuBtn.setOnClickListener {
            Toast.makeText(
                activity,
                "List icon clicked Icon Clicked",
                Toast.LENGTH_SHORT
            ).show()
        }


    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initial() {
        binding.toolBarMainActivity.totalTabOp.hide()
        binding.toolBarMainActivity.toolbarHomeBtn.hide()
        binding.toolBarMainActivity.srcBtn.show()
        binding.toolBarMainActivity.threeBotMnuBtn.setImageResource(R.drawable.ic_new_list_vew)
        binding.toolBarMainActivity.searchBoxEd.hide()
        binding.totalVidTxt.text = getString(R.string.total_vid, 1100)
        binding.viewTxt.text = getString(R.string.total_vid_view, "view:")
        setBtnColor(binding.btnForGridView)
        binding.toolBarMainActivity.toolBarLayout.title =
            getString(R.string.content_description_down)
        binding.toolBarMainActivity.toolBarLayout.setTitleTextColor(Color.WHITE)
    }


}