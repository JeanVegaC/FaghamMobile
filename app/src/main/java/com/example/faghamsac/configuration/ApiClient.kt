package com.example.faghamsac.configuration

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    val gson = GsonBuilder()
        .setLenient()  // Permite respuestas mal formateadas
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://tefacturo.pe/") // Reemplaza con tu URL base
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}
