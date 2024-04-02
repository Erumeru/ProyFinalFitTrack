package com.example.proyfinalfittrack.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.proyfinalfittrack.R
import com.example.proyfinalfittrack.db.DatabaseHelper
import com.example.proyfinalfittrack.entities.Entrenamiento

class dashboard : AppCompatActivity() {

    var nombreUsuario: String = "Undefined"
    private lateinit var tvNombre : TextView
    private lateinit var btnCaliz: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        tvNombre=findViewById(R.id.tvNombre)
        nombreUsuario=intent.getSerializableExtra("nombreUsuario") as String
        btnCaliz=findViewById(R.id.btnCaliz)



        dbHelper= DatabaseHelper(this)
        tvNombre.setText(nombreUsuario)

        btnCaliz.setOnClickListener(){
            val idUser=intent.getSerializableExtra("idUsuario") as Int
            val currentTimeMillis = System.currentTimeMillis()
            var entrenamiento=Entrenamiento(idUser,currentTimeMillis,"caminata",200f)

            dbHelper.insertEntrenamiento(entrenamiento)
            dbHelper.selectAllEntrenamientos()
        }
    }




}