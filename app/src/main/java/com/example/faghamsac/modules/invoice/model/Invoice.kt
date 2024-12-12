package com.example.faghamsac.modules.invoice.model

data class Invoice(
    val accepted: Boolean = true,
    val asignado: String? = null,
    val branchName: String? = null,
    val chargedDetraction: Boolean = false,
    val coCentro: String? = null,
    val coDistribuidor: String? = null,
    val codigoAlmacen: String? = null,
    val codigoDetraccion: String? = null,
    val codigoPercepcion: String? = null,
    val codigoPlantilla: String = "01",
    val codigobarras: String? = null,
    val condicionPago: String = "CONTADO",
    val correlativoPago: String? = null,
    val cuotas: List<Any> = emptyList(),
    val descuentoGlobal: Int = 0,
    val detalle: List<InvoiceItem> = emptyList(),
    val detalleWithDistinctProducts: List<InvoiceItem> = emptyList(),
    val detraccion: String? = null,
    val direccionEmisor: String? = null,
    val emailEmisor: String = "",
    val email_emisor: String = "",
    val emailReceptor: String = "",
    val estadoDespacho: String? = null,
    val estadoCobro: String = "POR_COBRAR",
    val estadoComprobante: Int = 1,
    val estadoCondicion: String? = null,
    val estadoConformidad: String = "REGISTRADO",
    val estadoDeclaracion: String? = null,
    val estadoEntrega: Int = 0,
    val estadoPago: String = "POR_PAGAR",
    val estadoSunat: String = "1",
    val expirationDays: String? = null,
    val factorCambio: Double? = null,
    val fechaCondicion: String? = null,
    val fechaCreacion: String = "",
    val fechaDeclaracion: String = "",
    val fechaEmision: String = "",
    val fecha_emision: String = "",
    val fechaEntrega: String? = null,
    val fechaLectura: String? = null,
    val fechaModificacion: String = "",
    val fechaPago: String? = null,
    val fechaProgramacion: String? = null,
    val fechaVencimiento: String? = null,
    val firma: String = "",
    val fise: String? = null,
    val flexString01: String? = null,
    val formaPago: String = "CONTADO",
    val fullyDispatched: Boolean = false,
    val glosa: String? = null,
    val hash: String = "",
    val igv: Double = 0.0,
    val isc: Double = 0.0,
    val medioPago: String = "EFECTIVO",
    val moneda: String = "PEN",
    val montoPago: Double? = null,
    val montoRetenido: Double? = null,
    val motivo: String? = null,
    val motivoRechazo: String? = null,
    val nombre: String = "",
    val nombreAlmacen: String? = null,
    val nombreVendedor: String? = null,
    val numberQuotas: String? = null,
    val numero: Int = 0,
    val numeroErp: String? = null,
    val numeroIntentoDeclaracion: Int = 0,
    val numeroOrden: Int = 0,
    val numeroPago: String? = null,
    val observacion: String? = null,
    val ordenCompra: String? = null,
    val otrosCargos: Double = 0.0,
    val payedDetraction: Boolean = false,
    val percepcion: String? = null,
    val preciosIncluyenIgv: Int = 0,
    val puntoEmisor: String = "0000",
    val razonSocialEmisor: String = "",
    val razon_social_emisor: String = "",
    val razonSocialReceptor: String = "",
    val razon_social_receptor: String = "",
    val receptorId: String? = null,
    val regimenRetencion: String? = null,
    val respuestaDeclaracion: String = "",
    val rucEmisor: Long = 0L,
    val ruc_emisor: Long = 0L,
    val rucReceptor: String = "",
    val ruc_receptor: String = "",
    val scheduledDueDate: String? = null,
    val serie: String = "",
    val serieNumeroReferencia: String? = null,
    val subtotal: Double = 0.0,
    val tasaDetraccion: Double? = null,
    val tasaFise: Double? = null,
    val tasaPercepcion: Double? = null,
    val tasaRetencion: Double? = null,
    val tipoComprobante: String = "",
    val tipo_comprobante: String = "",
    val tipoDocReceptor: String = "1",
    val tipoIntegracion: Int = 9,
    val tipoOperacion: String = "VENTA_INTERNA",
    val total: Double = 0.0,
    val totalAnticipoExonerado: Double = 0.0,
    val totalAnticipoExportacion: Double = 0.0,
    val totalAnticipoGravado: Double = 0.0,
    val totalAnticipoInafecto: Double = 0.0,
    val totalIgvAnticipo: Double = 0.0,
    val totalPendingCharge: Double = 0.0,
    val totalPendingPayment: Double = 0.0,
    val totalAsignado: Double? = null,
    val totalExoneradas: Double = 0.0,
    val totalGratuitas: Double = 0.0,
    val totalGravadas: Double = 0.0,
    val totalIcbper: Double = 0.0,
    val totalInafectas: Double = 0.0,
    val totalOtrosCargos: Double = 0.0,
    val totalOtrosImpuestos: Double = 0.0,
    val usuarioCreacion: String = "",
    val usuarioEmisor: String = "",
    val usuarioModificacion: String? = null,
    val usuarioPago: String? = null,
    val vendedor: String = ""
)
