package com.giis.mobileappproto1.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import com.giis.mobileappproto1.data.models.LoginAuthCredentialDTO
import com.giis.mobileappproto1.data.models.ObtainedOTPDTO
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.data.models.RequestLoginOTPDTO
import com.giis.mobileappproto1.databinding.FragmentOTPVerifyBinding
import com.giis.mobileappproto1.ui.bottomsheets.NewBottomSheetDialogFragment
import com.giis.mobileappproto1.ui.dialogs.StdLoadingDialog
import com.giis.mobileappproto1.ui.facilitators.SendOtpClickListener
import com.giis.mobileappproto1.ui.viewmodels.OTPVerifyViewModel
import com.giis.mobileappproto1.utils.networkFailureMessage
import com.giis.mobileappproto1.utils.otpFailureMessage
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class OTPVerifyFragment : Fragment() {

    companion object StaticHelper {

    }


    lateinit var viewModel: OTPVerifyViewModel
        @Inject set

    private lateinit var binding: FragmentOTPVerifyBinding
    private lateinit var authorizedCredentialData: LoginAuthCredentialDTO
    lateinit var bottomSheetFragment: BottomSheetDialogFragment
    private lateinit var loadingDialog: StdLoadingDialog
    lateinit var enteredEmail: String
    var prefBool: Boolean = false

    var emailOrMobileChoice: Boolean = true// true for email and false for mobile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.enteredEmail = requireArguments().getString("enteredEmail")!!
        this.prefBool = requireArguments().getBoolean("prefBool")
        this.authorizedCredentialData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable("authCred", LoginAuthCredentialDTO::class.java)!!
        } else {
            requireArguments().getParcelable("authCred")!!
        }
        Log.e(
            "verificationData",
            "LoginAuthCredentialReceived at OTPVerifyFragment: ${authorizedCredentialData.toString()}"
        )
        binding = FragmentOTPVerifyBinding.inflate(inflater, container, false)
        return binding.root
        // return inflater.inflate(R.layout.fragment_o_t_p_verify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Loading Dialog instantiation
        loadingDialog = StdLoadingDialog(requireActivity())

        // Activate send OTP Button on picking a radio Button choice and update its onClickListener Implementation
        binding.choiceGroupRG.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.emailIdChoice.id || checkedId == binding.mobileNumberChoice.id) {
                binding.sendOTPButton.isEnabled = true
                val checkedRadioButton = group.findViewById<RadioButton>(checkedId)
                val checkedText = checkedRadioButton.text.toString()
                Log.e("verificationData", "$checkedId and $checkedText")
                this.emailOrMobileChoice = checkedText == "Email ID"
                Log.e("verificationData", "${this.emailOrMobileChoice}")
                binding.sendOTPButton.setOnClickListener(
                    SendOtpClickListener(
                        viewModel = this@OTPVerifyFragment.viewModel,
                        loadingDialog = this@OTPVerifyFragment.loadingDialog,
                        requestLoginOTPDTO = RequestLoginOTPDTO(
                            isEmail = this@OTPVerifyFragment.emailOrMobileChoice,
                            userId = authorizedCredentialData.userId,
                            userName = authorizedCredentialData.userName
                        ),
                        otpFragmentContext = this@OTPVerifyFragment, // sending the context of OTPVerifyFragment
                        blockFun = this@OTPVerifyFragment::onCompleteVerification
                    )
                )
            }
        }

        // Initial clickListener Implementation (is overridden later)
        binding.sendOTPButton.setOnClickListener {
            Log.e("verificationData", "OnClick Default Implementation executed - Check For Errors")
        }

    }

    private fun onCompleteVerification() {
        binding.emailIdChoice.visibility = View.INVISIBLE
        binding.mobileNumberChoice.visibility = View.INVISIBLE
        binding.sendOTPButton.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // To Stop playing anim if still playing
        if (loadingDialog.isShowing) {
            loadingDialog.cancel()
        }
    }
}

