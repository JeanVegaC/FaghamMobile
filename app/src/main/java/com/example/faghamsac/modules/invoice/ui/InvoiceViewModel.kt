package com.example.faghamsac.modules.invoice.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.faghamsac.modules.invoice.model.Invoice

class InvoiceViewModel : ViewModel() {

    private val _invoices = MutableLiveData<List<Invoice>>()
    val invoices: LiveData<List<Invoice>> get() = _invoices

    fun addInvoice(invoice: Invoice) {
        // Lógica para agregar una factura (podría incluir almacenamiento en base de datos)
    }

    fun getInvoices() {
        // Lógica para recuperar todas las facturas
    }
}