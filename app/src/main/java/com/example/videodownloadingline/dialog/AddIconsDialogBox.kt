package com.example.videodownloadingline.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.example.videodownloadingline.R
import com.example.videodownloadingline.databinding.AddIconToHomeSrcDialogLayoutBinding
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.utils.checkInputField
import com.example.videodownloadingline.utils.hide
import com.example.videodownloadingline.utils.show

private typealias ListerIcon = (bookmarks: HomeSrcIcon) -> Unit
private typealias ListenDismiss = () -> Unit
private typealias ListenNewFolder = (folderName: String) -> Unit

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
            if (checkInputField(name) || checkInputField(url)) {
                Toast.makeText(it.context, "Please Enter correct Information ", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            listenerIcon(HomeSrcIcon(id = 121, name, url))
        }
        this.alertDialog = alertDialog.create()
        this.alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.alertDialog?.show()
    }

    fun dismiss() = alertDialog?.dismiss()


    fun createNewFolder(
        context: Context,
        flag: Boolean = true,
        listenerForDismiss: ListenDismiss,
        listenerForNewFolder: ListenNewFolder
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
                rightToRight=binding.folderNameLayout.id
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
        this.alertDialog = alertDialog.create()
        this.alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.alertDialog?.show()
    }


}