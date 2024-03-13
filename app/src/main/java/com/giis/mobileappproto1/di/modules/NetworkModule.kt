package com.giis.mobileappproto1.di.modules

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.giis.mobileappproto1.data.sources.remote_api.ServiceBuilder
import com.giis.mobileappproto1.data.sources.remote_api.GenericService
import com.giis.mobileappproto1.data.sources.remote_api.LoginService
import com.giis.mobileappproto1.data.sources.remote_api.MediaService
import com.giis.mobileappproto1.data.sources.remote_api.RetrofitAdapterFactory
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideChuckerInterceptorLogger(@ApplicationContext app: Context): ChuckerInterceptor =
        ChuckerInterceptor(app)

    @Provides
    @Singleton
    fun getOkHttp3(logger2: ChuckerInterceptor): OkHttpClient {
        val okHttpLogger = with(HttpLoggingInterceptor()) {
            level = HttpLoggingInterceptor.Level.BODY
            this
        }

        return OkHttpClient.Builder().apply {
            connectTimeout(45, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            addInterceptor(logger2)
            this
        }.build()
    }

    @Provides
    @Singleton
    @RetrofitInstance1
    fun getRetrofit1(okHttpClient: OkHttpClient): Retrofit = with(Retrofit.Builder()) {
        baseUrl(ServiceBuilder.URL)
        addCallAdapterFactory(RetrofitAdapterFactory())
        addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        client(okHttpClient)
        build()
    }

    @Provides
    @Singleton
    fun buildLoginService(@RetrofitInstance1 retrofit: Retrofit): LoginService =
        retrofit.create(LoginService::class.java)

    @Provides
    @Singleton
    @RetrofitInstance2
    fun getRetrofit2(okHttpClient: OkHttpClient): Retrofit = with(Retrofit.Builder()) {
        baseUrl(ServiceBuilder.UPLOAD_BASE_URL)
        addCallAdapterFactory(RetrofitAdapterFactory())
        addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        client(okHttpClient)
        build()
    }

    @Provides
    @Singleton
    fun buildGenericService(@RetrofitInstance2 retrofit: Retrofit): GenericService =
        retrofit.create(GenericService::class.java)

    @Provides
    @Singleton
    fun buildMediaService(@RetrofitInstance2 retrofit: Retrofit): MediaService =
        retrofit.create(MediaService::class.java)


}