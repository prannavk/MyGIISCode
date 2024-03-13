package com.giis.mobileappproto1.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.security.crypto.EncryptedSharedPreferences
import com.giis.mobileappproto1.GIISApplication
import com.giis.mobileappproto1.GIISSessionTokenDataManager
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.data.models.VerifiedLoginDataDTO
import com.giis.mobileappproto1.databinding.ActivitySplashScreenBinding
import com.giis.mobileappproto1.ui.bottomsheets.NewBottomSheetDialogFragment
import com.giis.mobileappproto1.utils.globalTag
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader


class SplashScreenActivity : AppCompatActivity() {

    private val splashScreenDuration: Long = 2100
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var sessionManager: GIISSessionTokenDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_splash_screen)
        binding = DataBindingUtil.setContentView(
            this@SplashScreenActivity,
            R.layout.activity_splash_screen
        )

        sessionManager = GIISSessionTokenDataManager(applicationContext)
        Log.e(globalTag, "At SplashScreen, about to check Session and Login")
        val intent: Intent = if (sessionManager.validateSavedSessionAndCheckPref(1)) Intent(
            this,
            MainActivity::class.java
        ) else Intent(this, LoginActivity::class.java)

        Handler().postDelayed({
            startActivity(intent)
            finish()
        }, splashScreenDuration)


    }


}