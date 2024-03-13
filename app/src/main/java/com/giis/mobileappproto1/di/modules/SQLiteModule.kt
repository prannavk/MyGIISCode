package com.giis.mobileappproto1.di.modules

import android.content.Context
import androidx.room.Room
import com.giis.mobileappproto1.data.repositories.AppRepository
import com.giis.mobileappproto1.data.sources.local_room.CredentialDatabase
import com.giis.mobileappproto1.data.sources.local_room.LoginCredentialDao
import com.giis.mobileappproto1.data.sources.local_room.PostDatabase
import com.giis.mobileappproto1.data.sources.local_room.SavePostDAO
import com.giis.mobileappproto1.data.sources.remote_api.GenericService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SQLiteModule {

    @Provides
    @Singleton
    fun provideCredentialDatabase(
        @ApplicationContext app: Context
    ): CredentialDatabase {
        return Room.databaseBuilder(app, CredentialDatabase::class.java, "AuthDB")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideLoginCredentialDao(database: CredentialDatabase): LoginCredentialDao =
        database.loginCredentialDao()

    @Provides
    @Singleton
    fun providePostDatabase(@ApplicationContext app: Context): PostDatabase {
        return Room.databaseBuilder(
            app,
            PostDatabase::class.java,
            "GIISDB"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSavePostDAO(postDatabase: PostDatabase): SavePostDAO = postDatabase.savePostDAO()

    @Provides
    @Singleton
    fun provideAppRepository(apiInterface: GenericService, postDAO: SavePostDAO) =
        AppRepository(apiEndPoint = apiInterface, postsDao = postDAO)

}

//@Retention(AnnotationRetention.RUNTIME)
//@Qualifier
//annotation class ApplicationScope

//    @ApplicationScope
//    @Provides
//    @Singleton
//    fun provideApplicationScope() = CoroutineScope(SupervisorJob())