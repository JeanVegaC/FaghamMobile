package com.example.faghamsac.modules.invoice.model

data class Product(
    val code: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0
) {
    constructor() : this("", "", 0.0, 0)
}
