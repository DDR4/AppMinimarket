package com.example.appminimarket.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appminimarket.R
import com.example.appminimarket.modelos.ProductoOrdenCompra

class ProductoOrdenCompraAdapter(private val listaProductosOC: ArrayList<ProductoOrdenCompra>) : RecyclerView.Adapter<ProductoOrdenCompraAdapter.MyViewHolder>() {

    private lateinit var mlistener : onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.lista_producto_oc_item,
            parent,false)
        return MyViewHolder(itemView,mlistener)
    }

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = listaProductosOC[position]

        holder.descripcion.text = currentitem.descripcion
        holder.cantidad.text = currentitem.cantidad.toString()
        holder.precio.text = currentitem.precio.toString()
    }

    override fun getItemCount(): Int {
        return listaProductosOC.size
    }

    class MyViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val descripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val cantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        val btnBorrar: Button = itemView.findViewById(R.id.btnBorrar)

        init {
            btnBorrar.setOnClickListener(){
                listener.onItemClick(adapterPosition)
            }
        }
    }
}