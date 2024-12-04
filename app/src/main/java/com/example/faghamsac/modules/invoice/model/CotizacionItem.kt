import java.math.BigDecimal


data class CotizacionItem(
    val codigoProducto: String,
    val nombreProducto: String? = null,
    val caracteristicas: String? = null,
    val cantidad: BigDecimal,
    val isc: BigDecimal? = null,
    val tipoAfectacion: String,
    val precioVenta: BigDecimal,
    val valorUnitario: BigDecimal,
    val precioUnitario: BigDecimal,
    val descuentoUnitario: BigDecimal? = null,
    val valorVenta: BigDecimal,
    val descuentoEsPorcentaje: Boolean = false,
    val usaSerie: String? = null,
    val usaLote: String? = null,
    val stockActual: BigDecimal? = null,
    val unidadMedida: String? = null,
    val unidadMedidaNombre: String? = null,
    val tipoProducto: String? = null,
    val flagPaquete: Int? = null,
    val alternativeUnit: CotizationAlternativeUnitMeasure? = null,
    val productImageBytes: ByteArray? = null
)

data class CotizationAlternativeUnitMeasure(
    val alternativeUnitEquivalentQuantity: BigDecimal = BigDecimal.ZERO,
    val alternativeUnitCode: String = "",
    val baseUnitEquivalentQuantity: BigDecimal = BigDecimal.ZERO,
    val alternativeUnitPrice: BigDecimal = BigDecimal.ZERO
)
