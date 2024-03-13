package com.giis.mobileappproto1.di.modules

import com.giis.mobileappproto1.GIISApplication
import com.giis.mobileappproto1.di.bindings.ViewModelFactory
import com.giis.mobileappproto1.ui.fragments.HomeFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UIModule {

//    @Provides
//    @Singleton
//    fun provideHomeFragment(): HomeFragment = HomeFragment()

    @Provides
    @Singleton
    fun provideApplication(): GIISApplication = GIISApplication()

}