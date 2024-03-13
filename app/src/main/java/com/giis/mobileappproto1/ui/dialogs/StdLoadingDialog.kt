package com.giis.mobileappproto1.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.giis.mobileappproto1.R

class StdLoadingDialog(context: Context) : Dialog(context) {
    init {
        val params: WindowManager.LayoutParams = window!!.attributes
        params.gravity = Gravity.CENTER
        window!!.attributes = params
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setTitle("")
        setCancelable(true)
        val view: View = LayoutInflater.from(context)!!.inflate(R.layout.layout_load_anim, null)
        setContentView(view)
    }
}