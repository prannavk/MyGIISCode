package com.giis.mobileappproto1.data.models

data class ObtainedOTPDTO(
    val emailOrPhone: String,
    val loginOpt: String,
    val userId: Int,
    val userName: String
)