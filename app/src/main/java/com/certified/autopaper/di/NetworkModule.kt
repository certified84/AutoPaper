package com.certified.autopaper.di

import com.certified.autopaper.data.network.PaystackApiService
import com.certified.autopaper.data.repository.PaystackRepository
import com.certified.autopaper.util.ApiErrorUtil
import com.certified.autopaper.util.Config
import com.certified.autopaper.util.Config.PSTK_BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun providePaystackRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(PSTK_BASE_URL)
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    fun providePaystackApiService(retrofit: Retrofit): PaystackApiService =
        retrofit.create(PaystackApiService::class.java)

    @Provides
    fun providePaystackRepository(apiService: PaystackApiService) =
        PaystackRepository(apiService)

    @Provides
    fun provideApiErrorUtil(retrofit: Retrofit) = ApiErrorUtil(retrofit)
}