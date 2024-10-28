package com.example.faghamsac.modules.invoice.model

data class Invoice(
    val id: String,
    val date: String,
    val clientName: String,
    val clientRuc: String,
    val totalAmount: Double,
    val items: List<InvoiceItem>
)
