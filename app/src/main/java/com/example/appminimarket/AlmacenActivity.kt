package com.example.appminimarket

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appminimarket.adaptadores.ProductoAdapter
import com.example.appminimarket.modelos.Producto
import com.example.appminimarket.modelos.ProductoOrdenCompra
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class AlmacenActivity : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var productoRecyclerview : RecyclerView
    private lateinit var productoArrayList : ArrayList<Producto>
    private var productoOCArrayList: ArrayList<ProductoOrdenCompra>? = null
    private lateinit var tempProductoArrayList : ArrayList<Producto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_almacen)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.setTitle("Productos")
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        productoArrayList = arrayListOf<Producto>()
        tempProductoArrayList = arrayListOf<Producto>()
        productoOCArrayList = arrayListOf<ProductoOrdenCompra>()

        productoRecyclerview = findViewById(R.id.listaProductos)
        productoRecyclerview.layoutManager = LinearLayoutManager(this)
        productoRecyclerview.setHasFixedSize(true)

        getProductos()

        val bundle = intent.extras
        val registrarProductoOC = bundle?.getBoolean("registrarProductoOC")
        val listaProductoOC = bundle?.getString("listaProductoOC")

        if (registrarProductoOC == true){
            ObtenerListaProductoOCTemporal(listaProductoOC)
        }
    }

    private fun getProductos(){

        dbref = FirebaseDatabase.getInstance().getReference("productos")

        dbref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val producto = userSnapshot.getValue(Producto::class.java)
                        productoArrayList.add(producto!!)
                    }
                    tempProductoArrayList.addAll(productoArrayList)
                    FiltrarListaProductoOCTemporal()

                    var adapter = ProductoAdapter(tempProductoArrayList)
                    productoRecyclerview.adapter = adapter
                    adapter.setOnItemClickListener(object : ProductoAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val producto = productoArrayList[position]
                            MantenimientoProducto(producto)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun MantenimientoProducto(producto: Producto?){
        val bundle = intent.extras
        val registrarProductoOC = bundle?.getBoolean("registrarProductoOC")
        val idOrdenCompraEditar = bundle?.getString("idOrdenCompra")
        var fechaEntregaEditar = bundle?.getString("fechaEntrega")
        val consideracionPagoEditar = bundle?.getString("consideracionPago")
        val monedaEditar = bundle?.getString("moneda")
        val listaProductoOC = bundle?.getString("listaProductoOC")

        if (registrarProductoOC == true){

            val mantenimientoOrdenCompraActivity : Intent = Intent(this,
                MantenimientoOrdenCompraActivity::class.java).apply {
                if (producto != null) {
                    putExtra("idProducto",producto.idProducto.toString())
                    putExtra("descripcion",producto.descripcion)
                    putExtra("cantidadAnterior",producto.stock)
                    putExtra("idOrdenCompra",idOrdenCompraEditar)
                    putExtra("fechaEntrega",fechaEntregaEditar)
                    putExtra("consideracionPago",consideracionPagoEditar)
                    putExtra("moneda",monedaEditar)
                    putExtra("listaProductoOC",listaProductoOC)
                }
            }
            startActivity(mantenimientoOrdenCompraActivity)
        }
        else
        {
            val mantenimientoAlmacenIntent : Intent = Intent(this, MantenimientoAlmacenActivity::class.java).apply {
                if (producto != null) {
                    putExtra("idProducto",producto.idProducto.toString())
                    putExtra("descripcion",producto.descripcion)
                    putExtra("precioCompra",producto.precioCompra)
                    putExtra("precioVenta",producto.precioVenta)
                    putExtra("stock",producto.stock)
                }
            }
            startActivity(mantenimientoAlmacenIntent)
        }
    }

    private fun FiltrarListaProductoOCTemporal(){
        val bundle = intent.extras
        val registrarProductoOC = bundle?.getBoolean("registrarProductoOC")

        if (registrarProductoOC == true){

            var listaProductoAux = productoOCArrayList?.filter {
                it.idProductoOC != null
            }

            if(listaProductoAux?.size!! > 0){

                val listaProductosId = listaProductoAux.map { it.idProductoOC }

                var productoArrayListAux = productoArrayList.filter{
                    it.idProducto !in listaProductosId } as ArrayList<Producto>

                tempProductoArrayList = productoArrayListAux
                productoArrayList = productoArrayListAux
            }
        }
    }

    private fun ObtenerListaProductoOCTemporal(listaProductoOC : String?){
        var delimitador = "ProductoOrdenCompra"
        var delimitador1 = ','
        var delimitador2 = '('
        var delimitador3 = ')'
        var listaProductoOCTemp = listaProductoOC?.split(delimitador)

        for (ProductoOCTempAux in listaProductoOCTemp!!){
            if(ProductoOCTempAux != ""){
                var ProductoOCTemp = ProductoOCTempAux.split(delimitador1,delimitador2,delimitador3)

                var idProductoOC = ""
                var descripcion = ""
                var cantidad = 0
                var cantidadAnterior = 0
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
                                4 -> cantidadAnterior = entity[1].toInt()
                                5 -> precio = entity[1].toDouble()
                            }
                        }
                    }
                    i++
                }
                val producto = ProductoOrdenCompra(idProductoOC, descripcion, cantidad,
                    cantidadAnterior, precio)
                productoOCArrayList?.add(producto)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu_producto, menu)
        val item = menu?.findItem(R.id.btnBuscarProducto)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempProductoArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    productoArrayList.forEach{
                        if (it.descripcion.toString().toLowerCase(Locale.getDefault())
                           .contains(searchText)){
                          tempProductoArrayList.add(it)
                        }
                    }
                    productoRecyclerview.adapter!!.notifyDataSetChanged()
                }else{
                    tempProductoArrayList.clear()
                    tempProductoArrayList.addAll(productoArrayList)
                    productoRecyclerview.adapter!!.notifyDataSetChanged()
                }
                return false
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (item.itemId) {
            R.id.btnRegistrarProducto -> MantenimientoProducto(null)
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}