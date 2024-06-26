package com.example.proyfinalfittrack.sesiones

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.proyfinalfittrack.R
import com.example.proyfinalfittrack.dashboard.Dashboard
import com.example.proyfinalfittrack.db.DatabaseHelper

class activityLogin : AppCompatActivity() {

    private lateinit var tvRegiter : TextView
    private lateinit var btnLogin : Button
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvRegiter=findViewById(R.id.tvRegistrate)
        btnLogin=findViewById(R.id.btnLogin)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)



        tvRegiter.setOnClickListener(){
            val register= Intent(this,MainActivity::class.java)
            startActivity(register)
        }

        btnLogin.setOnClickListener(){
                val correo=findViewById<EditText>(R.id.editTextCorreo).text.toString()
                val pass=findViewById<EditText>(R.id.editTextContra).text.toString()

            if(isUserCreated(correo,pass)) {
                val nombre = getUserNameByEmail(correo)
                val id= getUserIdByEmail(correo)
                val dashboard= Intent(this,Dashboard::class.java)
                dashboard.putExtra("nombreUsuario", nombre)
                dashboard.putExtra("idUsuario", id)
                startActivity(dashboard)
            }else{
                showAlert(this, "Error en credenciales", "Las credenciales son incorrectas.")
                return@setOnClickListener
            }
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

    fun isUserCreated(email: String, pass: String): Boolean {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        val query = "SELECT COUNT(*) FROM users WHERE correo = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(email, pass))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        // Obtener el correo guardado en SharedPreferences
        val savedEmail = sharedPreferences.getString("correo", "")

        // Verificar si el correo coincide con el almacenado en SharedPreferences
        return savedEmail == email && count > 0
    }


    fun getUserNameByEmail(email: String): String? {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        var userName: String? = null

        val query = "SELECT nombre FROM users WHERE correo = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        if (cursor.moveToFirst()) {
            val columnNameIndex= cursor.getColumnIndex("nombre")
            if(columnNameIndex!=-1){
                userName=cursor.getString(columnNameIndex)
            }
        }

        cursor.close()
        db.close()

        return userName
    }

    fun getUserIdByEmail(email: String): Int {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        var userId = -1

        val query = "SELECT id FROM users WHERE correo = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("id")
            if (columnIndex != -1) {
                userId = cursor.getInt(columnIndex)
            }
        }

        cursor.close()
        db.close()

        return userId
    }

}