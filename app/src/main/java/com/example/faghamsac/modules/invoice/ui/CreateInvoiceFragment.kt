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

        // Asigna el LayoutManager y el adaptador al RecyclerView
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = productsAdapter

        // Lógica para el botón "Agregar Producto"
        binding.buttonAddProduct.setOnClickListener {
            // Obtener los valores de los campos
            val productCode = binding.autoCompleteTextViewProduct.text.toString()
            val productName = binding.editTextProductName.text.toString()
            val quantity = binding.editTextQuantity.text.toString()
            val precioVenta = binding.editTextPrice.text.toString()

            // Validar que los campos no estén vacíos
            if (productCode.isNotBlank() && productName.isNotBlank() && quantity.isNotBlank() && precioVenta.isNotBlank()) {
                // Crear un nuevo producto
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

                // Agregar el producto a tu lista
                productsList.add(newProduct)

                Toast.makeText(requireContext(), "Se agregó el producto", Toast.LENGTH_SHORT).show()

                // Notificar al adaptador que los datos han cambiado
                productsAdapter.notifyDataSetChanged()

                // Limpiar los campos después de agregar el producto
                binding.autoCompleteTextViewProduct.setText("")
                binding.editTextProductName.setText("")
                binding.editTextQuantity.setText("")
                binding.editTextPrice.setText("")
            } else {
                // Mostrar un mensaje de error o advertencia
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
                    // Si encontramos el producto, actualiza los campos
                    binding.editTextProductName.setText(product.name)
                    binding.editTextPrice.setText(product.price.toString())

                    // Cerrar el teclado
                    val imm = requireActivity().getSystemService(InputMethodManager::class.java)
                    imm.hideSoftInputFromWindow(binding.autoCompleteTextViewProduct.windowToken, 0)
                } else {
                    // Si no encontramos el producto, limpia los campos
                    binding.editTextProductName.setText("")
                    binding.editTextQuantity.setText("")
                    binding.editTextPrice.setText("")
                }
            }
        })

        binding.buttonEmitInvoice.setOnClickListener {
            // Recoger los datos de la factura
            val clientName = binding.editTextClientName.text.toString()
            val clientRuc = binding.editTextClientRuc.text.toString()
            val invoiceType = "BOLETA" // O usar el valor del combobox

            val detalleDocumento = productsList.map { item ->
                mapOf(
                    "codigoProducto" to item.codigoProducto, // Asegúrate de que este campo no sea vacío
                    "nombreProducto" to item.nombreProducto,
                    "caracteristicas" to null, // Opcional, usa "" si es null
                    "cantidad" to BigDecimal(item.cantidad.toString()), // Asegúrate de convertir a BigDecimal
                    "isc" to (item.isc?.let { BigDecimal(it.toString()) }), // Cambia según tu contexto
                    "tipoAfectacion" to "10", // Asegúrate de que esto sea correcto según tu contexto
                    "precioVenta" to BigDecimal(item.precioVenta.toString()), // Asegúrate de convertir a BigDecimal
                    "valorUnitario" to BigDecimal(item.valorUnitario.toString()), // Cambia según tu contexto
                    "precioUnitario" to BigDecimal(item.precioUnitario.toString()), // Cambia según tu contexto
                    "descuentoUnitario" to (item.descuentoUnitario?.let { BigDecimal(it.toString()) }), // Cambia según tu contexto
                    "valorVenta" to BigDecimal((item.precioVenta * item.cantidad).toString()), // Cálculo del valor de venta
                    "descuentoEsPorcentaje" to  false, // Opcional
                    "usaSerie" to (item.usaSerie ?: ""), // Opcional
                    "unidadMedida" to "UNIDAD_BIENES", // Cambia según tu contexto
                    "unidadMedidaNombre" to "UNIDAD_BIENES", // Cambia según tu contexto
                    "flagPaquete" to item.flagPaquete // Cambia según tu contexto
                )
            }

            // Crear el payload
            val payload = mapOf(
                "numero" to null,
                "rucReceptor" to clientRuc,
                "tipoDocReceptor" to "01", // Cambiar según sea necesario
                "razonSocialReceptor" to clientName,
                "direccionReceptor" to null, // Cambiar según sea necesario
                "moneda" to "PEN",
                "emailReceptor" to null, // Cambiar según sea necesario
                "fechaEmision" to LocalDate.now().toString(),
                "fechaVencimiento" to "2024-11-07", // Cambia esta fecha si es necesario
                "subtotal" to BigDecimal("0"), // Cambiar según el cálculo
                "igv" to BigDecimal("0"), // Cambiar según el cálculo
                "total" to (detalleDocumento.sumOf { it["valorVenta"] as BigDecimal }).toString(),
                "totalGravadas" to "0", // Cambiar según el cálculo
                "totalExoneradas" to "0", // Cambiar según el cálculo
                "totalInafectas" to "0", // Cambiar según el cálculo
                "totalOtrosImpuestos" to "0", // Cambiar según el cálculo
                "isc" to "0", // Cambiar según el cálculo
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

            // Convertir el payload a JSON
            val jsonPayload = Gson().toJson(payload)

            Log.d("payload", "p $jsonPayload")

            // Emitir la cotización
            emitInvoice(jsonPayload, onSuccess = { respuesta ->
                // Maneja la respuesta exitosa
                Toast.makeText(requireContext(), "Cotización emitida: $respuesta", Toast.LENGTH_SHORT).show()
            }, onError = { error ->
                // Maneja el error
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            })

        }

    }

    private fun emitInvoice(payload: String, onSuccess: (Respuesta?) -> Unit, onError: (String) -> Unit) {
        // Asegúrate de inicializar invoiceService antes de llamar
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
        // Crear una lista de los tipos de comprobante
        val tiposComprobante = InvoiceTypeEnum.values().map { it.descripcion }

        // Crear un adaptador para el spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tiposComprobante)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Establecer el adaptador en el spinner
        spinnerTipoComprobante.adapter = adapter

        // Configurar el listener para obtener el tipo seleccionado
        spinnerTipoComprobante.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val tipoSeleccionado = tiposComprobante[position]
                // Manejar el tipo seleccionado aquí
                // Puedes guardar el código correspondiente si lo necesitas
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        })
    }
}
