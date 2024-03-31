package com.example.proyfinalfittrack.sesiones

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.proyfinalfittrack.R
import com.example.proyfinalfittrack.db.DatabaseHelper
import com.example.proyfinalfittrack.entities.User

class MainActivity : AppCompatActivity() {

    private lateinit var btnUnirse : Button
    private lateinit var tvLogin : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUnirse=findViewById(R.id.btnUnete)
        tvLogin=findViewById(R.id.tvIniciarSesion)

        btnUnirse.setOnClickListener {

            val nombre=findViewById<EditText>(R.id.editTextNombre).text.toString()
            val correo=findViewById<EditText>(R.id.editTextCorreo).text.toString()
            //falta comprobar la contraseña
            val password=findViewById<EditText>(R.id.editTextContra).text.toString()
            val passwordConfirm=findViewById<EditText>(R.id.editTextContraConfirm).text.toString()

            if(!formatoCorreo(correo)){
                showAlert(this, "Error en el correo", "Ingresa un correo real.")
                return@setOnClickListener
            }
            if(isEmailUsado(correo)){
                showAlert(this, "Error en el correo", "El correo ya ha sido registrado.")
                return@setOnClickListener
            }
            if(!compararPasswords(password,passwordConfirm)){
                showAlert(this, "Error en el correo", "Ingresa un correo real.")
                return@setOnClickListener
            }

            val dbHelper= DatabaseHelper(this)

            val db=dbHelper.writableDatabase



            val usuario=User(nombre,correo,password)
            val contentValues=ContentValues().apply {
                put("nombre",usuario.nombre)
                put("correo",usuario.correo)
                put("password",usuario.password)
            }
            val newRow= db.insert("users",null,contentValues)

            selectAll()

        }

        tvLogin.setOnClickListener(){
            val login= Intent(this,activityLogin::class.java)
            startActivity(login)
        }

    }

    fun ingresarUsuario(){

    }

    fun selectAll(){
        val dbHelper= DatabaseHelper(this)

        val db=dbHelper.writableDatabase
        //CONSULTA SELECT * FROM USERS
        val projection = arrayOf("id", "nombre", "correo", "password")

        val cursor = db.query(
            "users",  // Nombre de la tabla
            projection,  // Columnas que deseas consultar
            null,        // Cláusula WHERE (null indica que no hay condiciones)
            null,        // Valores para la cláusula WHERE (null indica que no hay condiciones)
            null,        // GROUP BY
            null,        // HAVING
            null         // ORDER BY
        )

// Iterar sobre el cursor para obtener los datos
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val correo = cursor.getString(cursor.getColumnIndexOrThrow("correo"))
            val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))

            // Aquí puedes hacer lo que necesites con los datos, como mostrarlos en un TextView, por ejemplo
            Log.d("User", "ID: $id, Nombre: $nombre, Correo: $correo, Password: $password")
        }

// Cierra el cursor y la base de datos cuando hayas terminado
        cursor.close()
        db.close()
    }

    fun formatoCorreo(texto: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        return regex.containsMatchIn(texto)
    }

    fun compararPasswords(pass1: String, pass2: String): Boolean{
        return pass1.equals(pass2)
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

    fun isEmailUsado(email: String): Boolean {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        val query = "SELECT COUNT(*) FROM users WHERE correo = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        return count > 0
    }

}