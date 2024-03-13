package com.giis.mobileappproto1.data.models

data class RequestLoginOTPDTO(
    val isEmail: Boolean,
    val userId: Int,
    val userName: String
)