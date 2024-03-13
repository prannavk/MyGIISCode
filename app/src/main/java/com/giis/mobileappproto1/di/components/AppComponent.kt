package com.giis.mobileappproto1.di.components

import com.giis.mobileappproto1.GIISApplication
import com.giis.mobileappproto1.di.bindings.ViewModelBindingsClass
import com.giis.mobileappproto1.di.modules.NetworkModule
import com.giis.mobileappproto1.di.modules.SQLiteModule
import com.giis.mobileappproto1.di.modules.UIModule
import dagger.Component
import dagger.Provides
import javax.inject.Singleton

@Component(modules = [NetworkModule::class, SQLiteModule::class, UIModule::class, ViewModelBindingsClass::class])
abstract class AppComponent {

}