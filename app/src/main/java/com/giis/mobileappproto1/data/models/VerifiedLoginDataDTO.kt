package com.giis.mobileappproto1.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerifiedLoginDataDTO(
    @SerializedName("token") var token: String? = null,
    @SerializedName("userName") var userName: String? = null,
    @SerializedName("validaty") var validity: Int? = null,
    @SerializedName("refreshToken") var refreshToken: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("guidId") var guidId: String? = null,
    @SerializedName("expiredTime") var expiredTime: String? = null,
    @SerializedName("role") var role: String? = null,
    @SerializedName("user_role") var user_role: List<UserRoleDTO> = arrayListOf(),
    @SerializedName("nameOfPerson") var nameOfPerson: String? = null,
    @SerializedName("brandId") var brandId: Int? = null,
    @SerializedName("campusId") var campusId: Int? = null,
    @SerializedName("brandName") var brandName: String? = null,
    @SerializedName("campusName") var campusName: String? = null,
    @SerializedName("profilePicture") var profilePicture: String? = null,
    // Newly Added
    @SerializedName("brandLogoUrl"      ) var brandLogoUrl      : String?             = null,
    @SerializedName("campusAddress"     ) var campusAddress     : CampusAddressDTO?      = CampusAddressDTO(),
    @SerializedName("campusAddressJson" ) var campusAddressJson : String?             = null

) : Parcelable

