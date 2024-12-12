package com.example.faghamsac.modules.login.model

data class TokenResponse(
    val c2uToken: String,
    val expiresIn: Int,
    val type: String
)
