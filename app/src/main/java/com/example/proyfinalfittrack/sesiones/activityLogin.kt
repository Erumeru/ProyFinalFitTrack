package com.example.proyfinalfittrack.sesiones

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.proyfinalfittrack.R
import com.example.proyfinalfittrack.db.DatabaseHelper

class activityLogin : AppCompatActivity() {

    private lateinit var tvRegiter : TextView
    private lateinit var btnLogin : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvRegiter=findViewById(R.id.tvRegistrate)
        btnLogin=findViewById(R.id.btnLogin)


        tvRegiter.setOnClickListener(){
            val register= Intent(this,MainActivity::class.java)
            startActivity(register)
        }

        btnLogin.setOnClickListener(){
                val correo=findViewById<EditText>(R.id.editTextCorreo).text.toString()
                val pass=findViewById<EditText>(R.id.editTextContra).text.toString()

            if(isUserCreated(correo,pass)) {
                println("REGISTRADO")
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

        return count > 0
    }

}