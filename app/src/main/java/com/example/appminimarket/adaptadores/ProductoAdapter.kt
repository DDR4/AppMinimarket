package com.example.appminimarket.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appminimarket.R
import com.example.appminimarket.modelos.Producto

class ProductoAdapter(private val listaProductos : ArrayList<Producto>) : RecyclerView.Adapter<ProductoAdapter.MyViewHolder>() {

    private lateinit var mlistener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.lista_producto_item,
        parent,false)
        return MyViewHolder(itemView,mlistener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = listaProductos[position]

        holder.descripcion.text = currentitem.descripcion
        holder.precioCompra.text = currentitem.precioCompra.toString()
        holder.precioVenta.text = currentitem.precioVenta.toString()
        holder.stock.text = currentitem.stock.toString()
    }

    override fun getItemCount(): Int {
        return listaProductos.size
    }

    class MyViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val descripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val precioCompra: TextView = itemView.findViewById(R.id.tvPrecioCompra)
        val precioVenta: TextView = itemView.findViewById(R.id.tvPrecioVenta)
        val stock: TextView = itemView.findViewById(R.id.tvStock)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }
}