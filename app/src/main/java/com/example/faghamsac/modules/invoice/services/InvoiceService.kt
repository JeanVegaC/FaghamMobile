package com.example.faghamsac.modules.invoice.services

import com.example.faghamsac.modules.invoice.model.InvoicePagination
import com.example.faghamsac.modules.invoice.model.InvoiceRequest
import com.example.faghamsac.modules.invoice.model.PdfRequest
import com.example.faghamsac.modules.invoice.model.QuotationPagination
import com.example.faghamsac.modules.invoice.model.Respuesta
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface InvoiceService {

    @GET("comprobantesapi/comprobante/emisor/{ruc_emisor}/comprobantes")
    suspend fun getInvoices(
        @Header("Authorization") authHeader: String,
        @Path("ruc_emisor") rucEmisor: String, // Agregar el ruc_emisor como parámetro de la ruta
        @Query("first") first: Int,
        @Query("rows") rows: Int,
        @Query("fecha_emision_inicio") fechaEmisionInicio: String,
        @Query("fecha_emision_fin") fechaEmisionFin: String,
        @Query("usuario_creacion") usuarioCreacion: String,
        @Query("fecha_inicio") fechaInicio: String,
        @Query("fecha_fin") fechaFin: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): InvoicePagination

    @GET("comprobantesapi/cotizacion")
    suspend fun getQuotation(
        @Header("Authorization") authHeader: String,
        @Query("first") first: Int,
        @Query("rows") rows: Int,
        @Query("fecha_inicio") fechaInicio: String,
        @Query("fecha_fin") fechaFin: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("codigo_sucursal") codigoSucursal: String
    ): QuotationPagination

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "Accept-Language: en-US",
        "Connection: keep-alive"
    )
    @POST("comprobantesapi/cotizacion")
    suspend fun emitCotizacion(@Body cotizacion: String, @Header("Authorization") token: String): Response<Respuesta>

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "Accept-Language: en-US",
        "Connection: keep-alive"
    )
    @PUT("/apiemisor/invoice2u/integracion/{tipo}")
    suspend fun emitInvoice(
        @Body request: InvoiceRequest,
        @Path("tipo") tipo: String,
        @Header("Authorization") token: String
    ): Response<Respuesta>


    @PUT("pdfapi/pdfapi/consultarPdf")
    suspend fun getPdfBase64(
        @Body pdfRequest: PdfRequest,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

}