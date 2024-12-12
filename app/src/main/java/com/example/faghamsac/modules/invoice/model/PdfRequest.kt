package com.example.faghamsac.modules.invoice.model

data class PdfRequest(
    val emisor: String,
    val numero: Int,
    val serie: String,
    val tipoComprobante: String
)
