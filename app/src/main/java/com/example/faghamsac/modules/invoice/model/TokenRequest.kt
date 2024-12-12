package com.example.faghamsac.modules.invoice.model

data class TokenRequest(
    val mail: String,
    val ruc: String,
    val clave: String,
    val aplicacion: Aplicacion
)

data class Aplicacion(
    val codigo: String
)

data class TokenResponse(
    val type: String,
    val c2uToken: String,
    val expiresIn: Int
)