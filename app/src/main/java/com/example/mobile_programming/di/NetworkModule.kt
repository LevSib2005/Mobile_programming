package com.example.mobile_programming.di

import com.example.mobile_programming.data.api.CbrApi
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://www.cbr.ru/")
            .build()
    }
    single { get<Retrofit>().create(CbrApi::class.java) }
}