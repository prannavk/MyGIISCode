package com.giis.mobileappproto1

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GIISApplication: Application() {

    companion object{

    }

    override fun onCreate() {
        super.onCreate()
    }

}