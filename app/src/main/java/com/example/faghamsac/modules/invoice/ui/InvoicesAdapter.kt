package com.example.faghamsac.modules.invoice.ui

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.faghamsac.R
import com.example.faghamsac.configuration.ApiClient
import com.example.faghamsac.modules.invoice.model.Aplicacion
import com.example.faghamsac.modules.invoice.model.Invoice
import com.example.faghamsac.modules.invoice.model.PdfRequest
import com.example.faghamsac.modules.invoice.model.Quotation
import com.example.faghamsac.modules.invoice.model.TokenRequest
import com.example.faghamsac.modules.invoice.services.InvoiceService
import com.example.faghamsac.modules.invoice.services.TokenService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch // Asegúrate de importar launch
import kotlinx.coroutines.withContext

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class InvoicesAdapter(private val invoices: List<Invoice>, private val invoiceService: InvoiceService) :
    RecyclerView.Adapter<InvoicesAdapter.InvoiceViewHolder>() {
    private lateinit var tokenService: TokenService

    inner class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val invoiceDate: TextView = itemView.findViewById(R.id.textViewDate)
        val clientRuc: TextView = itemView.findViewById(R.id.textViewClientRuc)
        val clientName: TextView = itemView.findViewById(R.id.textViewClientName)
        val totalAmount: TextView = itemView.findViewById(R.id.textViewTotalAmount)
        val buttonDownload: ImageView = itemView.findViewById(R.id.buttonDownload)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): InvoiceViewHolder {
        tokenService = ApiClient.createService(TokenService::class.java)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invoice, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = invoices[position]
        Log.d("Invoice", "invoice ${invoice}")
        holder.invoiceDate.text = invoice.fecha_emision
        holder.clientName.text = invoice.razon_social_receptor
        holder.clientRuc.text = invoice.ruc_receptor
        holder.totalAmount.text = "%.2f".format(invoice.total)

        holder.buttonDownload.setOnClickListener {
            downloadPdf(invoice, holder.itemView.context)
        }
    }

    override fun getItemCount() = invoices.size

    private fun downloadPdf(invoice: Invoice, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")

                Log.d("Token", "Token obtenido: $token")


                if (token.isNullOrEmpty()) {
                    Log.e("Token", "Token no encontrado")
                    return@launch
                }

                Log.d("Token", "Token obtenido: $token")

                val pdfRequest = PdfRequest("10256228233", invoice.numero, invoice.serie, invoice.tipo_comprobante)
                val response = invoiceService.getPdfBase64(pdfRequest, token)

                Log.d("base64", "base64: $response")

                if (response.isSuccessful) {
                    try {
                        val base64String = response.body()?.string() ?: ""
                        Log.d("PDF Response", "Response body: $base64String")
                        val pdfFile = convertBase64ToPdf(base64String, context)
                        savePdfToDownloads(pdfFile, context)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "PDF descargado con éxito", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.d("download pdf error", "Código de error: ${response}, Mensaje: ${response}")
                            Toast.makeText(context, "Error al descargar el PDF: ${response}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("PDF Error", "Error: ${response.message()}")
                }

            } catch (e: Exception) {
                Log.e("Download PDF", "Error al obtener el token o al procesar el PDF: ${e.message}")
            }
        }
    }


    private fun convertBase64ToPdf(base64String: String, context: Context): File {
        val pdfFile = File(context.getExternalFilesDir(null), "invoice.pdf")

        // Verifica que el directorio exista
        pdfFile.parentFile?.mkdirs()

        // Decodificar los bytes y escribir en el archivo PDF
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

        FileOutputStream(pdfFile).use { outputStream ->
            outputStream.write(decodedBytes)
        }
        return pdfFile
    }

    suspend fun savePdfToDownloads(pdfFile: File, context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(intent, "Abrir PDF"))
                    Toast.makeText(context, "PDF guardado en: ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al guardar el PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
