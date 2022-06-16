package com.example.appminimarket.modelos

import android.view.View
import androidx.recyclerview.widget.RecyclerView

data class Producto(val idProducto: String? = null, val descripcion: String? = null,
                    val precioCompra: Double? = null, val precioVenta: Double? = null,
                    val stock: Int? = null) {
}




