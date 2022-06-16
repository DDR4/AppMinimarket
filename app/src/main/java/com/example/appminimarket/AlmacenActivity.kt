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
import com.example.appminimarket.modelos.Producto
import com.google.firebase.database.*
import java.util.*

class AlmacenActivity : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var productoRecyclerview : RecyclerView
    private lateinit var productoArrayList : ArrayList<Producto>
    private lateinit var tempProductoArrayList : ArrayList<Producto>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_almacen)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.setTitle("Productos")
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        productoRecyclerview = findViewById(R.id.listaProductos)
        productoRecyclerview.layoutManager = LinearLayoutManager(this)
        productoRecyclerview.setHasFixedSize(true)

        productoArrayList = arrayListOf<Producto>()
        tempProductoArrayList = arrayListOf<Producto>()
        getProductos()
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

                    var adapter = Adapter(tempProductoArrayList)
                    productoRecyclerview.adapter = adapter
                    adapter.setOnItemClickListener(object : Adapter.onItemClickListener{
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