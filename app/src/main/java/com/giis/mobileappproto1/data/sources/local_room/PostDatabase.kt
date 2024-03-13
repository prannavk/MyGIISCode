package com.giis.mobileappproto1.data.sources.local_room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.giis.mobileappproto1.data.models.FeedPostDTO

@Database(entities = [FeedPostDTO::class], version = 1)
@TypeConverters(Convertors::class)
abstract class PostDatabase : RoomDatabase() {

    abstract fun savePostDAO(): SavePostDAO

}