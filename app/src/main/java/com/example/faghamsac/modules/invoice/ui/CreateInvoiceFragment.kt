package com.example.faghamsac.modules.invoice.ui

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
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
import androidx.compose.ui.text.toLowerCase
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.faghamsac.R
import com.example.faghamsac.configuration.ApiClient
import com.example.faghamsac.modules.invoice.model.Aplicacion
import com.example.faghamsac.modules.invoice.model.InvoiceRequest
import com.example.faghamsac.modules.invoice.model.InvoiceTypeEnum
import com.example.faghamsac.modules.invoice.model.Respuesta
import com.example.faghamsac.modules.invoice.model.TokenRequest
import com.example.faghamsac.modules.invoice.services.InvoiceService
import com.example.faghamsac.modules.invoice.services.TokenService
import com.example.faghamsac.utils.mapPayloadToInvoiceRequest
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateInvoiceFragment : Fragment() {
    private lateinit var binding: FragmentCreateInvoiceBinding
    private lateinit var productsAdapter: ProductsAdapter
    private var productsList = mutableListOf<Product>()
    private lateinit var invoiceService: InvoiceService
    private lateinit var tokenService: TokenService
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
                val newProduct = Product(
                    code = productCode,
                    name = productName,
                    cantidad = quantity.toInt(),
                    price = precioVenta.toDouble(),
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
        tokenService = ApiClient.createService(TokenService::class.java)


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
            val invoiceType = binding.spinnerTipoComprobante.selectedItem.toString().lowercase()

            val invoiceReq = mapPayloadToInvoiceRequest(clientName, clientRuc, invoiceType, productsList);

            // Convertir el objeto `Invoice` a JSON
            val gson = Gson()
            // val invoiceReqJson = gson.toJson(invoiceReq)
            //Log.d("invoiceJson", invoiceReqJson)

            val request = TokenRequest(
                mail = "andrearocioarroyo@gmail.com",
                ruc = "10754522904",
                clave = "\$\$pruebasC2U",
                aplicacion = Aplicacion(codigo = "1")
            )

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // Crear la solicitud
                    val request = TokenRequest(
                        mail = "andrearocioarroyo@gmail.com",
                        ruc = "10754522904",
                        clave = "\$\$pruebasC2U",
                        aplicacion = Aplicacion(codigo = "1")
                    )

                    // Llamar al servicio
                    val response = tokenService.getToken(request)

                    // Verificar la respuesta
                    if (response.isSuccessful) {
                        val tokenResponse = response.body()
                        val token = "Bearer " + tokenResponse?.c2uToken ?: ""

                        Log.d("Token", "Token obtenido: $token")

                        Log.d("payload", "$invoiceReq")
                        // Emitir la cotización
                        emitInvoice(invoiceReq, invoiceType, token, onSuccess = { respuesta ->
                            // Maneja la respuesta exitosa
                            Toast.makeText(requireContext(), "Cotización emitida: $respuesta", Toast.LENGTH_SHORT).show()
                        }, onError = { error ->
                            // Maneja el error
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        })
                    } else {
                        Log.e("Token", "Error al obtener el token: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("Token", "Excepción al obtener el token: ${e.message}")
                }
            }



        }

    }

    private fun emitInvoice(payload: InvoiceRequest, invoiceType: String, token: String, onSuccess: (Respuesta?) -> Unit, onError: (String) -> Unit) {
        // Asegúrate de inicializar invoiceService antes de llamar
        viewLifecycleOwner.lifecycleScope.launch { // Cambiado a lifecycleScope
            try {
                Log.d("invoiceType", "$invoiceType")
                val response: Response<Respuesta> = invoiceService.emitInvoice(payload, invoiceType, token)

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
