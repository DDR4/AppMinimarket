package com.example.appminimarket.modelos

data class OrdenCompra(val idOrdenCompra: String? = null, val fechaEntrega: String? = null,
                       val consideracionPago: String? = null, val moneda: String? = null,
                       val listaProductoOC: ArrayList<ProductoOrdenCompra>? = null) {
}