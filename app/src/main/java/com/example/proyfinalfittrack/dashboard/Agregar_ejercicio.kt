package com.example.proyfinalfittrack.dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.proyfinalfittrack.R
import com.example.proyfinalfittrack.db.DatabaseHelper
import com.example.proyfinalfittrack.entities.Entrenamiento

class Agregar_ejercicio : AppCompatActivity() {

    private lateinit var imgCaminar:ImageView
    private lateinit var imgCorrer:ImageView
    private lateinit var imgBici:ImageView

    private lateinit var tvCaminar: TextView
    private lateinit var tvCorrer: TextView
    private lateinit var tvBici: TextView

    private lateinit var btnRegister: Button
    private lateinit var btnGps: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextDistancia: EditText

    private var lastLocation: Location? = null
    private var distanciaTotalGps: Float = 0.0f
    private lateinit var locationManager: LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_ejercicio)

        val colorSeleccionado = getResources().getColor(R.color.verdePrincipal)
        var tipo="nada"
        val idUser=intent.getSerializableExtra("idUsuario") as Int
        // configuraciones de variables
        cargarTitulosYActividades()
        cargarGifs()







        imgCaminar.setOnClickListener(){
            if(tvCaminar.currentTextColor!=colorSeleccionado) {
                tvCaminar.setTextColor(colorSeleccionado)
                tipo="caminar"
                tvCorrer.setTextColor(getResources().getColor(R.color.white))
                tvBici.setTextColor(getResources().getColor(R.color.white))
            }else{
                tvCaminar.setTextColor(getResources().getColor(R.color.white))
                tipo="nada"
            }
        }

        imgCorrer.setOnClickListener(){
            if(tvCorrer.currentTextColor!=colorSeleccionado) {
                tvCorrer.setTextColor(colorSeleccionado)
                tipo="correr"
                tvCaminar.setTextColor(getResources().getColor(R.color.white))
                tvBici.setTextColor(getResources().getColor(R.color.white))
            }else{
                tvCorrer.setTextColor(getResources().getColor(R.color.white))
                tipo="nada"
            }
        }

        imgBici.setOnClickListener(){
            if(tvBici.currentTextColor!=colorSeleccionado) {
                tvBici.setTextColor(colorSeleccionado)
                tipo="bici"
                tvCorrer.setTextColor(getResources().getColor(R.color.white))
                tvCaminar.setTextColor(getResources().getColor(R.color.white))
            }else{
                tvBici.setTextColor(getResources().getColor(R.color.white))
                tipo="nada"
            }
        }

        restrictToNumbers(editTextDistancia)

        btnRegister.setOnClickListener(){
            if(tipo.equals("nada")){
                showAlert(this, "Error en el tipo\nde entrenamiento", "Ingrese un tipo de entrenamiento.")
                return@setOnClickListener
            }
            if(editTextDistancia.text.isEmpty() || editTextDistancia.text.toString().equals(".") || editTextDistancia.text.toString().toFloat()<=0){
                showAlert(this, "Error en la distancia", "Ingrese una distancia.")
                return@setOnClickListener
            }

            println(editTextDistancia.text.toString())
            dbHelper= DatabaseHelper(this)
            val currentTimeMillis = System.currentTimeMillis()
            var entrenamiento= Entrenamiento(idUser,currentTimeMillis,tipo,editTextDistancia.text.toString().toFloat())
            dbHelper.insertEntrenamiento(entrenamiento)
            dbHelper.selectAllEntrenamientos()

            val dashboard= Intent(this,Dashboard::class.java)
            dashboard.putExtra("nombreUsuario", intent.getSerializableExtra("nombreUsuario") as String)
            dashboard.putExtra("idUsuario", idUser)
            startActivity(dashboard)


        }

        btnGps.setOnClickListener(){
            editTextDistancia.isEnabled=!(editTextDistancia.isEnabled)
            if(!editTextDistancia.isEnabled){
                btnGps.text = "Gps On"
                val LOCATION_PERMISSION_REQUEST_CODE = 1001

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // La aplicación tiene el permiso necesario
                    // Realiza la operación que requiere el permiso
                    locationManager  = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)

                } else {
                    // El permiso no está otorgado, solicítalo al usuario
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                }
            }else{
                locationManager.removeUpdates(locationListener)
                btnGps.text = "Gps Off"
            }

        }

    }

    override fun onBackPressed() {

        if (::locationManager.isInitialized) {
            locationManager.removeUpdates(locationListener)
        }
        super.onBackPressed()
    }

    val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Calcular distancia y velocidad
            if(lastLocation!=null){
                val distancia=lastLocation!!.distanceTo(location)
                distanciaTotalGps+=distancia
                lastLocation=location
                println("distancia: $distancia,  distanciatotal: ${distanciaTotalGps/1000.0f} km")
                editTextDistancia.setText((distanciaTotalGps/1000.0f).toString())
            }else{
                //si no hay localizacion antes, actualizar
                lastLocation=location
            }
            // Actualizar UI con la nueva ubicación, distancia y velocidad
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }
    }



    fun showAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar") { dialog, _ ->

            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    fun restrictToNumbers(editText: EditText) {
        val filter = InputFilter { source, _, _, dest, _, _ ->
            // Solo permite caracteres que son dígitos o el punto decimal
            if (source.toString().matches("[0-9.]*".toRegex())) {
                null
            } else {
                ""
            }
        }
        editText.filters = arrayOf(filter)
    }

    fun cargarTitulosYActividades(){
        imgCaminar=findViewById(R.id.icon_caminar)
        imgCorrer=findViewById(R.id.icon_correr)
        imgBici=findViewById(R.id.icon_bici)
        //btnregistrar
        btnRegister=findViewById(R.id.btnRegistrar)
        //btnGps
        btnGps=findViewById(R.id.btnGps)
        //EditText
        editTextDistancia=findViewById(R.id.editTextDistancia)

        tvCaminar=findViewById(R.id.tvCaminar)
        tvCorrer=findViewById(R.id.tvCorrer)
        tvBici=findViewById(R.id.tvBici)

        tvCaminar.setTextColor(getResources().getColor(R.color.white))
        tvCorrer.setTextColor(getResources().getColor(R.color.white))
        tvBici.setTextColor(getResources().getColor(R.color.white))

    }

    fun cargarGifs(){
        val bici: ImageView =findViewById(R.id.icon_bici)
        val caminar:ImageView=findViewById(R.id.icon_caminar)
        val correr: ImageView=findViewById(R.id.icon_correr)
        Glide.with(this).load(R.drawable.icon_select_bici).into(bici)
        Glide.with(this).load(R.drawable.icon_select_caminar).into(caminar)
        Glide.with(this).load(R.drawable.icon_select_correr).into(correr)
    }
}