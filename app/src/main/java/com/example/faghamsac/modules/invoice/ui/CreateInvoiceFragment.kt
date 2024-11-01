package com.example.faghamsac.modules.invoice.ui

import CotizacionItem
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import com.example.faghamsac.databinding.FragmentCreateInvoiceBinding
import com.example.faghamsac.modules.invoice.model.Product
import com.example.faghamsac.data.products
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.faghamsac.R
import com.example.faghamsac.configuration.ApiClient
import com.example.faghamsac.modules.invoice.model.Invoice
import com.example.faghamsac.modules.invoice.model.InvoiceItem
import com.example.faghamsac.modules.invoice.model.InvoiceTypeEnum
import com.example.faghamsac.modules.invoice.model.Respuesta
import com.example.faghamsac.modules.invoice.services.InvoiceService
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.security.auth.callback.Callback

class CreateInvoiceFragment : Fragment() {
    private lateinit var binding: FragmentCreateInvoiceBinding
    private lateinit var productsAdapter: ProductsAdapter
    private var productsList = mutableListOf<CotizacionItem>()
    private lateinit var invoiceService: InvoiceService
    private lateinit var spinnerTipoComprobante: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateInvoiceBinding.inflate(inflater, container, false)
        productsList = mutableListOf()
        productsAdapter = ProductsAdapter(productsList)

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = productsAdapter

        binding.buttonAddProduct.setOnClickListener {
        
            val productCode = binding.autoCompleteTextViewProduct.text.toString()
            val productName = binding.editTextProductName.text.toString()
            val quantity = binding.editTextQuantity.text.toString()
            val precioVenta = binding.editTextPrice.text.toString()

        
            if (productCode.isNotBlank() && productName.isNotBlank() && quantity.isNotBlank() && precioVenta.isNotBlank()) {
            
                val newProduct = CotizacionItem(
                    codigoProducto = productCode,
                    nombreProducto = productName,
                    cantidad = BigDecimal(quantity),
                    precioUnitario = BigDecimal(precioVenta),
                    precioVenta = BigDecimal(precioVenta),
                    tipoAfectacion = "10",
                    valorUnitario = BigDecimal(precioVenta),
                    valorVenta = BigDecimal(precioVenta)
                )

            
                productsList.add(newProduct)

                Toast.makeText(requireContext(), "Se agregó el producto", Toast.LENGTH_SHORT).show()

                
                productsAdapter.notifyDataSetChanged()


                binding.autoCompleteTextViewProduct.setText("")
                binding.editTextProductName.setText("")
                binding.editTextQuantity.setText("")
                binding.editTextPrice.setText("")
            } else {
                
                Toast.makeText(requireContext(), "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invoiceService = ApiClient.createService(InvoiceService::class.java)


        spinnerTipoComprobante = view.findViewById(R.id.spinnerTipoComprobante)
        setupSpinner()

        binding.autoCompleteTextViewProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val code = s.toString()
                val product = products.find { it.code == code }
                if (product != null) {
                    
                    binding.editTextProductName.setText(product.name)
                    binding.editTextPrice.setText(product.price.toString())

                
                    val imm = requireActivity().getSystemService(InputMethodManager::class.java)
                    imm.hideSoftInputFromWindow(binding.autoCompleteTextViewProduct.windowToken, 0)
                } else {
                    
                    binding.editTextProductName.setText("")
                    binding.editTextQuantity.setText("")
                    binding.editTextPrice.setText("")
                }
            }
        })

        binding.buttonEmitInvoice.setOnClickListener {
        
            val clientName = binding.editTextClientName.text.toString()
            val clientRuc = binding.editTextClientRuc.text.toString()
            val invoiceType = "BOLETA" // 

            val detalleDocumento = productsList.map { item ->
                mapOf(
                    "codigoProducto" to item.codigoProducto, 
                    "nombreProducto" to item.nombreProducto,
                    "caracteristicas" to null, // Opcional, usa "" si es null
                    "cantidad" to BigDecimal(item.cantidad.toString()), 
                    "isc" to (item.isc?.let { BigDecimal(it.toString()) }), 
                    "tipoAfectacion" to "10",
                    "precioVenta" to BigDecimal(item.precioVenta.toString()),
                    "valorUnitario" to BigDecimal(item.valorUnitario.toString()), 
                    "precioUnitario" to BigDecimal(item.precioUnitario.toString()), 
                    "descuentoUnitario" to (item.descuentoUnitario?.let { BigDecimal(it.toString()) }), 
                    "valorVenta" to BigDecimal((item.precioVenta * item.cantidad).toString()), 
                    "descuentoEsPorcentaje" to  false, 
                    "usaSerie" to (item.usaSerie ?: ""), 
                    "unidadMedida" to "UNIDAD_BIENES", 
                    "unidadMedidaNombre" to "UNIDAD_BIENES", 
                    "flagPaquete" to item.flagPaquete 
                )
            }

            
            val payload = mapOf(
                "numero" to null,
                "rucReceptor" to clientRuc,
                "tipoDocReceptor" to "01", 
                "razonSocialReceptor" to clientName,
                "direccionReceptor" to null, 
                "moneda" to "PEN",
                "emailReceptor" to null, 
                "fechaEmision" to LocalDate.now().toString(),
                "fechaVencimiento" to "2024-11-07", 
                "subtotal" to BigDecimal("0"), 
                "igv" to BigDecimal("0"), 
                "total" to (detalleDocumento.sumOf { it["valorVenta"] as BigDecimal }).toString(),
                "totalGravadas" to "0", 
                "totalExoneradas" to "0",
                "totalInafectas" to "0", 
                "totalOtrosImpuestos" to "0", 
                "isc" to "0", 
                "totalOtrosCargos" to null,
                "observacion" to "",
                "factorCambio" to "",
                "preciosIncluyenIgv" to 0,
                "contacto" to null,
                "detalle" to detalleDocumento,
                "formaPago" to "Efectivo",
                "otrosCargosEsProcentage" to false,
                "descuentoGlobal" to null,
                "descuentoEsProcentage" to false,
                "condiciones" to emptyList<Any>(),
                "idCondicionPago" to "CONT",
                "almacen" to "",
                "observaciones" to null,
                "vendedor" to "andrearocioarroyo@hotmail.com"
            )

    
            val jsonPayload = Gson().toJson(payload)

            Log.d("payload", "p $jsonPayload")

        
            emitInvoice(jsonPayload, onSuccess = { respuesta ->
                
                Toast.makeText(requireContext(), "Cotización emitida: $respuesta", Toast.LENGTH_SHORT).show()
            }, onError = { error ->
    
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            })

        }

    }

    private fun emitInvoice(payload: String, onSuccess: (Respuesta?) -> Unit, onError: (String) -> Unit) {
        
        var payloadString = JSONObject(payload).toString()

        Log.d("JSON Payload", payloadString) // Verifica el JSON generado


        viewLifecycleOwner.lifecycleScope.launch { // Cambiado a lifecycleScope
            try {
                val token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIyNDdiYjQyMS0wZjg0LTQ2NGItYWUwZi01NGQ5MzZhZTAzYWQiLCJpYXQiOjE3MzA0MTUwMjcsImlzcyI6IkNMT1NFMlUiLCJzdWIiOiIxMDI1NjIyODIzM3xhbmRyZWFyb2Npb2Fycm95b0Bob3RtYWlsLmNvbXwxfERFViIsImV4cCI6MTczMDQ0MzgyN30._PDFfI868cph3YKx6uMDBNT8uyjkhal9XUH_OL6K8tI"

                val response: Response<Respuesta> = invoiceService.emitCotizacion(payloadString, token)

                if (response.isSuccessful) {
                    val respuesta = response.body()
                    Toast.makeText(requireContext(), "Factura emitida exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("error asd", "error ${response}")
                    Log.d("error 1", "error ${response.code()} - ${response.message()}")
                    Log.d("Response Body", response.errorBody()?.string() ?: "No response body")
                    Toast.makeText(requireContext(), "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.d("error 2", "error ${e}")
                Toast.makeText(requireContext(), "Excepción: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun setupSpinner() {
        
        val tiposComprobante = InvoiceTypeEnum.values().map { it.descripcion }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tiposComprobante)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

    
        spinnerTipoComprobante.adapter = adapter


        spinnerTipoComprobante.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val tipoSeleccionado = tiposComprobante[position]
        
            
            }
        })
    }
}
