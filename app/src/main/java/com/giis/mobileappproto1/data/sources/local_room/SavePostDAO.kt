package com.giis.mobileappproto1.data.sources.local_room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.giis.mobileappproto1.data.models.FeedPostDTO

@Dao
interface SavePostDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun persistThisFeedPost(fp: FeedPostDTO)

    @Query("SELECT * FROM PersistedPosts")
    fun fetchAllSavedPosts(): LiveData<List<FeedPostDTO>>

    @Delete
    suspend fun deleteThisFeedPost(fp: FeedPostDTO)

}