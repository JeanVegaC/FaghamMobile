package com.example.faghamsac.modules.invoice.ui

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.faghamsac.modules.invoice.model.Invoice
import com.example.faghamsac.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class InvoicesAdapter(private val invoices: List<Invoice>) :
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
        holder.invoiceDate.text = invoice.date
        holder.clientName.text = invoice.clientName
        holder.clientRuc.text = invoice.clientRuc
        holder.totalAmount.text = "%.2f".format(invoice.totalAmount)

        holder.buttonDownload.setOnClickListener {
            downloadInvoiceAsText(invoice, holder.itemView.context)
        }
    }

    override fun getItemCount() = invoices.size

    private fun downloadInvoiceAsText(invoice: Invoice, context: Context) {
        val fileName = "invoice_${invoice.id}.txt"
        val content = StringBuilder().apply {
            appendLine("{")
            appendLine("  \"id\": \"${invoice.id}\",")
            appendLine("  \"date\": \"${invoice.date}\",")
            appendLine("  \"clientName\": \"${invoice.clientName}\",")
            appendLine("  \"clientRuc\": \"${invoice.clientRuc}\",")
            appendLine("  \"totalAmount\": ${invoice.totalAmount},")
            appendLine("  \"items\": [")
            invoice.items.forEachIndexed { index, item ->
                append("    {")
                append("\"id\": \"${item.productCode}\", ")
                append("\"name\": \"${item.productName}\", ")
                append("\"quantity\": ${item.quantity}, ")
                append("\"price\": ${item.price}")
                append("}")
                if (index < invoice.items.size - 1) append(",")
                appendLine()
            }
            appendLine("  ]")
            appendLine("}")
        }.toString()

        saveToDownloads(fileName, content, context)
    }

    private fun saveToDownloads(fileName: String, data: String, context: Context) {
        // Carpeta de descargas
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        try {
            // Guardar el archivo en la ubicación de descargas
            FileOutputStream(file).use { output ->
                output.write(data.toByteArray())
            }

            // Confirmación al usuario
            Toast.makeText(context, "Archivo descargado en la carpeta de descargas", Toast.LENGTH_SHORT).show()

        } catch (e: IOException) {
            // Mostrar error si ocurre
            Toast.makeText(context, "Error al guardar el archivo", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
