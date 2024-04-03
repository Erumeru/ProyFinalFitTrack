package com.example.proyfinalfittrack.dashboard

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextDistancia: EditText

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
            println(editTextDistancia.text.toString())
               dbHelper= DatabaseHelper(this)
               val currentTimeMillis = System.currentTimeMillis()
               var entrenamiento= Entrenamiento(idUser,currentTimeMillis,tipo,editTextDistancia.text.toString().toFloat())
               dbHelper.insertEntrenamiento(entrenamiento)
               dbHelper.selectAllEntrenamientos()

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