package com.example.faghamsac.modules.invoice.services

import com.example.faghamsac.modules.login.model.TokenRequest
import com.example.faghamsac.modules.login.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TokenService {
    @POST("tokenapi/secure/v2/login/token")
    suspend fun getToken(@Body request: TokenRequest): Response<TokenResponse>

}