package com.example.appminimarket

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appminimarket.modelos.Producto
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_almacen.*
import kotlinx.android.synthetic.main.activity_mantenimiento_almacen.*
import java.util.*

class MantenimientoAlmacenActivity : AppCompatActivity() {

    val database = Firebase.database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mantenimiento_almacen)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.setTitle("Mantenimiento de Productos")
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        val bundle = intent.extras
        val idProductoEditar = bundle?.getString("idProducto")
        var descripcionEditar = bundle?.getString("descripcion")
        val precioCompraEditar = bundle?.getDouble("precioCompra")
        val precioVentaEditar = bundle?.getDouble("precioVenta")
        val stockEditar = bundle?.getInt("stock")

        if (idProductoEditar != null){
            RecuperarDatos(descripcionEditar,precioCompraEditar,precioVentaEditar,stockEditar)
            btnRegistrar.text = "Editar"
        }

        Ingresar(idProductoEditar)
    }

    private fun Ingresar(idProductoEditar : String?){
        btnRegistrar.setOnClickListener{

            var idProducto = ""
            var desProducto = etDesProducto.text.toString()
            var precioCompra = etPrecioCompra.text.toString().toDouble()
            var precioVenta = etPrecioVenta.text.toString().toDouble()
            var stock = etStock.text.toString().toInt()

            if (idProductoEditar == null){
                idProducto = UUID.randomUUID().toString()
            } else {
                idProducto = idProductoEditar
            }

            val productoentity = database.child("productos")
                .child(idProducto)
            val producto = Producto(idProducto,desProducto,precioCompra,precioVenta,stock)
            productoentity.setValue(producto)
            val almacenIntent : Intent = Intent(this, AlmacenActivity::class.java).apply {
            }
            startActivity(almacenIntent)
        }
    }

    private fun RecuperarDatos(descripcionEditar : String?,precioCompraEditar : Double?,
                               precioVentaEditar : Double?,stockEditar : Int?){
        val descripcion: EditText = findViewById(R.id.etDesProducto)
        val precioCompra: EditText = findViewById(R.id.etPrecioCompra)
        val precioVenta: EditText = findViewById(R.id.etPrecioVenta)
        val stock: EditText = findViewById(R.id.etStock)

        descripcion.setText(descripcionEditar)
        precioCompra.setText(precioCompraEditar.toString())
        precioVenta.setText(precioVentaEditar.toString())
        stock.setText(stockEditar.toString())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}