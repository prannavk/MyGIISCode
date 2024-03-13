package com.giis.mobileappproto1.ui.facilitators

import android.util.Log
import android.view.View
import android.widget.Toast
import com.giis.mobileappproto1.data.models.ObtainedOTPDTO
import com.giis.mobileappproto1.data.models.RequestLoginOTPDTO
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.ui.bottomsheets.NewBottomSheetDialogFragment
import com.giis.mobileappproto1.ui.dialogs.StdLoadingDialog
import com.giis.mobileappproto1.ui.fragments.LoginCredentialsFragment
import com.giis.mobileappproto1.ui.fragments.OTPVerifyFragment
import com.giis.mobileappproto1.ui.viewmodels.OTPVerifyViewModel
import com.giis.mobileappproto1.utils.networkFailureMessage
import com.giis.mobileappproto1.utils.otpFailureMessage

open class SendOtpClickListener(
    private val otpFragmentContext: OTPVerifyFragment,
    private val viewModel: OTPVerifyViewModel,
    private val loadingDialog: StdLoadingDialog,
    private var requestLoginOTPDTO: RequestLoginOTPDTO,
    private val blockFun: () -> Unit
) :
    View.OnClickListener {
    override fun onClick(v: View?) {
        // start anim
        LoginCredentialsFragment.showProgressAnim(this.loadingDialog, 5000L)
        // Network Call First Step for Login Otp
        viewModel.obtainLoginOTPFor(requestLoginOTPDTO)
            .observe(otpFragmentContext.viewLifecycleOwner) {
                if (this.loadingDialog.isShowing)
                    this.loadingDialog.cancel()
                when (it) {
                    is Result.Success -> {
                        otpFragmentContext.bottomSheetFragment =
                            NewBottomSheetDialogFragment(
                                viewModel = viewModel,
                                loadingDialog = loadingDialog,
                                choice = otpFragmentContext.emailOrMobileChoice,
                                passedOTPDTO = it.data?.apply { } ?: run {
                                    Log.e(
                                        "VerificationData",
                                        "Unexpected Issue with PassedOTPDTO"
                                    )
                                    ObtainedOTPDTO(
                                        userName = "Invalid Data",
                                        loginOpt = "Invalid",
                                        userId = -1,
                                        emailOrPhone = ""
                                    )
                                },
                                requestLoginOTPDTO = requestLoginOTPDTO,
                                parentContext = otpFragmentContext,
                                blockFunPassed = blockFun
                            )
                        otpFragmentContext.bottomSheetFragment.show(
                            otpFragmentContext.requireActivity().supportFragmentManager,
                            ""
                        )
                    }

                    is Result.Failure -> {
                        Toast.makeText(
                            otpFragmentContext.requireActivity(),
                            otpFailureMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "verificationData",
                            "Result.Failure in OTPVerifyFragment says -> ${it.message}"
                        )
                    }

                    is Result.NetworkError -> {
                        Toast.makeText(
                            otpFragmentContext.requireActivity(),
                            networkFailureMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
}