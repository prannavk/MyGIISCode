package com.giis.mobileappproto1.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CredentialData")
data class EntityLoginCredentialDTO(

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val emailId: String,
    val password: String

)


