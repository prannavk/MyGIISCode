package com.giis.mobileappproto1.data.sources.local_room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.giis.mobileappproto1.data.models.EntityLoginCredentialDTO
import com.giis.mobileappproto1.data.models.EntityLoginStampDTO

@Dao
interface LoginCredentialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoginCredential(lg: EntityLoginCredentialDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoginStamp(stamp: EntityLoginStampDTO)

    @Query("SELECT * FROM UserLoginStamp")
    fun fetchAllLoginStamps(): LiveData<List<EntityLoginStampDTO>>

    @Query("SELECT * FROM CredentialData")
    fun fetchAllCredentialData(): LiveData<List<EntityLoginCredentialDTO>>

}