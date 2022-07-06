package com.example.appminimarket

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appminimarket.adaptadores.ProductoOrdenCompraAdapter
import com.example.appminimarket.modelos.OrdenCompra
import com.example.appminimarket.modelos.ProductoOrdenCompra
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_mantenimiento_orden_compra.*
import java.util.*

class MantenimientoOrdenCompraActivity : AppCompatActivity() {

    val database = Firebase.database.reference
    private lateinit var productoOCRecyclerview: RecyclerView
    private lateinit var productoOCArrayList: java.util.ArrayList<ProductoOrdenCompra>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mantenimiento_orden_compra)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.setTitle("Mantenimiento Orden Compra")
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        productoOCRecyclerview = findViewById(R.id.listaProductosOC)
        productoOCRecyclerview.layoutManager = LinearLayoutManager(this)
        productoOCRecyclerview.setHasFixedSize(true)

        productoOCArrayList = arrayListOf<ProductoOrdenCompra>()

        val moneda = resources.getStringArray(R.array.sp_moneda)
        val spMoneda = findViewById<Spinner>(R.id.spMoneda)
        if (spMoneda != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moneda)
            spMoneda.adapter = adapter
        }

        AgregarProductoOC()
        AgregarOrdenCompra()
    }

    private fun AgregarProductoOC() {
        btnNuevo.setOnClickListener {
            var idProductoOC = UUID.randomUUID().toString()
            var descripcion = etDesProducto.text.toString()
            var cantidad = etCantidad.text.toString().toInt()
            var precio = etPrecio.text.toString().toDouble()

            val producto = ProductoOrdenCompra(idProductoOC, descripcion, cantidad, precio)
            productoOCArrayList.add(producto)

            LimpiarProductoOC()

            val productoOCAdapter = ProductoOrdenCompraAdapter(productoOCArrayList)
            productoOCRecyclerview.adapter = productoOCAdapter

            productoOCAdapter.setOnItemClickListener(object :
                ProductoOrdenCompraAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    productoOCArrayList.removeAt(position)
                    var productoOCArrayListAux: java.util.ArrayList<ProductoOrdenCompra>
                    productoOCArrayListAux = arrayListOf<ProductoOrdenCompra>()

                    productoOCArrayList.forEach{
                        productoOCArrayListAux.add(it)
                    }

                    productoOCArrayList = arrayListOf<ProductoOrdenCompra>()
                    productoOCArrayList = productoOCArrayListAux

                    val productoOCAdapter = ProductoOrdenCompraAdapter(productoOCArrayList)
                    productoOCRecyclerview.adapter = productoOCAdapter
                }
            })
        }
    }

    private fun LimpiarProductoOC(){
        val descripcion: EditText = findViewById(R.id.etDesProducto)
        val cantidad: EditText = findViewById(R.id.etCantidad)
        val precio: EditText = findViewById(R.id.etPrecio)

        descripcion.setText("")
        cantidad.setText("")
        precio.setText("")
    }

    private fun AgregarOrdenCompra(){
        btnNuevaOC.setOnClickListener{
            var idOrdenCompra = UUID.randomUUID().toString()
            var fechaEntrega = etFechaEntrega.text.toString()
            var consideracionPago = etConsideracionPago.text.toString()
            var moneda = spMoneda.selectedItem.toString()

            val ordenCompraentity = database.child("ordenesCompra")
                .child(idOrdenCompra)
            val ordenCompra = OrdenCompra(idOrdenCompra,fechaEntrega,consideracionPago,moneda,productoOCArrayList)
            ordenCompraentity.setValue(ordenCompra)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}