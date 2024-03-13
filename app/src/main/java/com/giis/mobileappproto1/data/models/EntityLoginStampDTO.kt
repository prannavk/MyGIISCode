package com.giis.mobileappproto1.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserLoginStamp")
data class EntityLoginStampDTO(

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val nameOfLoggedPerson: String,
    val loggedPersonEmail: String,
    val loginDateTime: java.util.Date,
    val otpVerificationApproach: Boolean
)

