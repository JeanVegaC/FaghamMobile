package com.example.faghamsac.configuration

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://tefacturo.pe/") // Reemplaza con tu URL base
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}
