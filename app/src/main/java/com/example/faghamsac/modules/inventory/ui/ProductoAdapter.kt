package com.example.faghamsac.modules.inventory.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.faghamsac.R
import com.example.faghamsac.modules.invoice.model.Product

class ProductoAdapter(private val productos: List<Product>) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val codigoTextView: TextView = itemView.findViewById(R.id.codigoTextView)
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        val precioTextView: TextView = itemView.findViewById(R.id.precioTextView)
        val cantidadTextView: TextView = itemView.findViewById(R.id.cantidadTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_product, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        holder.codigoTextView.text = producto.code
        holder.nombreTextView.text = producto.name
        holder.cantidadTextView.text = producto.quantity.toString()
        holder.precioTextView.text = "Precio: $${producto.price}"
    }

    override fun getItemCount(): Int = productos.size
}
