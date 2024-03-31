package com.example.proyfinalfittrack.sesiones

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.proyfinalfittrack.R

class activityLogin : AppCompatActivity() {

    private lateinit var tvRegiter : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvRegiter=findViewById(R.id.tvRegistrate)

        tvRegiter.setOnClickListener(){
            val register= Intent(this,MainActivity::class.java)
            startActivity(register)
        }

    }
}