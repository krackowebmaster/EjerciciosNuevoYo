package com.c360app.ejerciciosnuevoyo

import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.c360app.appjson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InstruccionesGenerales : AppCompatActivity() {

    private var siAcepto = 0
    private var avisoMiPerfil = 0
    private var pNivel = "0"
    private var usrAvailability = "0"
    private var usrCategpry = "0"
    private var docUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instrucciones_generales)

        //eventos del toolbarr
        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        val btnHome = findViewById<ImageView>(R.id.btnHome)
        val btnCancelar = findViewById<Button>(R.id.button2)
        btnRegresar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        btnHome.setOnClickListener {
            startActivity(Intent(this, LanzadorNuevoYo::class.java))
        }

        val playVideo = findViewById<ImageView>(R.id.videoInstrucciones1)
        val playVideo2 = findViewById<ImageView>(R.id.videoInstrucciones2)

        playVideo.setOnClickListener {

            val url = "https://nuevoyoejercicio.mx/api.nuevoyoejerc.com/v1/videos_info_gral/video1.mp4"
            abrePdfVideo(url)
        }
        playVideo2.setOnClickListener {
            val url = "https://nuevoyoejercicio.mx/api.nuevoyoejerc.com/v1/videos_info_gral/video2.mp4"
            abrePdfVideo(url)
        }



        //detectando si ha aceptado la responsabilidad
        getAceptoResponsabilidad()

        //getUsuario()

    }



    private fun abrePdfVideo(miUrl: String) {
        //Toast.makeText(this@Rutinas, miUrl, Toast.LENGTH_SHORT).show()
        val url = Uri.parse(miUrl)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setDataAndType(url, "video/*")
        startActivity(intent)
    }

    //funcion obtiene acepto responsabilidad
    private fun getAceptoResponsabilidad(){
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = acepto(user_id = global_usuario.globalUserId.toInt())
        var miCodeRegreso = 0
        RetroService.aceptoResponsabilidad(userData).enqueue(object: Callback<acepto> {
            override fun onResponse(call: Call<acepto>, response: Response<acepto>) {
                //Toast.makeText(this@MainActivity, response.code().toString(), Toast.LENGTH_SHORT).show()
                if(response.code() !== 200){
                    //abre dialogo para regrear a mi perfil
                    onBackPressed()
                }else{
                    siAcepto = 200
                    avisoMiPerfil = 1
                    //estableciendo visita de ejercicios
                    setVisitoEjercicios()
                }
            }

            override fun onFailure(call: Call<acepto>, t: Throwable) {
                Toast.makeText(this@InstruccionesGenerales, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //funcion set acepto responsabilidad
    private fun setVisitoEjercicios() {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = acepto(user_id = global_usuario.globalUserId.toInt())

        RetroService.setVisiteEjercicios(userData).enqueue(object: Callback<acepto> {
            override fun onResponse(call: Call<acepto>, response: Response<acepto>) {
                //Toast.makeText(this@InstruccionesGenerales, response.code().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<acepto>, t: Throwable) {
                Toast.makeText(this@InstruccionesGenerales, "Error Visita Ejercicios", Toast.LENGTH_SHORT).show()
            }
        })
    }



    ///funciones generales
    override fun onBackPressed() {
        if (avisoMiPerfil == 0) {
            AlertDialog.Builder(this).apply {
                setTitle("NOTIFICACIÓN")
                setMessage("Para poder acceder a la sección de \"Ejercicios\" es necesario haber ingresado a la opcíon de \"Mi perfil\" y Aceptar la responsabilidad")

                setPositiveButton("Regresar") { _, _ ->
                    // if user press yes, then finish the current activity
                    super.onBackPressed()
                }

                /*setNegativeButton("No"){_, _ ->
                // if user press no, then return the activity
                Toast.makeText(this@MainActivity, "Thank you",
                    Toast.LENGTH_LONG).show()
            }*/

                setCancelable(true)
            }.create().show()
        }else{
            super.onBackPressed()
        }
    }
}