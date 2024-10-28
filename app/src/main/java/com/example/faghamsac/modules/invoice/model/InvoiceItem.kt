package com.example.faghamsac.modules.invoice.model

data class InvoiceItem(
    val productCode: String,
    val productName: String,
    val quantity: Int,
    val price: Double
) {
    val total: Double
        get() = quantity * price
}
