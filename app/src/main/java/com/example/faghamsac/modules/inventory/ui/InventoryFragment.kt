package com.example.faghamsac.modules.inventory.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.faghamsac.R
import com.example.faghamsac.databinding.FragmentCreateInvoiceBinding
import com.example.faghamsac.databinding.FragmentInventoryBinding
import com.example.faghamsac.modules.inventory.services.ProductService
import com.example.faghamsac.modules.invoice.model.Product

class InventoryFragment : Fragment() {
    private lateinit var binding: FragmentInventoryBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputCode: EditText
    private lateinit var inputName: EditText
    private lateinit var inputPrice: EditText
    private lateinit var inputQuantity: EditText
    private lateinit var btnAddOrUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var productService: ProductService
    private lateinit var adapter: ProductoAdapter
    private val productList = mutableListOf<Product>()
    private val productsSuggest = mutableListOf<Product>() // Lista de productos sugeridos

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        productService = ProductService()
        binding = FragmentInventoryBinding.inflate(inflater, container, false)
        loadProducts()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProductoAdapter(productList)
        binding.recyclerView.adapter = adapter

        recyclerView = view.findViewById(R.id.recyclerView)
        inputCode = view.findViewById(R.id.autoCompleteEditTextCodigo)
        inputName = view.findViewById(R.id.inputNombre)
        inputPrice = view.findViewById(R.id.inputPrecio)
        inputQuantity = view.findViewById(R.id.inputCantidad)
        btnAddOrUpdate = view.findViewById(R.id.btnAgregarEditar)
        btnDelete = view.findViewById(R.id.btnEliminar)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProductoAdapter(productList)
        recyclerView.adapter = adapter


        // Configurar el AutoCompleteTextView
        binding.autoCompleteEditTextCodigo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadProducts()
                val code = s.toString()
                val product = productsSuggest.find { it.code == code }
                Log.d("escribiendo","$code")
                if (product != null) {


                    binding.inputNombre.setText(product.name)
                    binding.inputCantidad.setText(product.quantity.toString())
                    binding.inputPrecio.setText(product.price.toString())

                    // Cerrar el teclado
                    val imm = requireActivity().getSystemService(InputMethodManager::class.java)
                    imm.hideSoftInputFromWindow(binding.autoCompleteEditTextCodigo.windowToken, 0)
                } else {
                    // Limpiar campos si no se encuentra el producto
                    binding.inputNombre.setText("")
                    binding.inputCantidad.setText("")
                    binding.inputPrecio.setText("")
                }
            }
        })

        productService = ProductService()

        productService.getAllProductos { products ->
            productList.clear()
            productList.addAll(products)
            adapter.notifyDataSetChanged()
        }

        btnAddOrUpdate.setOnClickListener {
            val code = inputCode.text.toString()
            val name = inputName.text.toString()
            val price = inputPrice.text.toString().toDoubleOrNull() ?: 0.0
            val quantity = inputQuantity.text.toString().toIntOrNull() ?: 0

            if (code.isNotEmpty()) {
                val product = Product(code, name, price, quantity)
                productService.addOrUpdateProduct(product) { success ->
                    if (isAdded) {
                        if (success) {
                            clearInputs()
                            Toast.makeText(requireContext(), "Producto guardado correctamente", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Error al guardar el producto", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                if (isAdded) {
                    Toast.makeText(requireContext(), "El código es obligatorio", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnDelete.setOnClickListener {
            val code = inputCode.text.toString()
            if (code.isNotEmpty()) {
                productService.deleteProduct(code) { success ->
                    if (success) {
                        clearInputs()
                        Toast.makeText(context, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "El código es obligatorio para eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProducts() {
        // Servicio de Firebase para obtener productos
        productService.getAllProductos { productList ->
            if (isAdded) {
                Log.d("products firebase", "$productList")
                productsSuggest.clear()
                productsSuggest.addAll(productList)

                // Configurar las sugerencias en el AutoCompleteTextView
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    productsSuggest.map { it.code } // Mostrar códigos de productos
                )
                binding.autoCompleteEditTextCodigo.setAdapter(adapter)
            }
        }
    }

    private fun clearInputs() {
        inputCode.text.clear()
        inputName.text.clear()
        inputPrice.text.clear()
        inputQuantity.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        productService.removeListeners() // Implementa un método para desconectar los listeners en ProductService
    }

}
