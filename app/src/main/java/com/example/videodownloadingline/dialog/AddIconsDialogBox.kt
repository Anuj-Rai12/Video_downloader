package com.example.videodownloadingline.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TableRow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.example.videodownloadingline.R
import com.example.videodownloadingline.adaptor.sort_adptor.SortRecyclerAdaptor
import com.example.videodownloadingline.databinding.AddIconToHomeSrcDialogLayoutBinding
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.utils.*
import com.example.videodownloadingline.view_model.MainViewModel

private typealias ListerIcon = (bookmarks: HomeSrcIcon) -> Unit
private typealias ListenDismiss = () -> Unit
private typealias ListenSetPin = (flag: Boolean) -> Unit
private typealias ListenNewFolder = (folderName: String, position: Int) -> Unit
private typealias ListenNewFolder2 = (folderName: String, position: Int, flag: Boolean) -> Unit

class AddIconsDialogBox {
    private var alertDialog: AlertDialog? = null

    @SuppressLint("SetTextI18n")
    fun show(
        context: Context,
        flag: Boolean = true,
        listenerForDismiss: ListenDismiss,
        listenerIcon: ListerIcon
    ) {
        val con = (context as Activity)
        val alertDialog = AlertDialog.Builder(con)
        val inflater = (con).layoutInflater
        val binding = AddIconToHomeSrcDialogLayoutBinding.inflate(inflater)
        alertDialog.setView(binding.root)
        alertDialog.setCancelable(flag)
        binding.cancelBtn.setOnClickListener {
            listenerForDismiss()
        }
        binding.okBtn.setOnClickListener {
            val name = binding.nmeEd.text.toString()
            val url = binding.urlEd.text.toString()
            if (checkInputField(name) || checkInputField(url) || !con.isValidUrl(url)) {
                Toast.makeText(it.context, "Please Enter correct Information ", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            listenerIcon(HomeSrcIcon(id = 0, name, url))
        }
        setUpDialogBox(alertDialog)
    }

    fun dismiss() = alertDialog?.dismiss()


    fun createNewFolder(
        context: Context,
        flag: Boolean = true,
        listenerForDismiss: ListenDismiss,
        listenerForNewFolder: (folder: String) -> Unit
    ) {
        val con = (context as Activity)
        val alertDialog = AlertDialog.Builder(con)
        val inflater = (con).layoutInflater
        val binding = AddIconToHomeSrcDialogLayoutBinding.inflate(inflater)
        alertDialog.setView(binding.root)
        alertDialog.setCancelable(flag)
        binding.urlEdLayout.hide()
        binding.nmeEdLayout.hide()
        binding.folderNameLayout.show()
        binding.textBoxTitle.text = con.getString(R.string.new_folder_name)
        binding.okBtn.apply {
            updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = binding.folderNameLayout.id
                rightToRight = binding.folderNameLayout.id
            }
            text = con.getText(R.string.create_folder_name)
            setOnClickListener {
                val name = binding.folderNameTxt.text.toString()
                if (checkInputField(name)) {
                    Toast.makeText(
                        this.context,
                        "Please Enter correct Information ",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                listenerForNewFolder(name)
            }
        }
        binding.cancelBtn.setOnClickListener {
            listenerForDismiss()
        }
        setUpDialogBox(alertDialog)
    }


    fun displaySortingViewRecycle(
        context: Context,
        data: Array<String>,
        flag: Boolean = true,
        title: String,
        isFolder: Boolean = false,
        listenerForNewFolder: ListenNewFolder
    ) {
        val con = (context as Activity)
        val alertDialog = AlertDialog.Builder(con)
        val inflater = (con).layoutInflater
        val binding = AddIconToHomeSrcDialogLayoutBinding.inflate(inflater)
        val adaptor = SortRecyclerAdaptor { res, position ->
            listenerForNewFolder(res, position)
        }
        alertDialog.setView(binding.root)
        alertDialog.setCancelable(flag)
        binding.urlEdLayout.hide()
        binding.nmeEdLayout.hide()
        binding.folderNameLayout.hide()
        binding.okBtn.hide()
        binding.cancelBtn.hide()
        binding.mainRecycleView.show()
        binding.textBoxTitle.text = title
        binding.mainRecycleView.apply {
            adapter = adaptor
        }
        adaptor.getPublicFolder(isFolder)
        adaptor.submitList(data.toList())
        setUpDialogBox(alertDialog)
    }


    fun showDeleteVideoDialogBox(
        context: Context,
        flag: Boolean = true,
        listenerYesBtn: ListenDismiss,
        listenerNoBtn: ListenDismiss
    ) {
        val con = (context as Activity)
        val alertDialog = AlertDialog.Builder(con)
        val inflater = (con).layoutInflater
        val binding = AddIconToHomeSrcDialogLayoutBinding.inflate(inflater)
        alertDialog.setView(binding.root)
        alertDialog.setCancelable(flag)
        binding.urlEdLayout.hide()
        binding.nmeEdLayout.hide()
        binding.folderNameLayout.hide()
        binding.okBtn.hide()
        binding.cancelBtn.hide()
        binding.mainRecycleView.hide()
        binding.delBtn.show()
        binding.doNotDelBtn.show()
        binding.textBoxTitle.apply {
            updateLayoutParams<ConstraintLayout.LayoutParams> {
                rightToRight = binding.root.id
                setMargins(0, 12, 0, 0)
            }
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTypeface(typeface, Typeface.NORMAL)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            text = this.context.getString(R.string.del_dialog_txt)
        }
        binding.delBtn.text = binding.delBtn.context.getString(R.string.total_vid_view, "Yes")
        binding.doNotDelBtn.text =
            binding.doNotDelBtn.context.getString(R.string.total_vid_view, "No")
        binding.delBtn.setOnClickListener {
            listenerYesBtn()
        }
        binding.doNotDelBtn.setOnClickListener {
            listenerNoBtn()
        }
        setUpDialogBox(alertDialog)
    }

    private fun setUpDialogBox(alert: AlertDialog.Builder) {
        this.alertDialog = alert.create()
        this.alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.alertDialog?.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showOptionForOptPin(
        context: Context,
        flag: Boolean = true,
        text: String,
        listenSetPin: ListenSetPin
    ) {
        var selectColorFlag: Boolean? = null
        val con = (context as Activity)
        val alertDialog = AlertDialog.Builder(con)
        val inflater = (con).layoutInflater
        val binding = AddIconToHomeSrcDialogLayoutBinding.inflate(inflater)
        alertDialog.setView(binding.root)
        alertDialog.setCancelable(flag)
        binding.urlEdLayout.hide()
        binding.nmeEdLayout.hide()
        binding.folderNameLayout.hide()
        binding.mainRecycleView.hide()
        binding.delBtn.hide()
        binding.doNotDelBtn.hide()
        binding.selectWithOutPin.apply {
            setOnClickListener {
                selectColorFlag = true
                con.setColorForDrawableTextView(binding.selectWithPin, R.color.white)
                con.setColorForDrawableTextView(binding.selectWithOutPin)
            }
            show()
        }
        binding.selectWithPin.apply {
            setOnClickListener {
                selectColorFlag = false
                con.setColorForDrawableTextView(binding.selectWithOutPin, R.color.white)
                con.setColorForDrawableTextView(binding.selectWithPin)
            }
            show()
        }
        binding.cancelBtn.text = binding.cancelBtn.context.getString(R.string.cancel_txt)
        binding.cancelBtn.show()
        binding.textBoxTitle.text = text

        binding.okBtn.apply {
            this.text = this.context.getString(R.string.create_folder_name)
            updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = binding.selectWithPin.id
                rightToRight = binding.selectWithPin.id
            }
            show()
            setOnClickListener {
                selectColorFlag?.let { flag ->
                    listenSetPin(flag)
                    return@let
                } ?: con.toastMsg("Please Select Option")
            }
        }
        setUpDialogBox(alertDialog)
    }


    fun displayFolderViewRecycle(
        context: Context,
        data: Array<String>,
        flag: Boolean = true,
        title: String,
        lifecycleOwner: LifecycleOwner,
        required: Context,
        listenerForNewFolder: ListenNewFolder2
    ) {
        val viewModel = MainViewModel.getInstance()
        val list = mutableListOf<String>()
        val name = mutableListOf<String>()
        val con = (context as Activity)
        var flagIdx: Boolean? = null
        val alertDialog = AlertDialog.Builder(con)
        val inflater = (con).layoutInflater
        val binding = AddIconToHomeSrcDialogLayoutBinding.inflate(inflater)
        val adaptor = SortRecyclerAdaptor { res, position ->
            binding.textBoxTitle.text = res
            val index = if (name.isEmpty()) data.indexOf(res) else name.indexOf(res)
            if (index != -1) {
                if (list.isNotEmpty()) {
                    flagIdx?.let { listenerForNewFolder("/$res", position, it) }
                        ?: con.toastMsg("Error Occur!!")
                } else {
                    if (index == 0) {
                        flagIdx = false
                        val filePath = getFileDir("", context)
                        viewModel?.getFolderDir(filePath)
                    } else {
                        flagIdx = true
                        val targetPath =
                            getFileDir(con.getString(R.string.file_path_2), context, false)
                        viewModel?.getFolderDir(targetPath)
                    }
                }
            }
        }
        alertDialog.setView(binding.root)
        alertDialog.setCancelable(flag)
        binding.urlEdLayout.hide()
        binding.nmeEdLayout.hide()
        binding.folderNameLayout.hide()
        binding.okBtn.hide()
        binding.cancelBtn.hide()
        binding.mainRecycleView.show()
        binding.textBoxTitle.text = title
        binding.mainRecycleView.apply {
            adapter = adaptor
        }
        adaptor.getPublicFolder(true)
        adaptor.submitList(data.toList())
        viewModel?.folderDir?.observe(lifecycleOwner) {
            if (it != null) {
                if (it.isNotEmpty()) {
                    list.clear()
                    name.clear()
                    it.forEach { file ->
                        list.add(file.fileLoc)
                        name.add(file.fileTitle)
                    }
                    adaptor.notifyDataSetChanged()
                    adaptor.submitList(name)
                } else {
                    Log.i(TAG, "displayFolderViewRecycle: log it not null but empty")
                    adaptor.submitList(data.toList())
                }
            } else {
                Log.i(TAG, "displayFolderViewRecycle: it is Null")
                adaptor.submitList(data.toList())
            }
        }
        setUpDialogBox(alertDialog)
    }
}