package com.example.faghamsac.modules.invoice.model

import java.math.BigDecimal

data class QuotationDetalle(
    val isc: BigDecimal? = BigDecimal.ZERO,
    val numeroItem: Int = 0,
    val codigoProducto: String = "",
    val nombreProducto: String = "",
    val cantidad: BigDecimal = BigDecimal.ZERO,
    val tipoAfectacion: String = "",
    val precioVenta: BigDecimal = BigDecimal.ZERO,
    val valorUnitario: BigDecimal = BigDecimal.ZERO,
    val precioUnitario: BigDecimal = BigDecimal.ZERO,
    val descuentoUnitario: BigDecimal = BigDecimal.ZERO,
    val valorVenta: BigDecimal = BigDecimal.ZERO,
    val descuentoEsPorcentaje: Boolean = false,
    val usaSerie: String? = null,
    val usaLote: String? = null,
    val stockActual: BigDecimal? = null,
    val unidadMedida: String = "",
    val tipoProducto: String? = null,
    val flagPaquete: Boolean = false,
    val unidadAlternativa: UnidadAlternativa? = null
)
