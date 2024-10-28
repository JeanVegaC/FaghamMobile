package com.example.faghamsac.modules.invoice.ui

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.faghamsac.databinding.FragmentCreateInvoiceBinding
import com.example.faghamsac.modules.invoice.model.Product
import com.example.faghamsac.data.products
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.faghamsac.modules.invoice.model.Invoice
import com.example.faghamsac.modules.invoice.model.InvoiceItem
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar

class CreateInvoiceFragment : Fragment() {
    private lateinit var binding: FragmentCreateInvoiceBinding
    private lateinit var productsAdapter: ProductsAdapter
    private var productsList = mutableListOf<InvoiceItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateInvoiceBinding.inflate(inflater, container, false)
        productsList = mutableListOf()
        productsAdapter = ProductsAdapter(productsList)

        // Asigna el LayoutManager al RecyclerView
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext());


        // Asigna el adaptador al RecyclerView
        binding.recyclerViewProducts.adapter = productsAdapter

        // Lógica para el botón "Agregar Producto"
        binding.buttonAddProduct.setOnClickListener {
            Log.d("asdasd", "Presionando agregar producto")

            // Obtener los valores de los campos
            val productCode = binding.autoCompleteTextViewProduct.text.toString()
            val productName = binding.editTextProductName.text.toString()
            val quantity = binding.editTextQuantity.text.toString()
            val price = binding.editTextPrice.text.toString()

            // Validar que los campos no estén vacíos
            if (productCode.isNotBlank() && productName.isNotBlank() && quantity.isNotBlank() && price.isNotBlank()) {
                // Crear un nuevo producto
                val newProduct = InvoiceItem(
                    productCode = productCode,
                    productName = productName,
                    quantity = quantity.toInt(),
                    price = price.toDouble()
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

        // Configuración del TextWatcher
        binding.autoCompleteTextViewProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Aquí puedes manejar lo que sucede después de que el texto cambia
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Aquí puedes manejar lo que sucede antes de que el texto cambie
            }

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
            val id = System.currentTimeMillis().toString()
            val date = binding.editTextDate.text.toString()
            val clientName = binding.editTextClientName.text.toString()
            val clientRuc = binding.editTextClientRuc.text.toString()

            val totalAmount = calculateTotalAmount()

            // Crear instancia de Invoice
            val invoiceData = Invoice(
                id = id,
                date = date,
                clientName = clientName,
                clientRuc= clientRuc,
                totalAmount = calculateTotalAmount(),
                items = productsList
            )

            // Convertir los datos a JSON
            val jsonInvoiceData = Gson().toJson(invoiceData)

            // DESCARGAR JSON
            saveToDownloads(jsonInvoiceData)
        }

        binding.editTextDate.apply {
            isFocusable = false // Evita la edición directa
            isClickable = true  // Permite el clic para abrir el DatePickerDialog

            setOnClickListener {
                // Obtén la fecha actual para configurar el DatePicker con valores iniciales
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                // Crea el DatePickerDialog
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, selectedYear, selectedMonth, selectedDay ->
                        // Formato de la fecha
                        val dateString = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                        setText(dateString)
                    },
                    year, month, day
                )

                // Muestra el DatePickerDialog
                datePickerDialog.show()
            }
        }

    }

    private fun saveToDownloads(data: String) {
        // Nombre y formato del archivo
        val fileName = "invoice_data.txt"

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

    private fun calculateTotalAmount(): Double {
        return productsList.sumOf { it.price * it.quantity }
    }




}