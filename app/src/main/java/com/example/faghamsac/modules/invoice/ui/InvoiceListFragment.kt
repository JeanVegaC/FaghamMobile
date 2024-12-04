package com.example.faghamsac.modules.invoice.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.faghamsac.configuration.ApiClient
import com.example.faghamsac.databinding.FragmentInvoiceListBinding
import com.example.faghamsac.modules.invoice.model.Invoice
import com.example.faghamsac.modules.invoice.model.InvoicePagination
import com.example.faghamsac.modules.invoice.model.Quotation
import com.example.faghamsac.modules.invoice.model.QuotationPagination
import com.example.faghamsac.modules.invoice.services.InvoiceService
import com.example.faghamsac.utils.mapApiResponseToInvoices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class InvoiceListFragment : Fragment() {

    private lateinit var binding: FragmentInvoiceListBinding
    private lateinit var invoicesAdapter: InvoicesAdapter
    private var invoicesList = mutableListOf<Quotation>()
    private lateinit var invoiceService: InvoiceService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInvoiceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                invoiceService = ApiClient.createService(InvoiceService::class.java)
               /* val token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2YWZjODg4Ni04NjVjLTRiMWEtOWZhZC1iNWEwNjA3NTdhNzAiLCJpYXQiOjE3MzA0MDAzMzIsImlzcyI6IkNMT1NFMlUiLCJzdWIiOiIxMDI1NjIyODIzM3xhbmRyZWFyb2Npb2Fycm95b0Bob3RtYWlsLmNvbXwxfERFViIsImV4cCI6MTczMDQyOTEzMn0.s34tdcAzOwrzjNZRFVjdzCEK7IhKJY9bp-16vQ3KCAU"
                val invoices: InvoicePagination = invoiceService.getInvoices(
                    token,
                    rucEmisor = "10256228233",
                    first = 0,
                    rows = 10,
                    fechaEmisionInicio = "2024-10-22T05:00:00.000Z",
                    fechaEmisionFin = "2024-10-29T05:00:00.000Z",
                    usuarioCreacion = "andrearocioarroyo@hotmail.com",
                    fechaInicio = "2024-10-22T05:00:00.000Z",
                    fechaFin = "2024-10-29T05:00:00.000Z",
                    page = 0,
                    size = 10
                )
                Log.d("aqui", "asdas $invoices")
                invoicesList.clear()
                invoicesList.addAll(mapApiResponseToInvoices(invoices))
                invoicesAdapter.notifyDataSetChanged()

                */

                val token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIyNDdiYjQyMS0wZjg0LTQ2NGItYWUwZi01NGQ5MzZhZTAzYWQiLCJpYXQiOjE3MzA0MTUwMjcsImlzcyI6IkNMT1NFMlUiLCJzdWIiOiIxMDI1NjIyODIzM3xhbmRyZWFyb2Npb2Fycm95b0Bob3RtYWlsLmNvbXwxfERFViIsImV4cCI6MTczMDQ0MzgyN30._PDFfI868cph3YKx6uMDBNT8uyjkhal9XUH_OL6K8tI"
                val invoices: QuotationPagination = invoiceService.getQuotation(
                    token,
                    codigoSucursal = "10256228233",
                    first = 0,
                    rows = 10,
                    fechaInicio = "2024-10-24T05:00:00.000Z",
                    fechaFin = "2024-10-31T05:00:00.000Z",
                    page = 0,
                    size = 10
                )

                Log.d("aqui", "asdas $invoices")
                invoicesList.clear()
                invoicesList.addAll(mapApiResponseToInvoices(invoices))
                invoicesAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.invoiceRecyclerView.layoutManager = LinearLayoutManager(context)

        invoicesAdapter = InvoicesAdapter(invoicesList, invoiceService)
        binding.invoiceRecyclerView.adapter = invoicesAdapter

    }
}