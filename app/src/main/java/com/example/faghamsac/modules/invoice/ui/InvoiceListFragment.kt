package com.example.faghamsac.modules.invoice.ui

import android.content.Context
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
import com.example.faghamsac.modules.invoice.services.InvoiceService
import com.example.faghamsac.modules.invoice.services.TokenService
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class InvoiceListFragment : Fragment() {

    private lateinit var binding: FragmentInvoiceListBinding
    private lateinit var invoicesAdapter: InvoicesAdapter
    private var invoicesList = mutableListOf<Invoice>()
    private lateinit var invoiceService: InvoiceService
    private lateinit var tokenService: TokenService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInvoiceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenService = ApiClient.createService(TokenService::class.java)
        invoiceService = ApiClient.createService(InvoiceService::class.java)
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val sharedPreferences = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")

                Log.d("Token asd", "Token obtenido: $token")

                if (token.isNullOrEmpty()) {
                    Log.e("Token", "Token no encontrado")
                    return@launch
                }

                Log.d("Token asd", "Token obtenido: $token")
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")


                val fechaFin = LocalDateTime.now()
                    .plusDays(1)
                    .toLocalDate()
                    .atStartOfDay()
                    .atZone(ZoneOffset.UTC)
                    .format(formatter)

                val fechaInicio = LocalDateTime.now().minus(1, ChronoUnit.WEEKS).toLocalDate().atStartOfDay()
                    .atZone(ZoneOffset.UTC).format(formatter)

                Log.d("Fecha", "Fecha inicio: $fechaInicio")
                Log.d("Fecha", "Fecha fin: $fechaFin")

                val invoices: InvoicePagination = invoiceService.getInvoices(
                    rucEmisor = "10256228233",
                    first = 0,
                    rows = 10,
                    fechaEmisionInicio = fechaInicio,
                    fechaEmisionFin = fechaFin,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    page = 0,
                    size = 10,
                    token
                )

                Log.d("aqui", "asdas $invoices")
                invoicesList.clear()
                invoicesList.addAll(invoices.result)
                invoicesAdapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("Token", "Excepción al obtener el token: ${e.message}")
            }
        }


        binding.invoiceRecyclerView.layoutManager = LinearLayoutManager(context)

        invoicesAdapter = InvoicesAdapter(invoicesList, invoiceService)
        binding.invoiceRecyclerView.adapter = invoicesAdapter

    }
}