package com.example.coroutines.main

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
internal class MainActivityModule {

    @Provides
    fun provideMainActivityRepository(api:MainActivityApi): MainActivityRepository = MainActivityRepositoryImpl(api)

    @Provides
    fun provideEditProfileApi(retrofit: Retrofit): MainActivityApi = retrofit.create(MainActivityApi::class.java)
}