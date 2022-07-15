package com.example.appminimarket

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SharedMemory
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header_main.*

enum class ProviderType {
    CORREO,
    GOOGLE
}

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.setTitle("Menu")
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        Ingreso(email ?: "", provider ?: "")

        //Guardando Datos
         val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.putString("email", email)
            prefs.putString("provider", provider)
            prefs.apply()

        }

        private fun Ingreso(email: String, provider: String) {
            val navigationView: NavigationView = findViewById(R.id.nav_view)
            val header = navigationView.getHeaderView(0)
            val correo: TextView = header.findViewById(R.id.nav_header_correo)
            val proveedor: TextView = header.findViewById(R.id.nav_header_proveedor)

            correo.text = email
            proveedor.text = provider

        }

        private fun CerrarSesion(){

            //Borrando Datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            val authIntent : Intent = Intent(this, AuthActivity::class.java).apply {
            }
            startActivity(authIntent)
        }

        private fun Almacen(){
            val almacenIntent : Intent = Intent(this, AlmacenActivity::class.java).apply {
            }
            startActivity(almacenIntent)
        }

        private fun OrdenCompra(){
            val ordenCompraIntent : Intent = Intent(this, OrdenCompraActivity::class.java).apply {
            }
            startActivity(ordenCompraIntent)
        }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
         when (item.itemId) {
             R.id.nav_item_uno -> Almacen()
             R.id.nav_item_dos -> OrdenCompra()
             R.id.nav_item_tres -> CerrarSesion()
         }

         drawer.closeDrawer(GravityCompat.START)
         return true
     }

     override fun onPostCreate(savedInstanceState: Bundle?) {
         super.onPostCreate(savedInstanceState)
         toggle.syncState()
     }

     override fun onConfigurationChanged(newConfig: Configuration) {
         super.onConfigurationChanged(newConfig)
         toggle.onConfigurationChanged(newConfig)
     }

     override fun onOptionsItemSelected(item: MenuItem): Boolean {
         if (toggle.onOptionsItemSelected(item)){
             return true
         }
         return super.onOptionsItemSelected(item)
     }
}