package com.example.videodownloadingline.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.videodownloadingline.databinding.AddIconToHomeSrcDialogLayoutBinding
import com.example.videodownloadingline.model.homesrcicon.HomeSrcIcon
import com.example.videodownloadingline.utils.checkInputField

private typealias ListerIcon = (bookmarks: HomeSrcIcon) -> Unit
private typealias ListenDismiss = () -> Unit

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
                return@setOnClickListener
            }
            listenerIcon(HomeSrcIcon(id = 121, name, url))
        }
        this.alertDialog = alertDialog.create()
        this.alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.alertDialog?.show()
    }

    fun dismiss() = alertDialog?.dismiss()

}