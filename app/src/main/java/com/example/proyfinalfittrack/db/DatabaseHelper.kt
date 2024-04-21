package com.example.proyfinalfittrack.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.proyfinalfittrack.entities.Entrenamiento
import java.util.Calendar

class DatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,DATABASE_VERSION){

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "db.basefittrack"

        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_CORREO = "correo"
        private const val COLUMN_PASSWORD = "password"

        private const val TABLE_ENTRENAMIENTOS = "entrenamientos"
        private const val COLUMN_ENTRENAMIENTO_ID = "entrenamiento_id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_FECHA = "fecha"
        private const val COLUMN_TIPO = "tipo"
        private const val COLUMN_DISTANCIA = "distancia"


        private const val CREATE_TABLE_ENTRENAMIENTOS = ("CREATE TABLE $TABLE_ENTRENAMIENTOS (" +
                "$COLUMN_ENTRENAMIENTO_ID INTEGER PRIMARY KEY," +
                "$COLUMN_USER_ID INTEGER," +
                "$COLUMN_FECHA INTEGER,"+
                "$COLUMN_TIPO TEXT,"+
                "$COLUMN_DISTANCIA REAL,"+

                //llave foranea a id de usuario
                "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_NAME($COLUMN_ID))")


        private const val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_NOMBRE TEXT,"+
                "$COLUMN_CORREO TEXT,"+
                "$COLUMN_PASSWORD TEXT)")
    }



    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
        db.execSQL(CREATE_TABLE_ENTRENAMIENTOS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ENTRENAMIENTOS")
        onCreate(db)
    }

    fun insertEntrenamiento(entrenamiento: Entrenamiento) {

        val db = writableDatabase

        val contentValues = ContentValues().apply {
            put(COLUMN_USER_ID, entrenamiento.userId)
            put(COLUMN_FECHA, entrenamiento.fecha)
            put(COLUMN_TIPO, entrenamiento.tipo)
            put(COLUMN_DISTANCIA, entrenamiento.distancia)
        }

        db.insert(TABLE_ENTRENAMIENTOS, null, contentValues)

        db.close()
    }

    fun getEntremaientosUnMesAtras(idUser: Int): List<Entrenamiento> {
        val entrenamientosList = mutableListOf<Entrenamiento>()

        val currentTimeMillis = System.currentTimeMillis()

        // Calcular la fecha de hace un mes
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis
        calendar.add(Calendar.MONTH, -1)
        val oneMonthAgo = calendar.timeInMillis


        val query = "SELECT * FROM $TABLE_ENTRENAMIENTOS WHERE $COLUMN_FECHA >= $oneMonthAgo AND $COLUMN_USER_ID = $idUser"

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val fecha = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO))
            val distancia = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_DISTANCIA))

            val entrenamiento = Entrenamiento(userId, fecha, tipo, distancia)
            entrenamientosList.add(entrenamiento)
        }

        cursor.close()
        db.close()
        return entrenamientosList
    }

    fun getEntremaientosXDiasAtras(idUser: Int, dias: Int): List<Entrenamiento> {
        val entrenamientosList = mutableListOf<Entrenamiento>()

        val currentTimeMillis = System.currentTimeMillis()

        // Calcular la fecha de hace un mes
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis
        calendar.add(Calendar.DAY_OF_MONTH, -dias)
        val xDiasAtras = calendar.timeInMillis


        val query = "SELECT * FROM $TABLE_ENTRENAMIENTOS WHERE $COLUMN_FECHA >= $xDiasAtras AND $COLUMN_USER_ID = $idUser"

        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val fecha = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO))
            val distancia = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_DISTANCIA))

            val entrenamiento = Entrenamiento(userId, fecha, tipo, distancia)
            entrenamientosList.add(entrenamiento)
        }

        cursor.close()
        db.close()
        return entrenamientosList
    }



    fun selectAllEntrenamientos() {

        val db = readableDatabase

        // Columnas que deseas recuperar
        val projection = arrayOf(
            COLUMN_ENTRENAMIENTO_ID,
            COLUMN_USER_ID,
            COLUMN_FECHA,
            COLUMN_TIPO,
            COLUMN_DISTANCIA
        )

        // Ejecutar la consulta SELECT
        val cursor = db.query(
            TABLE_ENTRENAMIENTOS, // Nombre de la tabla
            projection,           // Columnas
            null,                 // Cláusula WHERE
            null,                 // Argumentos para WHERE
            null,                 // GROUP BY
            null,                 // HAVING
            null                  // ORDER BY
        )

        // Iterar sobre el cursor para obtener los datos
        while (cursor.moveToNext()) {
            val entrenamientoId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ENTRENAMIENTO_ID))
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val fecha = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO))
            val distancia = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_DISTANCIA))

        }


        cursor.close()
        db.close()
    }

    fun getEntrenamientosEnFecha(idUser: Int, fecha: Long): List<Entrenamiento> {
        val entrenamientosList = mutableListOf<Entrenamiento>()
        val query = "SELECT * FROM $TABLE_ENTRENAMIENTOS WHERE $COLUMN_FECHA = $fecha AND $COLUMN_USER_ID = $idUser"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val fechaEntrenamiento = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO))
            val distancia = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_DISTANCIA))

            val entrenamiento = Entrenamiento(userId, fechaEntrenamiento, tipo, distancia)
            entrenamientosList.add(entrenamiento)
        }

        cursor.close()
        db.close()
        return entrenamientosList
    }





}