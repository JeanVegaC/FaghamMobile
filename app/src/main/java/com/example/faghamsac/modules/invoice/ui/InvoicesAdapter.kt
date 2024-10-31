package com.example.faghamsac.modules.invoice.ui

import android.content.Context
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.faghamsac.R
import com.example.faghamsac.configuration.ApiClient
import com.example.faghamsac.modules.invoice.model.Quotation
import com.example.faghamsac.modules.invoice.services.InvoiceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch // Asegúrate de importar launch
import kotlinx.coroutines.withContext

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class InvoicesAdapter(private val invoices: List<Quotation>, private val invoiceService: InvoiceService) :
    RecyclerView.Adapter<InvoicesAdapter.InvoiceViewHolder>() {


    inner class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val invoiceDate: TextView = itemView.findViewById(R.id.textViewDate)
        val clientRuc: TextView = itemView.findViewById(R.id.textViewClientRuc)
        val clientName: TextView = itemView.findViewById(R.id.textViewClientName)
        val totalAmount: TextView = itemView.findViewById(R.id.textViewTotalAmount)
        val buttonDownload: ImageView = itemView.findViewById(R.id.buttonDownload)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int, ): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_invoice, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = invoices[position]
        Log.d("Invoice", "invoice ${invoice}")
        holder.invoiceDate.text = invoice.fechaEmision
        holder.clientName.text = invoice.razonSocialReceptor
        holder.clientRuc.text = invoice.rucReceptor.toString()
        holder.totalAmount.text = "%.2f".format(invoice.total)

        holder.buttonDownload.setOnClickListener {
            downloadPdf(invoice, holder.itemView.context)
        }
    }

    override fun getItemCount() = invoices.size

    private fun downloadPdf(invoice: Quotation, context: Context) {
        val rucEmisor = "10256228233" // Asegúrate de usar el RUC correcto
        val numero = 30 // El número de la cotización
        val tipo = "A4" // El tipo de PDF
        val ruc = "10256228233" // RUC opcional
        val formato = "BASE64" // RUC opcional

        CoroutineScope(Dispatchers.IO).launch {
            val token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIyNDdiYjQyMS0wZjg0LTQ2NGItYWUwZi01NGQ5MzZhZTAzYWQiLCJpYXQiOjE3MzA0MTUwMjcsImlzcyI6IkNMT1NFMlUiLCJzdWIiOiIxMDI1NjIyODIzM3xhbmRyZWFyb2Npb2Fycm95b0Bob3RtYWlsLmNvbXwxfERFViIsImV4cCI6MTczMDQ0MzgyN30._PDFfI868cph3YKx6uMDBNT8uyjkhal9XUH_OL6K8tI"

            try {

                val response = invoiceService.downloadPdf(numero, tipo, ruc, formato)

                if (response.isSuccessful) {
                    val base64String = response.body() ?: return@launch
                    Log.d("PDF Response", "Response body: $base64String")
                    val pdfFile = convertBase64ToPdf(base64String, context)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "PDF descargado con éxito", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.d("download pdf error", "Código de error: ${response.code()}, Mensaje: ${response}")
                        Toast.makeText(context, "Error al descargar el PDF: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("download pdf error", "Error: ${e.message}", e)
                    Toast.makeText(context, "Error al descargar el PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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



}
