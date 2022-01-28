package com.c360app.ejerciciosnuevoyo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog

class LanzadorNuevoYo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lanzador_nuevo_yo)

        val btnNotiNuevas = findViewById<Button>(R.id.bntNuevaNotificacion)
        val btnNotiSemana = findViewById<Button>(R.id.btnNSemanal)
        val btnPerfil = findViewById<Button>(R.id.btnPerfil)
        val btnEjercicios = findViewById<Button>(R.id.btnEjercicios)

        btnNotiNuevas.setOnClickListener {
            val builDialogNN = AlertDialog.Builder(this@LanzadorNuevoYo)
            val miVistaNN = layoutInflater.inflate(R.layout.notificacion_nuevas,null)
            builDialogNN.setView(miVistaNN)
            val mailDialog = builDialogNN.create()
            mailDialog.show()

            //eventos de botones

            val btnCerrarNN = miVistaNN.findViewById<ImageView>(R.id.btncerrarNN)
            btnCerrarNN.setOnClickListener {
                mailDialog.hide()
            }
        }
        btnNotiSemana.setOnClickListener {
            val builDialogMail = AlertDialog.Builder(this@LanzadorNuevoYo)
            val miVista = layoutInflater.inflate(R.layout.notificacion_semanal,null)
            builDialogMail.setView(miVista)
            val mailDialog = builDialogMail.create()
            mailDialog.show()

            //eventos de botones

            val btnCancelaMail = miVista.findViewById<ImageView>(R.id.btncerrarNS)
            btnCancelaMail.setOnClickListener {
                mailDialog.hide()
            }
        }
        btnPerfil.setOnClickListener {
            startActivity(Intent(this,Perfil::class.java))
        }
        btnEjercicios.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        if(global_usuario.globalUserId.toInt() == 0) {
            //Toast.makeText(this@LanzadorNuevoYo, "entre con otra app", Toast.LENGTH_SHORT).show()
            val extraUserId = intent.getStringExtra("user_id")
            val extraUserAPI = intent.getStringExtra("user_api")
            val extraUserView = intent.getStringExtra("user_view")

            //primero abrimos ventanas sin pasar por el USER_ID, no es necesario
            if(extraUserView.toString() == "NNuevas"){
                abreNNuevas()
            }

            if(extraUserView.toString() == "NNuevasSemana"){
                abreNSemanal()
            }

            if (extraUserId.isNullOrBlank()) {
                global_usuario.globalUserId = "1"
                global_url.globalUrlAPI = "https://nuevoyoejercicio.mx/api.nuevoyoejerc.com/v1/"
                global_view.globalUsrView = "0"
            } else {
                global_usuario.globalUserId = extraUserId.toString()
                global_url.globalUrlAPI = extraUserAPI.toString()
                global_view.globalUsrView = extraUserView.toString()
            }

            //despues de obtener los datos se abre la vista
            if(extraUserView.toString() == "Perfil"){
                startActivity(Intent(this,Perfil::class.java))
            }

            if(extraUserView.toString() == "Ejercicios"){
                startActivity(Intent(this,MainActivity::class.java))
            }
        }else{
            val extraUserView = intent.getStringExtra("user_view")

            //primero abrimos ventanas sin pasar por el USER_ID, no es necesario
            if(extraUserView.toString() == "NNuevas"){
                abreNNuevas()
            }

            if(extraUserView.toString() == "NNuevasSemana"){
                abreNSemanal()
            }

            //despues de obtener los datos se abre la vista
            if(extraUserView.toString() == "Perfil"){
                startActivity(Intent(this,Perfil::class.java))
            }

            if(extraUserView.toString() == "Ejercicios"){
                startActivity(Intent(this,MainActivity::class.java))
            }

            //Toast.makeText(this@LanzadorNuevoYo, "Mantengo datos : " + global_usuario.globalUserId + " - " + global_url.globalUrlAPI, Toast.LENGTH_SHORT).show()
            //Toast.makeText(this@LanzadorNuevoYo, "vista: " + extraUserView.toString(), Toast.LENGTH_SHORT).show()
        }

        //Toast.makeText(this@LanzadorNuevoYo, global_url.globalUrlAPI, Toast.LENGTH_SHORT).show()
    }

    private fun abreNNuevas(){
        val builDialogNN = AlertDialog.Builder(this@LanzadorNuevoYo)
        val miVistaNN = layoutInflater.inflate(R.layout.notificacion_nuevas,null)
        builDialogNN.setView(miVistaNN)
        val mailDialog = builDialogNN.create()
        mailDialog.show()

        //eventos de botones

        val btnCerrarNN = miVistaNN.findViewById<ImageView>(R.id.btncerrarNN)
        btnCerrarNN.setOnClickListener {
            mailDialog.hide()
        }
    }

    private fun abreNSemanal(){
        val builDialogMail = AlertDialog.Builder(this@LanzadorNuevoYo)
        val miVista = layoutInflater.inflate(R.layout.notificacion_semanal,null)
        builDialogMail.setView(miVista)
        val mailDialog = builDialogMail.create()
        mailDialog.show()

        //eventos de botones

        val btnCancelaMail = miVista.findViewById<ImageView>(R.id.btncerrarNS)
        btnCancelaMail.setOnClickListener {
            mailDialog.hide()
        }
    }
}