package com.example.faghamsac.modules.invoice.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class InvoiceRequest(
    val anticipos: List<Any> = emptyList(),
    val close2u: Close2U = Close2U(),
    val datosDocumento: DatosDocumento = DatosDocumento(),
    val descuentoGlobal: Any? = null,
    val detalleDocumento: List<DetalleDocumento> = listOf(DetalleDocumento()),
    val detraccion: Any? = null,
    val emisor: Emisor = Emisor(),
    val factorCambio: Any? = null,
    val informacionAdicional: InformacionAdicional = InformacionAdicional(),
    val otrosCargos: Any? = null,
    val percepcion: Any? = null,
    val receptor: ReceptorInvoice = ReceptorInvoice(),
    val referencias: Referencias = Referencias(),
    val sector: Sector = Sector(),
    val facturaGuia: Any? = null,
    val idCondicionPago: String = "CONT",
    val invoiceQuotesParams: InvoiceQuotesParams = InvoiceQuotesParams(),
    val retencion: Any? = null
)

data class Close2U(
    val numero: String? = null,
    val tipoIntegracion: String = "ENLINEA",
    val tipoPlantilla: String = "01",
    val tipoRegistro: String = "PRECIOS_SIN_IGV"
)

data class DatosDocumento(
    val fechaEmision: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val fechaVencimiento: String? = null,
    val formaPago: String = "CONTADO",
    val medioPago: String = "EFECTIVO",
    val glosa: String? = null,
    val horaEmision: String? = null,
    val moneda: String = "PEN",
    val numero: String? = null,
    val ordencompra: String? = null,
    val puntoEmisor: String? = null,
    val serie: String = "FFA1",
    val condicionPago: String = "CONTADO",
    val almacen: String = "Almacén Principal",
    val codigoAlmacen: String = "001"
)

data class DetalleDocumento(
    val codigoProducto: String = "001",
    val codigoProductoSunat: String? = null,
    val descripcion: String = "Producto A",
    val numeroOrden: Int = 0,
    val tipoProducto: Int = 1,
    val cuenta: String = "",
    val tipoAfectacion: String = "GRAVADO_OPERACION_ONEROSA",
    val unidadMedida: String = "UNIDAD_BIENES",
    val unidadMedidaNombre: String = "UNIDAD_BIENES",
    val cantidad: Int = 1,
    val valorVentaUnitarioItem: Double = 40.0,
    val descuento: Any? = null,
    val grupoUnidadMedida: Any? = null,
    val descripcionAdicional: List<Any> = emptyList()
)

data class Emisor(
    val correo: String = "andrearocioarroyo@hotmail.com",
    val tipoDocumentoIdentidad: String = "RUC",
    val numeroDocumentoIdentidad: String = "10256228233",
    val nombreLegal: String = "ARROYO ELESCANO ANDREA DEL ROCIO",
    val nombreComercial: String = "ARROYO ELESCANO ANDREA DEL ROCIO"
)

data class InformacionAdicional(
    val tipoOperacion: String = "VENTA_INTERNA",
    val vendedor: String = "ANDREA DEL ROCIO ARROYO ELESCANO",
    val coVendedor: String = "andrearocioarroyo@gmail.com"
)

data class ReceptorInvoice(
    val correo: String = "jhonivegacasimiro@gmail.com",
    val correoCopia: String = "",
    val domicilioFiscal: DomicilioFiscal = DomicilioFiscal(),
    val nombreComercial: String? = null,
    val nombreLegal: String? = null,
    val numeroDocumentoIdentidad: String = "",
    val tipoDocumentoIdentidad: String = "RUC"
)

data class DomicilioFiscal(
    val departamento: String? = null,
    val direccion: String = "",
    val distrito: String? = null,
    val pais: String? = null,
    val provincia: String? = null,
    val ubigeo: String? = null,
    val urbanizacion: String? = null
)

data class Referencias(
    val documentoReferenciaList: List<Any> = emptyList()
)

data class Sector(
    val tipoTotalDescuentos: Any? = null,
    val tipoCargo: Any? = null
)

data class InvoiceQuotesParams(
    val custom: Boolean = false,
    val quotesNumber: String = "1",
    val daysQuote: String = "30",
    val wordChange: Boolean = false
)