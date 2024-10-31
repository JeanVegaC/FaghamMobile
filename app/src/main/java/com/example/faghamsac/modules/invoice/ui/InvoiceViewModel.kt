package com.example.faghamsac.modules.invoice.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.faghamsac.modules.invoice.model.Quotation

class InvoiceViewModel : ViewModel() {

    private val _invoices = MutableLiveData<List<Quotation>>()
    val invoices: LiveData<List<Quotation>> get() = _invoices

    fun addInvoice(invoice: Quotation) {
        // Lógica para agregar una factura (podría incluir almacenamiento en base de datos)
    }

    fun getInvoices() {
        // Lógica para recuperar todas las facturas
    }
}