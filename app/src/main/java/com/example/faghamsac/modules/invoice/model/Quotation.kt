package com.example.faghamsac.modules.invoice.model

import java.math.BigDecimal

data class Quotation(
    val fechaEmision: String = "",
    val fechaVencimiento: String = "",
    val numero: Int = 0,
    val rucReceptor: String = "",
    val razonSocialReceptor: String = "",
    val moneda: String = "",
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val igv: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal = BigDecimal.ZERO,
    val estado: String = "",
    val formaPago: String = "",
    val almacen: String? = null,
    val descripcion: String? = null,
    val estadoPedido: String? = null,
    val fechaDelivery: String? = null,
    val horaDelivery: String? = null,
    val nombreContacto: String? = null,
    val telefonoContacto: String? = null,
    val direccionEnvio: String? = null,
    val ubigeoDistrito: String? = null,
    val instruccionEnvio: String? = null,
    val costoEnvio: BigDecimal? = null,
    val associated: Boolean = false,
    val vendedor: String = "",
    val receptor: Receptor = Receptor(), // Suponiendo que Receptor tiene valores por defecto también
    val detallePedido: String? = null,
    val observaciones: String? = null,
    val idCondicionPago: String? = null,
    val totalOtrosCargos: BigDecimal? = null,
    val detalle: List<QuotationDetalle> = emptyList(), // Lista vacía por defecto
    val repartidor: String? = null,
    val comprobanteRelacionado: String? = null,
    val tieneDelivery: Boolean = false
)


data class Receptor(
    val numeroDocumento: String = "10759302244",
    val tipoDocumento: String = "01",
    val direccion: String? = null,
    val email: String? = null
)



data class UnidadAlternativa(
    val unidadAlternativaCantidadEquivalente: BigDecimal?,
    val codigoUnidadAlternativa: String?,
    val unidadBaseCantidadEquivalente: BigDecimal?,
    val precioUnidadAlternativa: BigDecimal?
)