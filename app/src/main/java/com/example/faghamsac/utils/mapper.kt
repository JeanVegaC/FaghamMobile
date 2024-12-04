package com.example.faghamsac.utils

import android.util.Log
import com.example.faghamsac.modules.invoice.model.DatosDocumento
import com.example.faghamsac.modules.invoice.model.DetalleDocumento
import com.example.faghamsac.modules.invoice.model.Invoice
import com.example.faghamsac.modules.invoice.model.InvoiceItem
import com.example.faghamsac.modules.invoice.model.InvoicePagination
import com.example.faghamsac.modules.invoice.model.InvoiceRequest
import com.example.faghamsac.modules.invoice.model.Product
import com.example.faghamsac.modules.invoice.model.Quotation
import com.example.faghamsac.modules.invoice.model.QuotationDetalle
import com.example.faghamsac.modules.invoice.model.QuotationPagination
import com.example.faghamsac.modules.invoice.model.Receptor
import com.example.faghamsac.modules.invoice.model.ReceptorInvoice
import java.math.BigDecimal

fun mapPayloadToInvoiceRequest(clientName: String, clientRuc: String, invoiceType: String, productsList: List<Product>): InvoiceRequest {

    val detalleDocumento = productsList.map { product ->
        DetalleDocumento(
            codigoProducto = product.code,
            codigoProductoSunat = null,
            descripcion = product.name,
            numeroOrden = 0,
            tipoProducto = 1,
            cuenta = "",
            tipoAfectacion = "GRAVADO_OPERACION_ONEROSA",
            unidadMedida = "UNIDAD_BIENES",
            unidadMedidaNombre = "UNIDAD_BIENES",
            cantidad = product.cantidad,
            valorVentaUnitarioItem = product.price,
            descuento = null,
            grupoUnidadMedida = null,
            descripcionAdicional = emptyList()
        )
    }

    return InvoiceRequest(
        receptor = ReceptorInvoice(
            nombreLegal = clientName,
            numeroDocumentoIdentidad = clientRuc,
        ),
        detalleDocumento = detalleDocumento
    )
}
