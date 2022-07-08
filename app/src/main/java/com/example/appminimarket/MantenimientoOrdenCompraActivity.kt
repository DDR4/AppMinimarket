package com.example.appminimarket

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appminimarket.adaptadores.ProductoOrdenCompraAdapter
import com.example.appminimarket.modelos.OrdenCompra
import com.example.appminimarket.modelos.ProductoOrdenCompra
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_mantenimiento_orden_compra.*
import kotlinx.android.synthetic.main.activity_mantenimiento_orden_compra.etDesProducto
import java.util.*

class MantenimientoOrdenCompraActivity : AppCompatActivity() {

    val database = Firebase.database.reference
    private lateinit var dbref : DatabaseReference
    private lateinit var productoOCRecyclerview: RecyclerView
    private var productoOCArrayList: ArrayList<ProductoOrdenCompra>? = null
    private lateinit var ordenCompraArrayList : ArrayList<OrdenCompra>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mantenimiento_orden_compra)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.setTitle("Mantenimiento Orden Compra")
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        ordenCompraArrayList = arrayListOf<OrdenCompra>()
        InicializarListaProductoOC()
        RefrescarListaProductoOC(productoOCArrayList)

        val bundle = intent.extras
        val idOrdenCompraEditar = bundle?.getString("idOrdenCompra")
        var fechaEntregaEditar = bundle?.getString("fechaEntrega")
        val consideracionPagoEditar = bundle?.getString("consideracionPago")
        val monedaEditar = bundle?.getString("moneda")
        val idProducto = bundle?.getString("idProducto")
        val descripcionProdcto = bundle?.getString("descripcion")
        val listaProductoOC = bundle?.getString("listaProductoOC")

        if (listaProductoOC != null && listaProductoOC != ""){
            ObtenerListaProductoOCTemporal(listaProductoOC)
        }

        val moneda = resources.getStringArray(R.array.sp_moneda)
        if (spMoneda != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moneda)
            spMoneda.adapter = adapter
            if(monedaEditar != null){
                val spinnerPosition: Int = adapter.getPosition(monedaEditar)
                spMoneda.setSelection(spinnerPosition)
            }
        }

        if (idOrdenCompraEditar != null){
            RecuperarDatos(fechaEntregaEditar,consideracionPagoEditar)
            ObtenerListaProductoOC(idOrdenCompraEditar)
            btnNuevaOC.text = "Editar"
        }

        if(idProducto != null){
            val descripcion: EditText = findViewById(R.id.etDesProducto)
            descripcion.setText(descripcionProdcto)
            descripcion.isEnabled = false
            btnNuevo.setBackgroundColor(Color.RED)
            btnNuevo.text = "Cancelar Registro"
            /*if (idOrdenCompraEditar != null){
                ObtenerListaProductoOC(idOrdenCompraEditar)
            }*/
        }

        InicializarFechaEntrega()
        AgregarProductoOC()
        AgregarOrdenCompra(idOrdenCompraEditar)
        RegistrarProductoOC(idOrdenCompraEditar)
    }

    private fun InicializarFechaEntrega(){

        etFechaEntrega.setOnClickListener{
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this@MantenimientoOrdenCompraActivity,
            { view, year, monthOfYear, dayOfMonth ->

                var dia =  dayOfMonth.toString()
                var mes =  (monthOfYear + 1).toString()

                if (dia.length == 1){
                    dia = "0" + dia
                }

                if (mes.length == 1){
                    mes = "0" + mes
                }

                etFechaEntrega.setText(dia + "/" + mes + "/" + year)

            }, year, month, day)

            dpd.show()
        }
    }

    private fun InicializarListaProductoOC(){
        productoOCRecyclerview = findViewById(R.id.listaProductosOC)
        productoOCRecyclerview.layoutManager = LinearLayoutManager(this)
        productoOCRecyclerview.setHasFixedSize(true)

        if (productoOCArrayList == null){
            productoOCArrayList = arrayListOf<ProductoOrdenCompra>()
        }
    }

    private fun RefrescarListaProductoOC(productoOCArrayList: ArrayList<ProductoOrdenCompra>?){
        val productoOCAdapter = ProductoOrdenCompraAdapter(productoOCArrayList,
            object : ProductoOrdenCompraAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    productoOCArrayList?.removeAt(position)
                    RefrescarListaProductoOC(productoOCArrayList)
                }
            }
        )
        productoOCRecyclerview.adapter = productoOCAdapter
    }

    private fun AgregarProductoOC() {
        btnNuevo.setOnClickListener {
            val bundle = intent.extras
            val idProducto = bundle?.getString("idProducto")
            if (idProducto == null){
                var idProductoOC = UUID.randomUUID().toString()
                InsertarProductoOC(idProductoOC)
            }
            else
            {
                LimpiarProductoExistentesOC()
                LimpiarProductoOC()
            }
        }
    }

    private fun RegistrarProductoOC(idOrdenCompraEditar : String?){
        btnRegistrar.setOnClickListener{
            val bundle = intent.extras
            val idProducto = bundle?.getString("idProducto")
            if(idProducto != null) {
                InsertarProductoOC(idProducto)
                LimpiarProductoExistentesOC()
            }
            else
            {
                val almacenIntent : Intent = Intent(this, AlmacenActivity::class.java).apply {
                    putExtra("registrarProductoOC",true)
                    putExtra("idOrdenCompra",idOrdenCompraEditar)
                    putExtra("fechaEntrega",etFechaEntrega.text.toString())
                    putExtra("consideracionPago", etConsideracionPago.text.toString())
                    putExtra("moneda",spMoneda.selectedItem.toString())
                    putExtra("listaProductoOC",productoOCArrayList?.joinToString())
                }
                startActivity(almacenIntent)
            }
        }
    }

    private fun InsertarProductoOC(idProductoOC : String?){

        var descripcion = etDesProducto.text.toString()
        var cantidad = etCantidad.text.toString().toInt()
        var precio = etPrecio.text.toString().toDouble()

        val producto = ProductoOrdenCompra(idProductoOC, descripcion, cantidad, precio)
        productoOCArrayList?.add(producto)

        RefrescarListaProductoOC(productoOCArrayList)
        LimpiarProductoOC()
    }

    private fun LimpiarProductoOC(){
        val descripcion: EditText = findViewById(R.id.etDesProducto)
        val cantidad: EditText = findViewById(R.id.etCantidad)
        val precio: EditText = findViewById(R.id.etPrecio)

        descripcion.setText("")
        cantidad.setText("")
        precio.setText("")
    }

    private fun LimpiarProductoExistentesOC(){
        val descripcion: EditText = findViewById(R.id.etDesProducto)
        getIntent().removeExtra("idProducto")
        getIntent().removeExtra("descripcion")
        descripcion.isEnabled = true
        btnNuevo.setBackgroundColor(Color.rgb(30,144,255))
        btnNuevo.text = "Nuevo Producto"
    }

    private fun AgregarOrdenCompra(idOrdenCompraEditar : String?){
        btnNuevaOC.setOnClickListener{
            var idOrdenCompra = ""
            var fechaEntrega = etFechaEntrega.text.toString()
            var consideracionPago = etConsideracionPago.text.toString()
            var moneda = spMoneda.selectedItem.toString()

            if (idOrdenCompraEditar == null){
                idOrdenCompra = UUID.randomUUID().toString()
            } else {
                idOrdenCompra = idOrdenCompraEditar
            }

            val ordenCompraentity = database.child("ordenesCompra")
                .child(idOrdenCompra)
            val ordenCompra = OrdenCompra(idOrdenCompra,fechaEntrega,consideracionPago,moneda,
                                productoOCArrayList)
            ordenCompraentity.setValue(ordenCompra)
            val ordenCompraIntent : Intent = Intent(this, OrdenCompraActivity::class.java).apply {
            }
            startActivity(ordenCompraIntent)
        }
    }

    private fun RecuperarDatos(fechaEntregaEditar : String?,consideracionPagoEditar : String?){
        val fechaEntrega: EditText = findViewById(R.id.etFechaEntrega)
        val consideracionPago: EditText = findViewById(R.id.etConsideracionPago)

        fechaEntrega.setText(fechaEntregaEditar)
        consideracionPago.setText(consideracionPagoEditar)
    }

    private fun ObtenerListaProductoOC(idOrdenCompraEditar : String?){

        dbref = FirebaseDatabase.getInstance().getReference("ordenesCompra")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(productoOCArrayList?.size == 0) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val ordenCompra = userSnapshot.getValue(OrdenCompra::class.java)
                            ordenCompraArrayList.add(ordenCompra!!)
                        }
                        var ordenCompra: OrdenCompra = ordenCompraArrayList.filter {
                            it.idOrdenCompra == idOrdenCompraEditar
                        }.single()
                        var listaProductoOC = ordenCompra.listaProductoOC

                        productoOCArrayList = listaProductoOC
                        RefrescarListaProductoOC(productoOCArrayList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun ObtenerListaProductoOCTemporal(listaProductoOC : String){
        InicializarListaProductoOC()
        var delimitador = "ProductoOrdenCompra"
        var delimitador1 = ','
        var delimitador2 = '('
        var delimitador3 = ')'
        var listaProductoOCTemp = listaProductoOC.split(delimitador)

        for (ProductoOCTempAux in listaProductoOCTemp){
            if(ProductoOCTempAux != ""){
                var ProductoOCTemp = ProductoOCTempAux.split(delimitador1,delimitador2,delimitador3)

                var idProductoOC = ""
                var descripcion = ""
                var cantidad = 0
                var precio = 0.0

                var i = 0
                for (item in ProductoOCTemp){
                    var itemAux = item.trim()
                    var delimitador = '='
                    if (itemAux != ""){
                        var entity = itemAux.split(delimitador)

                        if(itemAux.contains(entity[0])){
                            when(i){
                                1 -> idProductoOC = entity[1]
                                2 -> descripcion = entity[1]
                                3 -> cantidad = entity[1].toInt()
                                4 -> precio = entity[1].toDouble()
                            }
                        }
                    }
                    i++
                }
                val producto = ProductoOrdenCompra(idProductoOC, descripcion, cantidad, precio)
                productoOCArrayList?.add(producto)
            }
        }
        RefrescarListaProductoOC(productoOCArrayList)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}