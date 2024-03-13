package com.giis.mobileappproto1.utils

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.giis.mobileappproto1.R
import com.google.android.material.snackbar.Snackbar

class Commons {
    companion object {
        fun simpleSnackAlert(fromContext: Context, view: View, Message: String) {
            val sBar = Snackbar.make(view, Message, Snackbar.LENGTH_SHORT)
            val sBarView = sBar.view
            // sBarView.setBackgroundColor(Color.WHITE)
//            val textColor =
//                AppCompatResources.getColorStateList(fromContext, R.color.bubble_item_selected)
//            sBar.setActionTextColor(textColor)
            sBar.show()
        }

        fun toastIt(fromContext: Context, Message: String) {
            Toast.makeText(fromContext, Message, Toast.LENGTH_SHORT).show()
        }

    }
}