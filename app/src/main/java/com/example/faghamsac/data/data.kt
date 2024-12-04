package com.example.faghamsac.data

import com.example.faghamsac.modules.invoice.model.Product

data class Product(val code: String, val name: String, val quantity: Int, val price: Double)

// Lista de productos (puedes ajustar esto con tus datos)
val products = listOf(
    Product("001", "Producto A", 100.0, 0),
    Product("002", "Producto B", 200.0, 0),
    Product("003", "Producto C", 300.0, 0),
    // Agrega más productos según sea necesario
)
