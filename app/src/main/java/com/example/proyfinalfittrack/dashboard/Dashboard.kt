package com.example.proyfinalfittrack.dashboard

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import com.example.proyfinalfittrack.R
import com.example.proyfinalfittrack.db.DatabaseHelper
import com.example.proyfinalfittrack.entities.Entrenamiento
import com.example.proyfinalfittrack.sesiones.activityLogin
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Dashboard : AppCompatActivity() {

    var nombreUsuario: String = "Undefined"
    private lateinit var tvNombre : TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var agregarEjercicio: ImageView
    private lateinit var listaEnt: List<Entrenamiento>

    private lateinit var tvSumaCorrer:TextView
    private lateinit var tvSumaCaminar:TextView
    private lateinit var tvSumaBici:TextView

    private lateinit var linearCaminar: LinearLayout
    private lateinit var viewBarraCaminar: View
    private lateinit var viewBarraCorrer: View
    private lateinit var viewBarraBici: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        tvNombre=findViewById(R.id.tvNombre)
        nombreUsuario=intent.getSerializableExtra("nombreUsuario") as String
        agregarEjercicio=findViewById(R.id.agregarEjercicio)
        linearCaminar=findViewById(R.id.linearCaminarDistanciaBarra)
        viewBarraCaminar=findViewById(R.id.viewBarraCaminar)
        viewBarraCorrer=findViewById(R.id.viewBarraCorrer)
        viewBarraBici=findViewById(R.id.viewBarraBici)

        dbHelper= DatabaseHelper(this)
        tvNombre.setText(nombreUsuario)

        cargarTextViews()
        cargarEntrenamientos()

        val distanciaTotalRecorrida=sumaTotalDistancias()
        val maxWidth=tvSumaCaminar.layoutParams.width
        println("dadsd $maxWidth")

        linearCaminar.post{
            cargarDistanciasRecorridas(tvSumaCaminar, listaEnt, "caminar", viewBarraCaminar,distanciaTotalRecorrida)
            cargarDistanciasRecorridas(tvSumaCorrer, listaEnt, "correr", viewBarraCorrer,distanciaTotalRecorrida)
            cargarDistanciasRecorridas(tvSumaBici, listaEnt, "bici", viewBarraBici,distanciaTotalRecorrida)
        }


        agregarEjercicio.setOnClickListener(){
            val agregar= Intent(this, Agregar_ejercicio::class.java)
            agregar.putExtra("idUsuario",intent.getSerializableExtra("idUsuario") as Int)
            agregar.putExtra("nombreUsuario", intent.getSerializableExtra("nombreUsuario") as String)
            startActivity(agregar)
         //   val idUser=intent.getSerializableExtra("idUsuario") as Int
          //  val currentTimeMillis = System.currentTimeMillis()
         //   var entrenamiento=Entrenamiento(idUser,currentTimeMillis,"caminata",100f)
         //   dbHelper.insertEntrenamiento(entrenamiento)
          //  dbHelper.selectAllEntrenamientos()
        }
    }

    fun porcentajeWidthEntrenamiento(view: View, distancia: Float, distanciaTotal: Float){

        val linearLayout = linearCaminar

            // El ancho de la vista ya se ha calculado
            val maxWidth = linearLayout.width
            println("maxwidht: $maxWidth")
            // Utiliza el valor de width aquí
            val params=view.layoutParams
            println("total $distanciaTotal")
            val widthCalculado=maxWidth*(distancia/distanciaTotal)
            if(widthCalculado.toInt()<=0){
                params.width=0
                println(params.width.toString())
                view.layoutParams=params
            }else{
                params.width=widthCalculado.toInt()
                println(params.width.toString())
                view.layoutParams=params
            }

    }

    fun cargarTextViews(){
        tvSumaCaminar=findViewById(R.id.tvSumaCaminar)
        tvSumaBici=findViewById(R.id.tvSumaBici)
        tvSumaCorrer=findViewById(R.id.tvSumaCorrer)
    }

    fun sumaTotalDistancias():Float{
        var distanciaTotal=0.0f
        listaEnt.forEach{ent ->
            distanciaTotal+=ent.distancia
        }
        return distanciaTotal
    }

    fun cargarDistanciasRecorridas(textView:TextView, listaEnts: List<Entrenamiento>, tipo:String, view: View, distanciaTotal: Float){
        val distancia=sumaDistancia(listaEnts, tipo)
        porcentajeWidthEntrenamiento(view,distancia,distanciaTotal)
        textView.setText("$distancia km")
    }

    fun sumaDistancia(entrenamientos: List<Entrenamiento>, tipo:String):Float{
        var suma=0.0f

        entrenamientos.forEach{ent ->
            if(ent.tipo.equals(tipo)) suma+=ent.distancia
        }
        return suma
    }
    fun cargarEntrenamientos(){
        val idUser=intent.getSerializableExtra("idUsuario") as Int
        listaEnt=dbHelper.getEntremaientosUnMesAtras(idUser)
        listaEnt.forEach{ent ->
            println("Tipo: ${ent.tipo}, Distancia: ${ent.distancia}, Fecha: ${convertirMillisAFecha(ent.fecha,"dd/MM/yyyy HH:mm:ss")}, ")
        }
    }

    fun convertirMillisAFecha(millis: Long, formato: String): String {
        val dateFormat = SimpleDateFormat(formato, Locale.getDefault())
        val fecha = Date(millis)
        return dateFormat.format(fecha)
    }

}