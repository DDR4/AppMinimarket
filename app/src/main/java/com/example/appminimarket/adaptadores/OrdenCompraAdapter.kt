package com.example.appminimarket.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appminimarket.R
import com.example.appminimarket.modelos.OrdenCompra
import com.example.appminimarket.modelos.Producto

class OrdenCompraAdapter(private val listaOrdenesCompra : ArrayList<OrdenCompra>) : RecyclerView.Adapter<OrdenCompraAdapter.MyViewHolder>() {

    private lateinit var mlistener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.lista_orden_compra_item,
        parent,false)
        return MyViewHolder(itemView,mlistener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = listaOrdenesCompra[position]

        holder.fechaEntrega.text = currentitem.fechaEntrega
        holder.consideracionPago.text = currentitem.consideracionPago
        holder.moneda.text = currentitem.moneda
    }

    override fun getItemCount(): Int {
        return listaOrdenesCompra.size
    }

    class MyViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val fechaEntrega: TextView = itemView.findViewById(R.id.tvFechaEntrega)
        val consideracionPago: TextView = itemView.findViewById(R.id.tvConsideracionPago)
        val moneda: TextView = itemView.findViewById(R.id.tvMoneda)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }
}