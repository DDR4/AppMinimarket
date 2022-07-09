package com.example.appminimarket

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appminimarket.adaptadores.OrdenCompraAdapter
import com.example.appminimarket.adaptadores.ProductoAdapter
import com.example.appminimarket.modelos.OrdenCompra
import com.example.appminimarket.modelos.Producto
import com.google.firebase.database.*
import java.util.*

class OrdenCompraActivity : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var ordenCompraRecyclerview : RecyclerView
    private lateinit var ordenCompraArrayList : ArrayList<OrdenCompra>
    private lateinit var tempOrdenCompraArrayList : ArrayList<OrdenCompra>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orden_compra)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.setTitle("Orden Compra")
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        ordenCompraRecyclerview = findViewById(R.id.listaOrdenesCompra)
        ordenCompraRecyclerview.layoutManager = LinearLayoutManager(this)
        ordenCompraRecyclerview.setHasFixedSize(true)

        ordenCompraArrayList = arrayListOf<OrdenCompra>()
        tempOrdenCompraArrayList = arrayListOf<OrdenCompra>()
        getOrdenesCompra()
    }

    private fun getOrdenesCompra(){

        dbref = FirebaseDatabase.getInstance().getReference("ordenesCompra")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val ordenCompra = userSnapshot.getValue(OrdenCompra::class.java)
                        ordenCompraArrayList.add(ordenCompra!!)
                    }

                    tempOrdenCompraArrayList.addAll(ordenCompraArrayList)

                    var adapter = OrdenCompraAdapter(tempOrdenCompraArrayList)
                    ordenCompraRecyclerview.adapter = adapter
                    adapter.setOnItemClickListener(object : OrdenCompraAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val ordenCompra = ordenCompraArrayList[position]
                            MantenimientoOrdenCompra(ordenCompra)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun MantenimientoOrdenCompra(ordenCompra: OrdenCompra?){
        val mantenimientoOrdenCompraActivity : Intent = Intent(this, MantenimientoOrdenCompraActivity::class.java).apply {
            if (ordenCompra != null) {
                putExtra("idOrdenCompra",ordenCompra.idOrdenCompra.toString())
                putExtra("fechaEntrega",ordenCompra.fechaEntrega)
                putExtra("consideracionPago",ordenCompra.consideracionPago)
                putExtra("moneda",ordenCompra.moneda)
            }
        }
        startActivity(mantenimientoOrdenCompraActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu_orden_compra, menu)
        val item = menu?.findItem(R.id.btnBuscarOrdenCompra)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempOrdenCompraArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    ordenCompraArrayList.forEach{
                        if (it.fechaEntrega.toString().toLowerCase(Locale.getDefault())
                                .contains(searchText)){
                            tempOrdenCompraArrayList.add(it)
                        }
                    }
                    ordenCompraRecyclerview.adapter!!.notifyDataSetChanged()
                }else{
                    tempOrdenCompraArrayList.clear()
                    tempOrdenCompraArrayList.addAll(ordenCompraArrayList)
                    ordenCompraRecyclerview.adapter!!.notifyDataSetChanged()
                }
                return false
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val homeIntent : Intent = Intent(this, HomeActivity::class.java).apply {
        }
        val id = item.itemId
        when (item.itemId) {
            R.id.btnRegistrarOrdenCompra -> MantenimientoOrdenCompra(null)
            android.R.id.home -> startActivity(homeIntent)
        }
        return true
    }
}