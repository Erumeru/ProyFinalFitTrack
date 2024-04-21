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
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Dashboard : AppCompatActivity() {

    var nombreUsuario: String = "Undefined"
    private lateinit var tvNombre : TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var agregarEjercicio: ImageView
    private lateinit var listaEnt: List<Entrenamiento>

    private lateinit var listaEntDiasAtras: List<Entrenamiento>

    private lateinit var tvSumaCorrer:TextView
    private lateinit var tvSumaCaminar:TextView
    private lateinit var tvSumaBici:TextView

    private lateinit var linearCaminar: LinearLayout
    private lateinit var viewBarraCaminar: View
    private lateinit var viewBarraCorrer: View
    private lateinit var viewBarraBici: View

    private val listaTextViewsKilometros = mutableListOf<TextView>()
    private val listaTextViewsDiferencia = mutableListOf<TextView>()

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
        cargarEntrenamientosDiasAtras(60)
        inicializarListasTextViews()



        val listaParaPrincipalConstancia = eliminarUltimos30DiasListaEntrenamientos(listaEntDiasAtras)

        configurarConstancia(listaEntDiasAtras,listaParaPrincipalConstancia)





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

        calcularYActualizarKilometrosDiferenciasSemana()
    }

    fun configurarConstancia(listaCompleta:List<Entrenamiento>,lista30DiasMenos:List<Entrenamiento>){
        val listaVerdeSombra=listaCompleta.toMutableList()
        listaVerdeSombra.removeAll(lista30DiasMenos)



        var sumaLunesSombra = listaVerdeSombra.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY
        }.size

        var sumaMartesSombra = listaVerdeSombra.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY
        }.size

        var sumaMiercolesSombra = listaVerdeSombra.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY
        }.size
        var sumaJuevesSombra = listaVerdeSombra.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY
        }.size
        var sumaViernesSombra = listaVerdeSombra.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY
        }.size
        var sumaSabadoSombra = listaVerdeSombra.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY
        }.size
        var sumaDomingoSombra = listaVerdeSombra.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY
        }.size


        var sumaLunesPrincipal = lista30DiasMenos.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY
        }.size

        var sumaMartesPrincipal = lista30DiasMenos.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY
        }.size

        var sumaMiercolesPrincipal = lista30DiasMenos.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY
        }.size
        var sumaJuevesPrincipal = lista30DiasMenos.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY
        }.size
        var sumaViernesPrincipal = lista30DiasMenos.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY
        }.size
        var sumaSabadoPrincipal = lista30DiasMenos.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY
        }.size
        var sumaDomingoPrincipal = lista30DiasMenos.filter{ entrenamiento ->
            Calendar.getInstance().apply{ time=Date(entrenamiento.fecha)}.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY
        }.size

        //Configurar el height de las constancias
        val maxHeight=findViewById<MaterialCardView>(R.id.domingoActivo).layoutParams.height

        //Barras Domingo
        findViewById<MaterialCardView>(R.id.domingoActivo).layoutParams.height=calcularHeightConstancias(maxHeight,sumaDomingoPrincipal,sumaDomingoPrincipal+sumaDomingoSombra)
        findViewById<MaterialCardView>(R.id.domingoSombra).layoutParams.height=calcularHeightConstancias(maxHeight,sumaDomingoSombra,sumaDomingoPrincipal+sumaDomingoSombra)

        //Barras Lunes
        findViewById<MaterialCardView>(R.id.lunesActivo).layoutParams.height=calcularHeightConstancias(maxHeight,sumaLunesPrincipal,sumaLunesPrincipal+sumaLunesSombra)
        findViewById<MaterialCardView>(R.id.lunesSombra).layoutParams.height=calcularHeightConstancias(maxHeight,sumaLunesSombra,sumaLunesPrincipal+sumaLunesSombra)

        //Barras Martes
        findViewById<MaterialCardView>(R.id.martesActivo).layoutParams.height=calcularHeightConstancias(maxHeight,sumaMartesPrincipal,sumaMartesPrincipal+sumaMartesSombra)
        findViewById<MaterialCardView>(R.id.martesSombra).layoutParams.height=calcularHeightConstancias(maxHeight,sumaMartesSombra,sumaMartesPrincipal+sumaMartesSombra)

        //Barras Miercoles
        findViewById<MaterialCardView>(R.id.miercolesActivo).layoutParams.height=calcularHeightConstancias(maxHeight,sumaMiercolesPrincipal,sumaMiercolesPrincipal+sumaMiercolesSombra)
        findViewById<MaterialCardView>(R.id.miercolesSombra).layoutParams.height=calcularHeightConstancias(maxHeight,sumaMiercolesSombra,sumaMiercolesPrincipal+sumaMiercolesSombra)

        //Barras Jueves
        findViewById<MaterialCardView>(R.id.juevesActivo).layoutParams.height=calcularHeightConstancias(maxHeight,sumaJuevesPrincipal,sumaJuevesPrincipal+sumaJuevesSombra)
        findViewById<MaterialCardView>(R.id.juevesSombra).layoutParams.height=calcularHeightConstancias(maxHeight,sumaJuevesSombra,sumaJuevesPrincipal+sumaJuevesSombra)


        //Barras Viernes
        findViewById<MaterialCardView>(R.id.viernesActivo).layoutParams.height=calcularHeightConstancias(maxHeight,sumaViernesPrincipal,sumaViernesPrincipal+sumaViernesSombra)
        findViewById<MaterialCardView>(R.id.viernesSombra).layoutParams.height=calcularHeightConstancias(maxHeight,sumaViernesSombra,sumaViernesPrincipal+sumaViernesSombra)

        //Barras Sabado
        findViewById<MaterialCardView>(R.id.sabadoActivo).layoutParams.height=calcularHeightConstancias(maxHeight,sumaSabadoPrincipal,sumaSabadoPrincipal+sumaSabadoSombra)
        findViewById<MaterialCardView>(R.id.sabadoSombra).layoutParams.height=calcularHeightConstancias(maxHeight,sumaSabadoSombra,sumaSabadoPrincipal+sumaSabadoSombra)

        listaVerdeSombra.forEach{ent ->
            println("Tipo: ${ent.tipo}, Distancia: ${ent.distancia}, Fecha: ${convertirMillisAFecha(ent.fecha,"dd/MM/yyyy HH:mm:ss")}, ")
        }

    }

    fun calcularHeightConstancias(maxH:Int,sumaEnts:Int, sumaEntsTotales:Int):Int{
        if(sumaEntsTotales==0){
            return 0
        }
        val valor = (maxH * (sumaEnts.toFloat() / sumaEntsTotales)).toInt()
        println("valor $valor")
        if(valor<=0){
            return 0
        }else{
            return valor
        }
    }

    private fun cargarEntrenamientosDiaAnterior(dia: Int): List<Entrenamiento> {
        val idUser = intent.getSerializableExtra("idUsuario") as Int
        // Calcula la fecha del día anterior al especificado
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -dia - 1) // Resta un día adicional
        val fechaDiaAnterior = calendar.timeInMillis

        // Carga los entrenamientos del día anterior al especificado
        val listaEntrenamientosDiaAnterior = dbHelper.getEntrenamientosPorFecha(idUser, fechaDiaAnterior)
        println("--ENTRENAMIENTOS DEL DÍA ANTERIOR AL $dia--")
        listaEntrenamientosDiaAnterior.forEach { ent ->
            println("Tipo: ${ent.tipo}, Distancia: ${ent.distancia}, Fecha: ${convertirMillisAFecha(ent.fecha, "dd/MM/yyyy HH:mm:ss")}, ")
        }
        return listaEntrenamientosDiaAnterior
    }


    fun eliminarUltimos30DiasListaEntrenamientos(lista:List<Entrenamiento>): List<Entrenamiento>{

        if(lista.isEmpty()){
            return emptyList()
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = lista.last().fecha


        //SE OBTIENE LA FECHA DEL EJERCICIO MAS NUEVO Y SE LE RESTAN X DIAS
        //LA LISTA DE X DIAS EN ADELANTE (NUEVOS) EQUIVALEN A CONSTANCIA VERDE FUERTE
        //LA LISTA X DIAS ATRAS (VIEJOS) EQUIVALEN A VERDE OSCURO (SOMBRA)
        calendar.add(Calendar.DAY_OF_MONTH, -2)
        val diasRestados = calendar.timeInMillis

        println("diasrestados $diasRestados")

        //se le aplica al filtro
        var nuevaLista=lista.filter { entrenamiento ->
            entrenamiento.fecha > diasRestados
        }

        println("--ENTRENAMIENTOS POR DIAS ATRAS -DIAS--")
        nuevaLista.forEach{ent ->
            println("Tipo: ${ent.tipo}, Distancia: ${ent.distancia}, Fecha: ${convertirMillisAFecha(ent.fecha,"dd/MM/yyyy HH:mm:ss")}, ")
        }
        return nuevaLista
    }

    fun cargarEntrenamientosDiasAtras(dias: Int){
        val idUser=intent.getSerializableExtra("idUsuario") as Int
        listaEntDiasAtras=dbHelper.getEntremaientosXDiasAtras(idUser,dias)
        println("--ENTRENAMIENTOS POR DIAS ATRAS METODO CARGARENTRENAMIENTOSDIASATRAS--")
        listaEntDiasAtras.forEach{ent ->
            println("Tipo: ${ent.tipo}, Distancia: ${ent.distancia}, Fecha: ${convertirMillisAFecha(ent.fecha,"dd/MM/yyyy HH:mm:ss")}, ")
        }

    }


    fun porcentajeWidthEntrenamiento(view: View, distancia: Float, distanciaTotal: Float){

        val linearLayout = linearCaminar

            // El ancho de la vista ya se ha calculado
            val maxWidth = linearLayout.width
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
            println("Cargar entrenamientos Tipo: ${ent.tipo}, Distancia: ${ent.distancia}, Fecha: ${convertirMillisAFecha(ent.fecha,"dd/MM/yyyy HH:mm:ss")}, ")
       }
    }

    fun cargarEntrenamientosDia(dia: Int): List<Entrenamiento> {
        val idUser = intent.getSerializableExtra("idUsuario") as Int
        // Calcula la fecha del día especificado
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -dia)
        val fechaDia = calendar.timeInMillis

        // Carga los entrenamientos del día especificado
        val listaEntDiasAtras = dbHelper.getEntrenamientosPorFecha(idUser, fechaDia)
        println("--ENTRENAMIENTOS DEL DÍA $dia--")
        listaEntDiasAtras.forEach { ent ->
            println("Tipo: ${ent.tipo}, Distancia: ${ent.distancia}, Fecha: ${convertirMillisAFecha(ent.fecha, "dd/MM/yyyy HH:mm:ss")}, ")
        }
        return listaEntDiasAtras
    }



    fun convertirMillisAFecha(millis: Long, formato: String): String {
        val dateFormat = SimpleDateFormat(formato, Locale.getDefault())
        val fecha = Date(millis)
        return dateFormat.format(fecha)
    }


    private fun inicializarListasTextViews() {
        listaTextViewsKilometros.apply {
            add(findViewById(R.id.tvKilometrosDomingo))
            add(findViewById(R.id.tvKilometrosLunes))
            add(findViewById(R.id.tvKilometrosMartes))
            add(findViewById(R.id.tvKilometrosMiercoles))
            add(findViewById(R.id.tvKilometrosJueves))
            add(findViewById(R.id.tvKilometrosViernes))
            add(findViewById(R.id.tvKilometrosSabado))
        }

        listaTextViewsDiferencia.apply {
            add(findViewById(R.id.tvDiferenciaDomingo))
            add(findViewById(R.id.tvDiferenciaLunes))
            add(findViewById(R.id.tvDiferenciaMartes))
            add(findViewById(R.id.tvDiferenciaMiercoles))
            add(findViewById(R.id.tvDiferenciaJueves))
            add(findViewById(R.id.tvDiferenciaViernes))
            add(findViewById(R.id.tvDiferenciaSabado))
        }

    }

    private fun calcularYActualizarKilometrosDiferenciasSemana() {
        for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
            // Obtener la lista de entrenamientos del día actual y del día anterior
            val listaEntrenamientosDiaActual = obtenerEntrenamientosDia(i)
            val listaEntrenamientosDiaAnterior = obtenerEntrenamientosDiaAnterior(i)

            // Calcular los kilómetros recorridos en el día actual y en el día anterior
            val kilometrosDiaActual = calcularKilometrosRecorridos(listaEntrenamientosDiaActual)
            val kilometrosDiaAnterior = calcularKilometrosRecorridos(listaEntrenamientosDiaAnterior)

            // Calcular la diferencia entre los kilómetros del día actual y los del día anterior
            val diferenciaKilometros = kilometrosDiaActual - kilometrosDiaAnterior

            // Actualizar los TextViews correspondientes al día actual
            listaTextViewsKilometros[i - Calendar.SUNDAY].text = "KM $kilometrosDiaActual"
            listaTextViewsDiferencia[i - Calendar.SUNDAY].text = "DF $diferenciaKilometros"
        }
    }

    private fun obtenerEntrenamientosDia(dia: Int): List<Entrenamiento> {
        // Aquí llama a tu método existente para cargar los entrenamientos del día
        return cargarEntrenamientosDia(dia)
    }

    // Función para obtener los entrenamientos del día anterior al especificado
    private fun obtenerEntrenamientosDiaAnterior(dia: Int): List<Entrenamiento> {
        // Aquí llama a tu método existente para cargar los entrenamientos del día anterior
        return cargarEntrenamientosDiaAnterior(dia)
    }

    // Función para calcular los kilómetros recorridos en una lista de entrenamientos
    private fun calcularKilometrosRecorridos(entrenamientos: List<Entrenamiento>): Float {
        var distanciaTotal = 0.0f
        // Suma las distancias de todos los entrenamientos en la lista
        for (entrenamiento in entrenamientos) {
            distanciaTotal += entrenamiento.distancia
        }
        return distanciaTotal
    }



}