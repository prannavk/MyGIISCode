package com.giis.mobileappproto1.utils

const val networkCallURL: String =
    "https://syjyg7izf3.execute-api.ap-southeast-1.amazonaws.com/Login/"

const val loginEmailRegex: String = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
const val loginPasswordRegex: String = ".{8,}"
const val verifyLoginSuccessMessage: String = "Now, Please Verify your Credentials"
const val verifyLoginFailureMessage: String =
    "Invalid Credentials, Try Again"

const val genLoginOtpUserName = "aarthi123"
const val genLoginOtpUsedId = 63
const val genOtpFailMessage = "Failure in receiving OTP"

const val sheetTitleEmail = "Verify Your Email ID"
const val sheetTitleMobileNumber = "Verify Your Mobile Number"

const val forgotPasswordToast = "Forgot Password Facility Not Available At the Moment"

const val networkFailureMessage = "Please Check your Network Connections And Try Again"
const val networkFailureMessage2 = "Please Enable Network Connection or Check your Connectivity"

const val otpFailureMessage = "There has been an issue in OTP Generation, Please try again"

const val thanksMessage = "Thank you for using this App"

const val globalTag = "MyLogDebug"

const val createPassResultCode = 11
const val createFailResultCode = 10