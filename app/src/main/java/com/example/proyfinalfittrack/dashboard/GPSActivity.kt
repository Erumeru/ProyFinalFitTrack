package com.example.proyfinalfittrack.dashboard

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.proyfinalfittrack.R
import com.example.proyfinalfittrack.db.DatabaseHelper
import com.example.proyfinalfittrack.entities.Entrenamiento

class GPSActivity : AppCompatActivity(), LocationListener {

    private lateinit var db: DatabaseHelper
    private lateinit var txtDistanciaRecorrida: TextView
    private lateinit var txtTiempoTranscurrido: TextView
    private lateinit var btnIniciarTerminar: Button
    private lateinit var locationManager: LocationManager
    private var isTracking: Boolean = false
    private var startTimeMillis: Long = 0L
    private var distanciaRecorrida: Float = 0f
    private var idUser: Int = -1
    private var lastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)

        idUser = intent.getSerializableExtra("idUsuario") as? Int ?: -1

        txtDistanciaRecorrida = findViewById(R.id.txtDistanciaRecorrida)
        txtTiempoTranscurrido = findViewById(R.id.txtTiempoTranscurrido)
        btnIniciarTerminar = findViewById(R.id.btnIniciarTerminar)
        db = DatabaseHelper(this)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        iniciarSeguimiento() // Iniciar seguimiento automáticamente al crear la actividad

        btnIniciarTerminar.setOnClickListener {
            detenerSeguimiento()
        }
    }

    private fun iniciarSeguimiento() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_LOCATION
            )
            return
        }

        isTracking = true
        startTimeMillis = System.currentTimeMillis()
        distanciaRecorrida = 0f
        actualizarUI()

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000L,
            0f,
            this
        )
    }

    private fun detenerSeguimiento() {
        isTracking = false
        locationManager.removeUpdates(this)

        val endTimeMillis = System.currentTimeMillis()
        val tiempoTranscurrido = endTimeMillis - startTimeMillis
        val velocidadPromedio = calcularVelocidadPromedio(distanciaRecorrida, tiempoTranscurrido)

        val tipoActividad = determinarTipoActividad(velocidadPromedio)
        val distanciaRecorridaKm = distanciaRecorrida / 1000f

        confirmarActividad()
        actualizarUI()
    }

    private fun confirmarActividad() {
        val velocidadPromedio = calcularVelocidadPromedio(distanciaRecorrida, System.currentTimeMillis() - startTimeMillis)
        val actividadFisica = determinarTipoActividad(velocidadPromedio)

        mostrarDetalles(actividadFisica)
    }

    private fun mostrarDetalles(actividadFisica: String) {
        val distanciaRecorridaKm = distanciaRecorrida / 1000f
        val mensaje = "Distancia recorrida: ${String.format("%.2f", distanciaRecorridaKm)} km\n" +
                "Tiempo transcurrido: ${formatoTiempo(System.currentTimeMillis() - startTimeMillis)}\n" +
                "Actividad física: $actividadFisica"

        AlertDialog.Builder(this)
            .setTitle("Detalles de la actividad")
            .setMessage(mensaje)
            .setPositiveButton("Listo") { dialog, _ ->
                dialog.dismiss()
                guardarEntrenamiento(actividadFisica, distanciaRecorridaKm)
            }
            .show()
    }

    private fun guardarEntrenamiento(tipoActividad: String, distancia: Float) {
        if (idUser != -1) {
            val endTimeMillis = System.currentTimeMillis()
            val entrenamiento = Entrenamiento(idUser, endTimeMillis, tipoActividad, distancia)
            db.insertEntrenamiento(entrenamiento)
            db.selectAllEntrenamientos()
            mostrarToast("Entrenamiento guardado con éxito")

            val dashboardIntent = Intent(this, Dashboard::class.java)
            dashboardIntent.putExtra("idUsuario", idUser)
            startActivity(dashboardIntent)
        } else {
            mostrarToast("Entrenamiento no guardado, lo sentimos.")
        }
    }

    private fun calcularVelocidadPromedio(distancia: Float, tiempoMillis: Long): Float {
        return if (tiempoMillis != 0L) {
            distancia / (tiempoMillis / 1000f)
        } else {
            0f
        }
    }

    private fun determinarTipoActividad(velocidadKmPorHora: Float): String {
        return when {
            velocidadKmPorHora < 5f -> "Caminar"
            velocidadKmPorHora >= 5f && velocidadKmPorHora < 15f -> "Correr"
            else -> "Bici"
        }
    }

    private fun actualizarUI() {
        runOnUiThread {
            val tiempoTranscurridoMillis = System.currentTimeMillis() - startTimeMillis
            val segundos = (tiempoTranscurridoMillis / 1000).toInt()
            val horas = segundos / 3600
            val minutos = (segundos % 3600) / 60
            val segundosRestantes = segundos % 60

            val tiempoFormateado = String.format("%02d:%02d:%02d", horas, minutos, segundosRestantes)
            txtTiempoTranscurrido.text = "Tiempo: $tiempoFormateado"

            txtDistanciaRecorrida.text = "Distancia: ${String.format("%.2f", distanciaRecorrida)}m"
        }
    }

    private fun mostrarToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun formatoTiempo(tiempoMillis: Long): String {
        val segundos = (tiempoMillis / 1000).toInt()
        val horas = segundos / 3600
        val minutos = (segundos % 3600) / 60
        val segundosRestantes = segundos % 60
        return String.format("%02d:%02d:%02d", horas, minutos, segundosRestantes)
    }

    override fun onLocationChanged(location: Location) {
        if (lastLocation != null) {
            distanciaRecorrida += lastLocation!!.distanceTo(location)
        }

        lastLocation = location
        actualizarUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 123
    }
}
