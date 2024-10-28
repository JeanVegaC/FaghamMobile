package com.example.faghamsac.modules.invoice.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.faghamsac.R
import com.example.faghamsac.databinding.FragmentCreateInvoiceBinding
import com.example.faghamsac.databinding.FragmentInvoiceListBinding
import com.example.faghamsac.modules.invoice.model.Invoice
import com.example.faghamsac.modules.invoice.model.InvoiceItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class InvoiceListFragment : Fragment() {
    private lateinit var binding: FragmentInvoiceListBinding
    private lateinit var invoicesAdapter: InvoicesAdapter
    private var invoicesList = mutableListOf<Invoice>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInvoiceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.invoiceRecyclerView.layoutManager = LinearLayoutManager(context)

        invoicesAdapter = InvoicesAdapter(invoicesList)
        binding.invoiceRecyclerView.adapter = invoicesAdapter

        loadInvoices()
    }

    private fun loadInvoices() {
        try {
            invoicesList.clear() // Limpiar la lista antes de cargar nuevos datos
            val loadedInvoices = loadInvoicesFromAssets()
            invoicesList.addAll(loadedInvoices) // Agregar las facturas cargadas a la lista
            invoicesAdapter.notifyDataSetChanged() // Notificar al adaptador sobre los cambios
            Log.d("Invoices", "Cantidad de facturas cargadas: ${invoicesList.size}")
        } catch (e: Exception) {
            Log.e("InvoiceListFragment", "Error al cargar facturas: ${e.message}")
        }
    }

    private fun loadInvoicesFromAssets(): List<Invoice> {
        val json = context?.assets?.open("invoices.json")?.bufferedReader().use { it?.readText() }
        val gson = Gson()
        val type = object : TypeToken<List<Invoice>>() {}.type
        return gson.fromJson(json, type)
    }
}