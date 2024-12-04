package com.example.faghamsac.utils

import android.util.Log
import com.example.faghamsac.modules.invoice.model.Invoice
import com.example.faghamsac.modules.invoice.model.InvoiceItem
import com.example.faghamsac.modules.invoice.model.InvoicePagination
import com.example.faghamsac.modules.invoice.model.Quotation
import com.example.faghamsac.modules.invoice.model.QuotationDetalle
import com.example.faghamsac.modules.invoice.model.QuotationPagination
import com.example.faghamsac.modules.invoice.model.Receptor

fun mapApiResponseToInvoices(apiResponse: QuotationPagination): List<Quotation> {
    return apiResponse.result.map { result ->
        
        val detalle = result.detalle?.map { item ->
            QuotationDetalle(
                codigoProducto = item.codigoProducto, 
                cantidad = item.cantidad,
                nombreProducto = item.nombreProducto,
                precioVenta = item.precioVenta,
                unidadMedida = item.unidadMedida
            )
        } ?: emptyList()

        Log.d("result", "result: $result")

        val receptor = result.receptor?.let {
            Receptor(
                numeroDocumento = it.numeroDocumento,
                tipoDocumento = it.tipoDocumento,
                direccion = it.direccion,
                email = it.email
            )
        } ?: Receptor() 

        Quotation(
            numero = result.numero,
            moneda = result.moneda,
            subtotal = result.total,
            igv = result.igv,
            total = result.total,
            detalle = detalle, // Aquí se asigna el detalle mapeado
            rucReceptor = receptor.numeroDocumento, // Cambiado a "rucReceptor"
            razonSocialReceptor = "Jhean Carlos Vega", // Asigna un valor por defecto o desde otro campo si aplica
            fechaEmision = result.fechaEmision
        )
    }
}
