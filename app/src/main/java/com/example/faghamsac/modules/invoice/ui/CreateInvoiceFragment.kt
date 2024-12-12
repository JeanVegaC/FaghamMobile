package com.example.faghamsac.modules.invoice.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch

import com.example.faghamsac.databinding.FragmentCreateInvoiceBinding
import com.example.faghamsac.modules.invoice.model.Product
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
import com.example.faghamsac.modules.inventory.services.ProductService
import com.example.faghamsac.modules.invoice.model.InvoiceRequest
import com.example.faghamsac.modules.invoice.model.InvoiceTypeEnum
import com.example.faghamsac.modules.invoice.model.Respuesta
import com.example.faghamsac.modules.invoice.services.InvoiceService
import com.example.faghamsac.modules.invoice.services.TokenService
import com.example.faghamsac.utils.mapPayloadToInvoiceRequest
import retrofit2.Response

class CreateInvoiceFragment : Fragment() {
    private lateinit var binding: FragmentCreateInvoiceBinding
    private lateinit var productsAdapter: ProductsAdapter
    private var productsList = mutableListOf<Product>()
    private var productsSuggest = mutableListOf<Product>()
    private lateinit var invoiceService: InvoiceService
    private lateinit var tokenService: TokenService
    private lateinit var spinnerTipoComprobante: Spinner
    private lateinit var productService: ProductService


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateInvoiceBinding.inflate(inflater, container, false)
        productsList = mutableListOf()
        productsAdapter = ProductsAdapter(productsList)
        productService = ProductService()

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = productsAdapter

        binding.buttonAddProduct.setOnClickListener {
            val productCode = binding.autoCompleteTextViewProduct.text.toString()
            val productName = binding.editTextProductName.text.toString()
            val quantity = binding.editTextQuantity.text.toString()
            val precioVenta = binding.editTextPrice.text.toString()

            if (productCode.isNotBlank() && productName.isNotBlank() && quantity.isNotBlank() && precioVenta.isNotBlank()) {
                val newProduct = Product(
                    code = productCode,
                    name = productName,
                    quantity = quantity.toInt(),
                    price = precioVenta.toDouble(),
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

        loadProducts()

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
                loadProducts()
                val code = s.toString()
                val product = productsSuggest.find { it.code == code }
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
            val invoiceType = binding.spinnerTipoComprobante.selectedItem.toString().lowercase()

            val invoiceReq = mapPayloadToInvoiceRequest(clientName, clientRuc, invoiceType, productsList);

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val sharedPreferences = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("token", "") // Obtiene el token guardado o un valor vacío si no existe

                    if (token.isNullOrEmpty()) {
                        Log.e("Token", "Token no encontrado")
                        return@launch
                    }

                    emitInvoice(invoiceReq, invoiceType, token, onSuccess = { respuesta ->
                        Toast.makeText(requireContext(), "Cotización emitida: $respuesta", Toast.LENGTH_SHORT).show()
                    }, onError = { error ->
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    })
                } catch (e: Exception) {
                    Log.e("Token", "Excepción al obtener el token: ${e.message}")
                }
            }
        }

    }

    private fun emitInvoice(payload: InvoiceRequest, invoiceType: String, token: String, onSuccess: (Respuesta?) -> Unit, onError: (String) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("invoiceType", "$invoiceType")
                val response: Response<Respuesta> = invoiceService.emitInvoice(payload, invoiceType, token)

                if (response.isSuccessful) {
                    val respuesta = response.body()
                    val type = invoiceType.replaceFirstChar { it.uppercaseChar() }
                    Toast.makeText(requireContext(), "$type emitida exitosamente", Toast.LENGTH_SHORT).show()
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


    private fun loadProducts() {
        productService.getAllProductos { e ->
            Log.d("products firebase", "$e")
            productsSuggest.clear()
            productsSuggest.addAll(e)
        }
    }

    private fun setupSpinner() {
        val tiposComprobante = InvoiceTypeEnum.values().map { it.descripcion }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tiposComprobante)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerTipoComprobante.adapter = adapter

        spinnerTipoComprobante.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val tipoSeleccionado = tiposComprobante[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        spinnerTipoComprobante.onItemSelectedListener = null
    }
}
