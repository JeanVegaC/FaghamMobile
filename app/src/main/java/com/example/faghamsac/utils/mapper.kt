package com.example.faghamsac.utils

import android.util.Log
import com.example.faghamsac.modules.invoice.model.Invoice
import com.example.faghamsac.modules.invoice.model.InvoiceItem
import com.example.faghamsac.modules.invoice.model.InvoicePagination

fun mapApiResponseToInvoices(apiResponse: InvoicePagination): List<Invoice> {
    return apiResponse.result.map { result ->

        // Mapea el detalle, que es un array de objetos dentro de cada objeto de result
        val detalle = result.detalle.map { item ->
            InvoiceItem(
                codpro = item.codpro,
                cantidad = item.cantidad,
                nombreProducto = item.nombreProducto,
                precioVenta = item.precioVenta,
                unidadMedida = item.unidadMedida
            )
        }

        Log.d("result", "result ${result}")

        // Crea una instancia de Invoice con los valores de result y el detalle mapeado
        Invoice(
            serie = result.serie,
            numero = result.numero,
            moneda = result.moneda,
            subtotal = result.subtotal,
            igv = result.igv,
            total = result.total,
            detalle = detalle, // Aquí se asigna el detalle mapeado
            rucEmisor = result.ruc_emisor,
            razonSocialEmisor = result.razon_social_emisor,
            razonSocialReceptor = result.razon_social_receptor,
            fechaEmision = result.fecha_emision
        )
    }
}
