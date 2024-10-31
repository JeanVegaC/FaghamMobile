package com.example.faghamsac.modules.invoice.ui
import CotizacionItem
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.faghamsac.R
import com.example.faghamsac.modules.invoice.model.InvoiceItem

class ProductsAdapter(private val items: MutableList<CotizacionItem>) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textViewProductCode: TextView = itemView.findViewById(R.id.textViewProductCode)
        val textViewProductName: TextView = itemView.findViewById(R.id.textViewProductName)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textViewQuantity)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val buttonDeleteProduct: ImageView = itemView.findViewById(R.id.buttonDeleteProduct) // Asegúrate de que este ID coincida con el XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]
        Log.d("RecyclerView", "Binding item at position $position: $item")


        holder.textViewProductCode.text = item.codigoProducto
        holder.textViewProductName.text = item.nombreProducto
        holder.textViewQuantity.text = item.cantidad.toString()
        holder.textViewPrice.text = item.precioVenta.toString()

        holder.buttonDeleteProduct.setOnClickListener {
            // Lógica para eliminar el producto
            removeAt(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun removeAt(position: Int) {
        items.removeAt(position) // Asegúrate de usar el nombre correcto de tu lista
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }
}