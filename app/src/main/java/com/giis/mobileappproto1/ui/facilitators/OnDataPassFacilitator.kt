package com.giis.mobileappproto1.ui.facilitators

import com.giis.mobileappproto1.data.models.LoginAuthCredentialDTO

interface OnDataPassFacilitator {
    fun dataFacilitate(data: LoginAuthCredentialDTO, enteredEmail: String, preferenceFlag: Boolean)
}