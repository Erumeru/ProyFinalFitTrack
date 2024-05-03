package com.example.proyfinalfittrack.dashboard



import android.app.Activity
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout

import android.widget.TextView
import com.example.proyfinalfittrack.R
import com.example.proyfinalfittrack.db.DatabaseHelper
import com.example.proyfinalfittrack.entities.Entrenamiento

import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Dashboard : AppCompatActivity() {

    var nombreUsuario: String = "Undefined"
    private lateinit var tvNombre : TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var agregarEjercicio: ImageView
    private lateinit var agregarGPS: Button
    private lateinit var listaEnt: List<Entrenamiento>

    private lateinit var listaEntDiasAtras: List<Entrenamiento>

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
        agregarGPS=findViewById(R.id.agregarGPS)
        linearCaminar=findViewById(R.id.linearCaminarDistanciaBarra)
        viewBarraCaminar=findViewById(R.id.viewBarraCaminar)
        viewBarraCorrer=findViewById(R.id.viewBarraCorrer)
        viewBarraBici=findViewById(R.id.viewBarraBici)

        dbHelper= DatabaseHelper(this)
        tvNombre.setText(nombreUsuario)

        cargarTextViews()
        cargarEntrenamientos()
        cargarEntrenamientosDiasAtras(60)




        val listaParaPrincipalConstancia = eliminarUltimos30DiasListaEntrenamientos(listaEntDiasAtras)

        configurarConstancia(listaEntDiasAtras,listaParaPrincipalConstancia)





        val distanciaTotalRecorrida=sumaTotalDistancias()
        val distanciaDias=sumarDistanciasDelDiaActual()
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

        agregarGPS.setOnClickListener(){
            val agregar = Intent(this, GPSActivity::class.java)
            agregar.putExtra("idUsuario", intent.getSerializableExtra("idUsuario") as Int)
            agregar.putExtra("nombreUsuario", intent.getSerializableExtra("nombreUsuario") as String)
            startActivityForResult(agregar, REQUEST_CODE_GPS_ACTIVITY)

        }





// Lunes
        val fechaLunes = obtenerFechaEspecificaParaDiaSemana(Calendar.MONDAY)
        val distanciaLunes = calcularDistanciaDiaEspecifico(fechaLunes)
        val diferenciaLunes = calcularDiferenciaDiaSemanaAnterior(fechaLunes)
        val tvKilometrosLunes = findViewById<TextView>(R.id.tvKilometrosLunes)
        val tvDiferenciaLunes = findViewById<TextView>(R.id.tvDiferenciaLunes)
        tvKilometrosLunes.text = getString(R.string.km_format, distanciaLunes.toString())
        tvDiferenciaLunes.text = getString(R.string.dif_format, diferenciaLunes.toString())
        println("LUNES{$diferenciaLunes}")
// Martes
        val fechaMartes = obtenerFechaEspecificaParaDiaSemana(Calendar.TUESDAY)
        val distanciaMartes = calcularDistanciaDiaEspecifico(fechaMartes)
        val diferenciaMartes = calcularDiferenciaDiaSemanaAnterior(fechaMartes)
        val tvKilometrosMartes = findViewById<TextView>(R.id.tvKilometrosMartes)
        val tvDiferenciaMartes = findViewById<TextView>(R.id.tvDiferenciaMartes)
        tvKilometrosMartes.text = getString(R.string.km_format, distanciaMartes.toString())
        tvDiferenciaMartes.text = getString(R.string.dif_format, diferenciaMartes.toString())

// Miércoles
        val fechaMiercoles = obtenerFechaEspecificaParaDiaSemana(Calendar.WEDNESDAY)
        val distanciaMiercoles = calcularDistanciaDiaEspecifico(fechaMiercoles)
        val diferenciaMiercoles = calcularDiferenciaDiaSemanaAnterior(fechaMiercoles)
        val tvKilometrosMiercoles = findViewById<TextView>(R.id.tvKilometrosMiercoles)
        val tvDiferenciaMiercoles = findViewById<TextView>(R.id.tvDiferenciaMiercoles)
        tvKilometrosMiercoles.text = getString(R.string.km_format, distanciaMiercoles.toString())
        tvDiferenciaMiercoles.text = getString(R.string.dif_format, diferenciaMiercoles.toString())

// Jueves
        val fechaJueves = obtenerFechaEspecificaParaDiaSemana(Calendar.THURSDAY)
        val distanciaJueves = calcularDistanciaDiaEspecifico(fechaJueves)
        val diferenciaJueves = calcularDiferenciaDiaSemanaAnterior(fechaJueves)
        val tvKilometrosJueves = findViewById<TextView>(R.id.tvKilometrosJueves)
        val tvDiferenciaJueves = findViewById<TextView>(R.id.tvDiferenciaJueves)
        tvKilometrosJueves.text = getString(R.string.km_format, distanciaJueves.toString())
        tvDiferenciaJueves.text = getString(R.string.dif_format, diferenciaJueves.toString())

// Viernes
        val fechaViernes = obtenerFechaEspecificaParaDiaSemana(Calendar.FRIDAY)
        val distanciaViernes = calcularDistanciaDiaEspecifico(fechaViernes)
        val diferenciaViernes = calcularDiferenciaDiaSemanaAnterior(fechaViernes)
        val tvKilometrosViernes = findViewById<TextView>(R.id.tvKilometrosViernes)
        val tvDiferenciaViernes = findViewById<TextView>(R.id.tvDiferenciaViernes)
        tvKilometrosViernes.text = getString(R.string.km_format, distanciaViernes.toString())
        tvDiferenciaViernes.text = getString(R.string.dif_format, diferenciaViernes.toString())

// Sábado
        val fechaSabado = obtenerFechaEspecificaParaDiaSemana(Calendar.SATURDAY)
        val distanciaSabado = calcularDistanciaDiaEspecifico(fechaSabado)
        val diferenciaSabado = calcularDiferenciaDiaSemanaAnterior(fechaSabado)
        val tvKilometrosSabado = findViewById<TextView>(R.id.tvKilometrosSabado)
        val tvDiferenciaSabado = findViewById<TextView>(R.id.tvDiferenciaSabado)
        tvKilometrosSabado.text = getString(R.string.km_format, distanciaSabado.toString())
        tvDiferenciaSabado.text = getString(R.string.dif_format, diferenciaSabado.toString())

// Domingo
        val fechaDomingo = obtenerFechaEspecificaParaDiaSemana(Calendar.SUNDAY)
        val distanciaDomingo = calcularDistanciaDiaEspecifico(fechaDomingo)
        val diferenciaDomingo = calcularDiferenciaDiaSemanaAnterior(fechaDomingo)
        val tvKilometrosDomingo = findViewById<TextView>(R.id.tvKilometrosDomingo)
        val tvDiferenciaDomingo = findViewById<TextView>(R.id.tvDiferenciaDomingo)
        tvKilometrosDomingo.text = getString(R.string.km_format, distanciaDomingo.toString())
        tvDiferenciaDomingo.text = getString(R.string.dif_format, diferenciaDomingo.toString())






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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GPS_ACTIVITY && resultCode == Activity.RESULT_OK) {
            val actividadGuardada = data?.getStringExtra("actividadGuardada")
            if (!actividadGuardada.isNullOrEmpty()) {
                cargarEntrenamientos()
                val distanciaTotalRecorrida = sumaTotalDistancias()
                linearCaminar.post {
                    cargarDistanciasRecorridas(tvSumaCaminar, listaEnt, "caminar", viewBarraCaminar, distanciaTotalRecorrida)
                    cargarDistanciasRecorridas(tvSumaCorrer, listaEnt, "correr", viewBarraCorrer, distanciaTotalRecorrida)
                    cargarDistanciasRecorridas(tvSumaBici, listaEnt, "bici", viewBarraBici, distanciaTotalRecorrida)
                }
            }
        }
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



    fun convertirMillisAFecha(millis: Long, formato: String): String {
        val dateFormat = SimpleDateFormat(formato, Locale.getDefault())
        val fecha = Date(millis)
        return dateFormat.format(fecha)
    }



    private fun sumarDistanciasDelDiaActual(): Float {
        val idUser = intent.getSerializableExtra("idUsuario") as Int
        val fechaActual = System.currentTimeMillis()

        // Obtener la fecha de inicio del día actual
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaActual
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val inicioDiaActual = calendar.timeInMillis

        // Obtener la fecha de fin del día actual
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val finDiaActual = calendar.timeInMillis

        // Obtener los entrenamientos del día actual
        val listaEntrenamientosDiaActual = dbHelper.getEntrenamientosEntreFechas(idUser, inicioDiaActual, finDiaActual)

        // Sumar las distancias de los entrenamientos del día actual
        var distanciaTotalDelDia = 0.0f
        for (entrenamiento in listaEntrenamientosDiaActual) {
            distanciaTotalDelDia += entrenamiento.distancia
        }

        return distanciaTotalDelDia
    }

    private fun calcularDistanciaDiaEspecifico(fechaEspecifica: Long): Float {
        val idUser = intent.getSerializableExtra("idUsuario") as Int
        val listaEntrenamientosDiaEspecifico = dbHelper.getEntrenamientosEnFecha(idUser,fechaEspecifica)
        var distanciaTotal = 0.0f
        for (entrenamiento in listaEntrenamientosDiaEspecifico) {
            distanciaTotal += entrenamiento.distancia
        }
        return distanciaTotal
    }












    private fun calcularDiferenciaDiaSemanaAnterior(fechaEspecifica: Long): Float {
        val idUser = intent.getSerializableExtra("idUsuario") as Int

        // Calcular la fecha del mismo día de la semana anterior
        val fechaDiaSemanaAnterior = calcularFechaDiaSemanaAnterior(fechaEspecifica)

        // Calcular la distancia total del mismo día de la semana anterior
        val distanciaDiaSemanaAnterior = calcularDistanciaDiaEspecifico(fechaDiaSemanaAnterior)

        // Calcular la distancia total del mismo día de la semana actual
        val distanciaDiaSemanaActual = calcularDistanciaDiaEspecifico(fechaEspecifica)

        // Verificar si hay distancias válidas para ambos días
        if (distanciaDiaSemanaAnterior != 0.0f && distanciaDiaSemanaActual != 0.0f) {
            // Calcular la diferencia entre los kilómetros recorridos
            return  distanciaDiaSemanaActual - distanciaDiaSemanaAnterior
        } else {
            // Si alguna de las distancias es cero, establecer la diferencia en cero
            return 0.0f
        }
    }




    private fun calcularFechaDiaSemanaAnterior(fechaActual: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fechaActual

        // Restar 7 días para obtener la fecha del mismo día de la semana anterior
        calendar.add(Calendar.DAY_OF_YEAR, -7)

        return calendar.timeInMillis
    }


    private fun obtenerFechaEspecificaParaDiaSemana(diaSemana: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, diaSemana)
        return calendar.timeInMillis
    }

    private fun actualizarDiferenciaTextView(textViewId: Int, diferencia: Float) {
        val textView = findViewById<TextView>(textViewId)
        textView.text = getString(R.string.dif_format, diferencia.toString())
    }


    companion object {
        private const val REQUEST_CODE_GPS_ACTIVITY = 1001
    }


}