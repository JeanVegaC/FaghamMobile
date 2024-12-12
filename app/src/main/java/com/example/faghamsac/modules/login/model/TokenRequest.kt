package com.example.faghamsac.modules.login.model

data class TokenRequest(
    val mail: String,
    val ruc: String,
    val clave: String,
    val aplicacion: Aplicacion
)

data class Aplicacion(
    val codigo: String
)
