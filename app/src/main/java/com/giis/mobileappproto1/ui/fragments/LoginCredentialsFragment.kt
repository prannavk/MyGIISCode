package com.giis.mobileappproto1.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.giis.mobileappproto1.data.models.LoginDataDTO
import com.giis.mobileappproto1.databinding.FragmentLoginCredentialsBinding
import com.giis.mobileappproto1.ui.dialogs.StdLoadingDialog
import com.giis.mobileappproto1.ui.facilitators.OnDataPassFacilitator
import com.giis.mobileappproto1.utils.forgotPasswordToast
import com.giis.mobileappproto1.utils.loginEmailRegex
import com.giis.mobileappproto1.utils.loginPasswordRegex
import com.giis.mobileappproto1.utils.networkFailureMessage
import com.giis.mobileappproto1.utils.verifyLoginFailureMessage
import com.giis.mobileappproto1.utils.verifyLoginSuccessMessage
import com.giis.mobileappproto1.data.sources.remote_api.Result
import com.giis.mobileappproto1.ui.viewmodels.LoginCredentialsViewModel
import com.giis.mobileappproto1.utils.globalTag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

@AndroidEntryPoint
class LoginCredentialsFragment : Fragment() {

    companion object {

        fun isEmailValid(email: String): Boolean =
            email.matches(loginEmailRegex.toRegex())

        fun isPasswordValid(pass: String): Boolean = pass.matches(loginPasswordRegex.toRegex())

        var progressDialogJob: Job? = null

        fun showProgressAnim(loadingDialog: StdLoadingDialog, long: Long) {
            loadingDialog.show()
            progressDialogJob = CoroutineScope(Dispatchers.Main).launch {
                delay(long)
                dismissProgressDialog(loadingDialog)
            }
        }

        fun dismissProgressDialog(loadingDialog: StdLoadingDialog) {
            loadingDialog.cancel()
            progressDialogJob?.cancel()
        }

    }

    @Inject
    lateinit var viewModel: LoginCredentialsViewModel
    private lateinit var binding: FragmentLoginCredentialsBinding
    private lateinit var loadingDialog: StdLoadingDialog
    private lateinit var onDataPassFacilitator: OnDataPassFacilitator
    private lateinit var enteredLoginData: LoginDataDTO
    private var preferenceFlag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginCredentialsBinding.inflate(inflater, container, false)
        return binding.root
        // return inflater.inflate(R.layout.fragment_login_credentials, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initializing onDataPassFacilitator
        onDataPassFacilitator = requireActivity() as OnDataPassFacilitator

        // Setting the underline below and click listener to forgot password edit text
        val fpText = "Forgot Passsword?"
        val spannableString = SpannableString(fpText)
        spannableString.setSpan(UnderlineSpan(), 0, fpText.length, 0)
        binding.forgotPasswordTextView.text = spannableString
        binding.forgotPasswordTextView.setOnClickListener {
            Toast.makeText(
                requireActivity(),
                forgotPasswordToast,
                Toast.LENGTH_SHORT
            ).show()
        }

        // Text Watcher to activate Login Button on entering Login Details
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val emailIdInput: String = binding.emailIdCredentialET.text.toString().trim()
                val pwdInput: String = binding.passwordCredentialET.text.toString().trim()
                if (emailIdInput.isNotEmpty() && pwdInput.isNotEmpty()) {
                    // Below functions implemented using Regex
                    if (isEmailValid(emailIdInput) && isPasswordValid(pwdInput)) {
                        binding.LoginButton.isEnabled = true
                    }
                }
            }
        }

        binding.rememberMeBox.setOnCheckedChangeListener { _, isChecked ->
            this.preferenceFlag = isChecked
        }

        binding.emailIdCredentialET.addTextChangedListener(textWatcher)
        binding.passwordCredentialET.addTextChangedListener(textWatcher)

        // loading Dialog Instantiation
        loadingDialog = StdLoadingDialog(requireActivity())

        // loginBtn onclick listener to set the Credentials Data with observer code
        binding.LoginButton.setOnClickListener {
            val emailIdInput: String = binding.emailIdCredentialET.text.toString().trim()
            val pwdInput: String = binding.passwordCredentialET.text.toString().trim()
            // first step of Network call
            this.enteredLoginData = LoginDataDTO(emailIdInput, pwdInput)
            viewModel.sendCredentialsForVerification(this.enteredLoginData)
            // start loading anim
            showProgressAnim(this.loadingDialog, 10000L)
            // last step of Network call
            viewModel.authId.observe(viewLifecycleOwner) {
                if (this.loadingDialog.isShowing) {
                    this.loadingDialog.cancel()
                }
                when (it) {
                    is Result.Success -> {
                        Toast.makeText(
                            requireActivity(),
                            verifyLoginSuccessMessage,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        it.data?.let { it1 ->
                            onDataPassFacilitator.dataFacilitate(
                                it1,
                                this.enteredLoginData.email,
                                this.preferenceFlag
                            ).also { Log.e(globalTag, "checked? ${this.preferenceFlag}") }
                        }
                            ?: run {
                                Log.e(
                                    "verificationData",
                                    "Login Failed due to unexpected Issue"
                                )
                                it
                            }
                    }

                    is Result.Failure -> {
                        Toast.makeText(
                            requireActivity(),
                            verifyLoginFailureMessage,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    is Result.NetworkError -> {
                        Toast.makeText(requireActivity(), networkFailureMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        // binding.rememberMeBox.on
//        getEncryptedSharedPreferences().edit()
//            .putString("username", "He")
//            .putLong()

    }

//    private fun getEncryptedSharedPreferences(): SharedPreferences {
//        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
//        return EncryptedSharedPreferences.create(
//            "secured_prefs",
//            masterKeyAlias,
//            requireActivity(),
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        // To Stop playing anim if still playing
        if (this.loadingDialog.isShowing) {
            this.loadingDialog.cancel()
        }
    }

}