package com.example.faghamsac.modules.inventory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.faghamsac.R
import com.example.faghamsac.modules.inventory.services.ProductService
import com.example.faghamsac.modules.invoice.model.Product

class InventoryFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        inputCode = view.findViewById(R.id.inputCodigo)
        inputName = view.findViewById(R.id.inputNombre)
        inputPrice = view.findViewById(R.id.inputPrecio)
        inputQuantity = view.findViewById(R.id.inputCantidad)
        btnAddOrUpdate = view.findViewById(R.id.btnAgregarEditar)
        btnDelete = view.findViewById(R.id.btnEliminar)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProductoAdapter(productList)
        recyclerView.adapter = adapter

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
                    if (success) {
                        clearInputs()
                        Toast.makeText(context, "Producto guardado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al guardar el producto", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "El código es obligatorio", Toast.LENGTH_SHORT).show()
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

    private fun clearInputs() {
        inputCode.text.clear()
        inputName.text.clear()
        inputPrice.text.clear()
        inputQuantity.text.clear()
    }
}
