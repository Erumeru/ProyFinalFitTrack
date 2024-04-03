package com.example.proyfinalfittrack.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.proyfinalfittrack.R
import com.example.proyfinalfittrack.db.DatabaseHelper
import com.example.proyfinalfittrack.entities.Entrenamiento
import com.example.proyfinalfittrack.sesiones.activityLogin

class Dashboard : AppCompatActivity() {

    var nombreUsuario: String = "Undefined"
    private lateinit var tvNombre : TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var agregarEjercicio: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        tvNombre=findViewById(R.id.tvNombre)
        nombreUsuario=intent.getSerializableExtra("nombreUsuario") as String
        agregarEjercicio=findViewById(R.id.agregarEjercicio)



        dbHelper= DatabaseHelper(this)
        tvNombre.setText(nombreUsuario)

        agregarEjercicio.setOnClickListener(){


            val agregar= Intent(this, Agregar_ejercicio::class.java)
            agregar.putExtra("idUsuario",intent.getSerializableExtra("idUsuario") as Int)
            startActivity(agregar)

         //   val idUser=intent.getSerializableExtra("idUsuario") as Int
          //  val currentTimeMillis = System.currentTimeMillis()
         //   var entrenamiento=Entrenamiento(idUser,currentTimeMillis,"caminata",100f)
         //   dbHelper.insertEntrenamiento(entrenamiento)
          //  dbHelper.selectAllEntrenamientos()
        }
    }




}