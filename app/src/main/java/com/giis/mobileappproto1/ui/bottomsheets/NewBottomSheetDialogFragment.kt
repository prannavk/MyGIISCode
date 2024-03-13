package com.giis.mobileappproto1.ui.bottomsheets

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.chaos.view.PinView
import com.giis.mobileappproto1.GIISSessionTokenDataManager
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.R
import com.giis.mobileappproto1.data.models.CampusAddressDTO
import com.giis.mobileappproto1.data.models.EntityLoginStampDTO
import com.giis.mobileappproto1.data.models.ObtainedOTPDTO
import com.giis.mobileappproto1.data.models.RequestLoginOTPDTO
import com.giis.mobileappproto1.data.models.UserRoleDTO
import com.giis.mobileappproto1.data.models.VerifiedLoginDataDTO
import com.giis.mobileappproto1.data.models.VerifyOTPDTO
import com.giis.mobileappproto1.ui.activities.MainActivity
import com.giis.mobileappproto1.ui.dialogs.StdLoadingDialog
import com.giis.mobileappproto1.ui.facilitators.SendOtpClickListener
import com.giis.mobileappproto1.ui.fragments.LoginCredentialsFragment
import com.giis.mobileappproto1.ui.fragments.OTPVerifyFragment
import com.giis.mobileappproto1.ui.viewmodels.NewBottomSheetDialogViewModel
import com.giis.mobileappproto1.ui.viewmodels.OTPVerifyViewModel
import com.giis.mobileappproto1.utils.globalTag
import com.giis.mobileappproto1.utils.networkFailureMessage2
import com.giis.mobileappproto1.utils.sheetTitleEmail
import com.giis.mobileappproto1.utils.sheetTitleMobileNumber
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class NewBottomSheetDialogFragment(
    private val passedOTPDTO: ObtainedOTPDTO,
    private val viewModel: OTPVerifyViewModel,
    private val loadingDialog: StdLoadingDialog,
    private var requestLoginOTPDTO: RequestLoginOTPDTO,
    private val choice: Boolean,
    private val parentContext: OTPVerifyFragment,
    private val blockFunPassed: () -> Unit
) : BottomSheetDialogFragment() {

    lateinit var ownViewModel: NewBottomSheetDialogViewModel
        @Inject set

    // private lateinit var ownLoadingDialog: StdLoadingDialog
    private var enteredOtp: String = "1111"
    private lateinit var resendTextView: TextView
    private lateinit var ctx: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ctx = requireActivity()
        return inflater.inflate(R.layout.fragment_custombottomsheet, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sheetTitle = view.findViewById<TextView>(R.id.verifyTitleTV)
        val sheetDesc = view.findViewById<TextView>(R.id.OotpSentMessage)
        val sheetDescText = "OTP is sent to ${passedOTPDTO.emailOrPhone}"
        // Setting BottomSheet Title based on otp provision choice given by user
        // choice if true, its Email, if false its Mobile Number
        setSheetTitleAndDescription(sheetTitle, sheetDesc, sheetDescText)

        // Add underline, Timer and functionality to resend TextView
        resendTextView = view.findViewById(R.id.resendOtpTimerTextView)
        GlobalScope.launch(Dispatchers.Default) {
            var secondCounter: UInt = 180u
            val targetCounter: UInt = 1u

            while (secondCounter >= targetCounter) {
                withContext(Dispatchers.Main) {
                    val sec = secondCounter % 60u
                    val min = secondCounter / 60u
                    val time =
                        if (sec >= 10u) "Resend OTP (0$min:$sec)" else "Resend OTP (0$min:0$sec)"
                    val spannableString = SpannableString(time)
                    spannableString.setSpan(UnderlineSpan(), 0, time.length, 0)
                    resendTextView.text =
                        spannableString
                }
                delay(1000L)
                secondCounter--
                dialog?.setOnDismissListener {
                    secondCounter = targetCounter
                }
            }

            // RESEND OTP Task implementation
            resendOtpImplementation(resendTextView)
        }

//        val timerAlt = object : CountDownTimer(TimeUnit.MINUTES.toMillis(3), 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                // Update Remaining Time - Resend TextView
//            }
//
//            override fun onFinish() {
//                // resend textView activates
//            }
//        }

        val dissMissCrossRef = view.findViewById<CircleImageView>(R.id.dismiss_cross)
        dissMissCrossRef.setOnClickListener { dismiss(); }

        val otpPinViewRef = view.findViewById<PinView>(R.id.otpMainPinView)
        val validateButton = view.findViewById<Button>(R.id.validateButton)
        val errorTextView = view.findViewById<TextView>(R.id.invalidOTPMsgTV)
        configureActivePinView(otpPinViewRef, 4u, validateButton)

        // Loading Dialog instantiation
        // ownLoadingDialog = StdLoadingDialog(parentContext.requireActivity())

        // verifiedLoginDataDTO Observer placed inside validateButton onClick
        validateButton.setOnClickListener {
            // LoginCredentialsFragment.showProgressAnim(this.ownLoadingDialog, 4000L)
            // Defining verifyOTPDTO to be sent in the network call
            val enterOTPDTO = VerifyOTPDTO(
                loginOpt = this.enteredOtp,
                userId = passedOTPDTO.userId,
                userName = passedOTPDTO.userName
            )
            if (!enterOTPDTO.equals("")) {
                Log.e(
                    "verificationData",
                    "${enterOTPDTO.userName} has given otp as: ${enterOTPDTO.loginOpt}"
                ); }
            ownViewModel.confirmLoginOtp(enterOTPDTO).observe(this.viewLifecycleOwner) { apiRes ->
//                if (ownLoadingDialog.isShowing)
//                    ownLoadingDialog.cancel()
                Log.e("verificationData", apiRes.toString())
                when (apiRes) {
                    is Result.Success -> {
                        val verifiedAuthData: VerifiedLoginDataDTO =
                            apiRes.data?.let { Log.e("verificationData", ""); it } ?: run {
                                Log.e(
                                    "verificationData",
                                    "There has been an issue with the login data"
                                ); getOnVerificationFailureObject();
                            }
                        Log.e(
                            "verificationData",
                            "${verifiedAuthData.nameOfPerson}, ${verifiedAuthData.brandName} and ${verifiedAuthData.token}"
                        )
                        if (verifiedAuthData.token.equals("Failure") && verifiedAuthData.validity == -1 && verifiedAuthData.profilePicture == "") {

                            // Actual Failure
                            Toast.makeText(ctx, "Failure in OTP Verification", Toast.LENGTH_SHORT)
                                .show()
                            setErrorStateInPinView(otpPinViewRef, errorTextView)

                        } else {
                            // Actual Success
                            // resetting red 'error' colors
                            resetErrorStateInPinView(otpPinViewRef, errorTextView)

                            // Showing success Dialog on correct Otp initial way
                            // showSuccessDialog()

                            // Lets create a LoginStamp record and store apiRes in our local db
//                                val database = getDb()
//                                val loginStampNow = EntityLoginStampDTO(
//                                    id = 0,
//                                    nameOfLoggedPerson = verifiedAuthData.nameOfPerson!!,
//                                    loggedPersonEmail = parentContext.enteredEmail,
//                                    loginDateTime = Date(),
//                                    otpVerificationApproach = requestLoginOTPDTO.isEmail
//                                )
//                                this.ownViewModel.insertLoginInfo(loginStamp = loginStampNow)
                            // Creating Intent with 3 extras for navigating to TeacherDetailsActivity
                            // StringExtra -> passedOTPDTO.emailOrPhone -> Has the string
                            // BooleanExtra -> choice -> if choice is true, what apiRes has is email if not its a phone number
                            // ParcelableExtra -> Entire VerifiedLoginDataDTO
//                                navigateToTeacherDetails(
//                                    this@NewBottomSheetDialogFragment.choice,
//                                    this@NewBottomSheetDialogFragment.passedOTPDTO.emailOrPhone,
//                                    verifiedAuthData
//                                )
                            val sessionManager =
                                GIISSessionTokenDataManager(ctx.applicationContext)
                            val session = sessionManager.createAndSaveNewSessionWithPreference(
                                verifiedAuthData,
                                parentContext.prefBool
                            )
                            if (session) Log.e(globalTag, "Session Saving Success") else Log.e(
                                globalTag, "Problem faced while saving the Session"
                            )

                            val intent = Intent(ctx, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            ctx.startActivity(intent)
                            dismiss()
                            blockFunPassed.invoke()
                        }
                    }

                    is Result.Failure -> {
                        Toast.makeText(
                            ctx,
                            apiRes.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        // change pinView color to red color on incorrect PIN
                        setErrorStateInPinView(otpPinViewRef, errorTextView)
                    }

                    is Result.NetworkError -> {
                        Toast.makeText(
                            ctx,
                            networkFailureMessage2,
                            Toast.LENGTH_SHORT
                        ).show()
                        // change pinView color to red color on incorrect PIN
                        setErrorStateInPinView(otpPinViewRef, errorTextView)
                    }
                }
            }
            // passedOTPDTO.loginOpt == otpPinViewRef.text.toString() -> Earlier Simpler Approach
        }

    }

    private fun resetErrorStateInPinView(otpPinViewRef: PinView, errorTextView: TextView) {
        if (errorTextView.visibility == View.VISIBLE) {
            otpPinViewRef.setBackgroundResource(R.drawable.custom_edittext)
            errorTextView.visibility = View.GONE
        }
    }

    private fun setErrorStateInPinView(otpPinViewRef: PinView, errorTextView: TextView) {
        if (errorTextView.visibility == View.GONE) {
            otpPinViewRef.setBackgroundResource(R.drawable.custom_edittext_error)
            errorTextView.visibility = View.VISIBLE
        }
    }

    private fun configureActivePinView(
        pinViewRef: PinView,
        boxCount: UInt,
        validateButton: Button
    ) {
        pinViewRef.requestFocus()
        if (pinViewRef.hasFocus()) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                pinViewRef.windowInsetsController?.show(WindowInsets.Type.ime())
            } else {
                val inputMethodManager =
                    ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
            }
        }
        pinViewRef.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Activate validate Otp Button on PinView entry
                validateButton.isEnabled = s.toString().length.toUInt() == boxCount
                enteredOtp =
                    if (validateButton.isEnabled) pinViewRef.text.toString().trim() else ""
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setSheetTitleAndDescription(
        sheetTitle: TextView,
        sheetDesc: TextView,
        sheetDescText: String
    ) {
        if (this.choice) {
            sheetTitle.text = sheetTitleEmail
            sheetDesc.text = sheetDescText
        } else {
            sheetTitle.text = sheetTitleMobileNumber
            sheetDesc.text = sheetDescText
        }
    }

    private suspend fun resendOtpImplementation(textView: TextView) {
        withContext(Dispatchers.Main) {
            val time = "CLICK HERE TO RESEND OTP"
            val spannableString = SpannableString(time)
            spannableString.setSpan(UnderlineSpan(), 0, time.length, 0)
            textView.text = spannableString
            // On exhaustion of resend timer -> trigger button onClick and dismiss this fragment
            textView.setOnClickListener(object :
                SendOtpClickListener(
                    viewModel = this@NewBottomSheetDialogFragment.viewModel,
                    loadingDialog = this@NewBottomSheetDialogFragment.loadingDialog,
                    requestLoginOTPDTO = this@NewBottomSheetDialogFragment.requestLoginOTPDTO,
                    otpFragmentContext = this@NewBottomSheetDialogFragment.parentContext,
                    blockFun = this@NewBottomSheetDialogFragment.blockFunPassed
                ) {
                override fun onClick(v: View?) {
                    super.onClick(v)
                    dismiss()
                }
            })
        }
    }

    private fun showSuccessDialog() {
        val dialog = Dialog(ctx)
        dialog.setContentView(R.layout.dialog_custom_success)
        dialog.findViewById<Button>(R.id.successOkBtn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

//    private fun navigateToTeacherDetails(
//        booleanExtra: Boolean,
//        stringExtra: String,
//        authDataParcelable: VerifiedLoginDataDTO
//    ) {
//        val intent =
//            with(
//                Intent(
//                    requireActivity(),
//                    TeacherDetailsActivity::class.java
//                )
//            ) {
//                putExtra(
//                    "BooleanChoice",
//                    booleanExtra
//                )
//                putExtra(
//                    "StringEmailOrPhone",
//                    stringExtra
//                )
//                putExtra("ParcelVerifiedLoginDataDTO", authDataParcelable)
//            }
//        requireActivity().startActivity(intent)
//        requireActivity().finish()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
//        if (ownLoadingDialog.isShowing)
//            ownLoadingDialog.cancel()
    }

    companion object {
        fun getOnVerificationFailureObject(): VerifiedLoginDataDTO {
            return VerifiedLoginDataDTO(
                token = "failure",
                userName = "",
                validity = -1,
                refreshToken = "",
                id = "",
                guidId = "",
                expiredTime = "",
                role = "",
                user_role = listOf(
                    UserRoleDTO("", -1, -1)
                ),
                nameOfPerson = "",
                brandId = -1,
                campusName = "",
                campusId = -1,
                brandName = "",
                profilePicture = "",
                brandLogoUrl = "",
                campusAddress = CampusAddressDTO(),
                campusAddressJson = ""
            )
        }
    }


}