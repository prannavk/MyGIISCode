package com.giis.mobileappproto1.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginAuthCredentialDTO(
    val userName: String,
    val userId: Int
) : Parcelable