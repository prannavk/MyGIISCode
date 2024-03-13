package com.giis.mobileappproto1.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
//import com.example.task2proto2try.R
//import com.example.task2proto2try.data.LoginAuthCredential
//import com.example.task2proto2try.databinding.ActivityLoginBinding
//import com.example.task2proto2try.ui.commons.OnDataPassFacilitator
//import com.example.task2proto2try.ui.fragments.LoginCredentialsFragment
//import com.example.task2proto2try.ui.fragments.OTPVerifyFragment
//import com.example.task2proto2try.viewmodels.LoginCredentialsViewModel
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.data.models.LoginAuthCredentialDTO
import com.giis.mobileappproto1.databinding.ActivityLoginBinding
import com.giis.mobileappproto1.ui.facilitators.OnDataPassFacilitator
import com.giis.mobileappproto1.ui.fragments.LoginCredentialsFragment
import com.giis.mobileappproto1.ui.fragments.OTPVerifyFragment
import com.giis.mobileappproto1.utils.globalTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), OnDataPassFacilitator {
    private lateinit var binding: ActivityLoginBinding
    private val manager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_login)
        binding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)

        // Initial step for FragmentContainerView
        manager.beginTransaction()
            .add(R.id.loginFragmentContainerView, LoginCredentialsFragment())
            .commit()
    }

    override fun onBackPressed() {
        val backStackCount = manager.backStackEntryCount
        if (backStackCount > 0) {
            manager.popBackStackImmediate()
        } else {
            super.onBackPressed()
            // finish()
        }
    }

    override fun dataFacilitate(
        data: LoginAuthCredentialDTO,
        enteredEmail: String,
        preferenceFlag: Boolean
    ) {
        val bundle = Bundle().apply {
            putParcelable("authCred", data)
            putString("enteredEmail", enteredEmail)
            putBoolean("prefBool", preferenceFlag)
        }.also { Log.e(globalTag, "checked? $preferenceFlag") }

        val nextFragment = OTPVerifyFragment()
        nextFragment.arguments = bundle
        manager.beginTransaction()
            .replace(R.id.loginFragmentContainerView, nextFragment)
            // .addToBackStack("OTPVerifyFragment")
            .commit()
    }

}