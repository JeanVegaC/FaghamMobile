package com.example.faghamsac.modules.inventory.services
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.util.Log
import com.example.faghamsac.modules.invoice.model.Product

class ProductService {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun addOrUpdateProduct(product: Product, onComplete: (Boolean) -> Unit) {
        database.child("productos").child(product.code).setValue(product)
            .addOnSuccessListener {
                onComplete(true) // Exitoso
            }
            .addOnFailureListener { exception ->
                Log.e("ProductService", "Error al agregar/editar producto", exception)
                onComplete(false) // Error
            }
    }

    fun deleteProduct(code: String, onComplete: (Boolean) -> Unit) {
        database.child("productos").child(code).removeValue()
            .addOnSuccessListener {
                onComplete(true) // Exitoso
            }
            .addOnFailureListener { exception ->
                Log.e("ProductService", "Error al eliminar producto", exception)
                onComplete(false) // Error
            }
    }

    fun getAllProductos(onProductosRetrieved: (List<Product>) -> Unit) {
        database.child("productos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productosList = mutableListOf<Product>()
                for (productoSnapshot in snapshot.children) {
                    val producto = productoSnapshot.getValue(Product::class.java)

                    producto?.let {
                        val codigo = productoSnapshot.key ?: ""
                        productosList.add(it.copy(code = codigo))
                    }
                }
                onProductosRetrieved(productosList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProductoService", "Error al leer los datos de Firebase", error.toException())
            }
        })
    }



    fun getProduct(code: String, onProductReceived: (Product?) -> Unit) {
        database.child("productos").child(code).get()
            .addOnSuccessListener { snapshot ->
                val product = snapshot.getValue(Product::class.java)
                onProductReceived(product)
            }
            .addOnFailureListener { exception ->
                Log.e("ProductService", "Error al obtener producto", exception)
                onProductReceived(null)
            }
    }
}