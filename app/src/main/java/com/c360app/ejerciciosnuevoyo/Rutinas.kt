package com.c360app.ejerciciosnuevoyo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Layout
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.c360app.appjson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.TextView

import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.*
import android.widget.Toast

import androidx.core.content.FileProvider
import java.lang.Exception
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.util.JsonReader
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.regex.Pattern


class Rutinas : AppCompatActivity() {

    //variables globales
    private var siAcepto = 0
    private var usrCategpry = "0"
    private var pNivel = "0"
    private var usrAvailability = "0"
    private var siEntreEjercicios = 0
    private var posSpn1 = 0
    private var posSpn2 = 0
    private var miPesoG = 0
    private var avisoMiPerfil = 0
    private var minutosUsr = 0





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rutinas)

        //eventos del toolbarr
        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnRegresar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        btnHome.setOnClickListener {
            startActivity(Intent(this, LanzadorNuevoYo::class.java))
        }

        //llamando suaurio
        getUsuario()
        //detectando si ha aceptado la responsabilidad
        getAceptoResponsabilidad()
        //detectando visita de ejercicios
        getVisitoEjercicios()

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
                    avisoMiPerfil = 1
                }else{
                    siAcepto = 200
                }
            }

            override fun onFailure(call: Call<acepto>, t: Throwable) {
                Toast.makeText(this@Rutinas, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //funcion set acepto responsabilidad
    private fun getVisitoEjercicios() {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = acepto(user_id = global_usuario.globalUserId.toInt())

        RetroService.setVisiteEjercicios(userData).enqueue(object: Callback<acepto> {
            override fun onResponse(call: Call<acepto>, response: Response<acepto>) {
                //Toast.makeText(this@MainActivity, response.code().toString(), Toast.LENGTH_SHORT).show()
                if(response.code() == 200){
                    siEntreEjercicios = 1
                    avisoMiPerfil = 1
                }
            }

            override fun onFailure(call: Call<acepto>, t: Throwable) {
                Toast.makeText(this@Rutinas, "error", Toast.LENGTH_SHORT).show()
                siEntreEjercicios = 0
                avisoMiPerfil = 0
            }
        })
    }


    //funcion get Usuario
    private fun getUsuario() {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = acepto(user_id = global_usuario.globalUserId.toInt())

        RetroService.getPerfilUsuario(userData).enqueue(object: Callback<cPerfil> {
            override fun onResponse(call: Call<cPerfil>, response: Response<cPerfil>) {
                if(response.code() == 200){

                    pNivel = response.body()?.Perfil?.activity_level_id.toString()
                    usrAvailability = response.body()?.Perfil?.availability.toString()
                    usrCategpry = response.body()?.Perfil?.category_id.toString()
                    getRUsuario(pNivel,usrAvailability)

                }
                //Toast.makeText(this@IntegracionAerobicoFuerza, response.body().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<cPerfil>, t: Throwable) {
                Toast.makeText(this@Rutinas, "error", Toast.LENGTH_SHORT).show()
            }

        })
    }

    //funcion get Usuario
    private fun getRUsuario(miNivel: String, miDisponibilidad: String) {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = aceptoRutina(act_level_id = miNivel, availability = miDisponibilidad)

        RetroService.getRutinasUsuario(userData).enqueue(object: Callback<UserRutina> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<UserRutina>, response: Response<UserRutina>) {
                if(response.code() == 200){

                    val miListaEjercisio = response.body()?.Rutinas

                    //////////////////////
                    val sortedDatesDescending = miListaEjercisio?.sortedBy { it.routine_order }


                    //vista padre para agregarlas todas
                    //val miPadre = findViewById<LinearLayout>(R.id.miLayOut)
                    val miPadre = findViewById<LinearLayout>(R.id.vistaRFinal)


                    ///recooriendo el JSON recibido
                    var tSize = response.body()?.Rutinas?.size
                    tSize = tSize?.minus(1)

                    var cJson = 0
                    for (i in 0 .. tSize!!){


                        /// linearlayout de titulo de video o infografía
                        val layTitulo = LinearLayout(this@Rutinas)

                        layTitulo.layoutParams =
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        layTitulo.orientation = LinearLayout.VERTICAL

                        //instanciando los parametros para los objetos de este layout
                        val layoutParams = layTitulo.layoutParams as LinearLayout.LayoutParams
                        layoutParams.setMargins(0, 0, 0, 5)

                        //aplicando parametros
                        layTitulo.layoutParams = layoutParams

                        val titRutina = TextView(this@Rutinas)
                        var tipoArchivo = sortedDatesDescending?.get(cJson)?.rsx_type_name.toString()
                        var cadTitInicio = "Video"
                        if(tipoArchivo == "PDF"){
                            cadTitInicio = "Infografía"
                        }
                        val cadHtmlFinal = "<strong>"+cadTitInicio+": </strong>" + sortedDatesDescending?.get(cJson)?.routine_description.toString() + "<br>"
                        titRutina.setText(Html.fromHtml(cadHtmlFinal, Html.FROM_HTML_MODE_COMPACT))


                        titRutina.setTextColor(Color.parseColor("#6e6f71"))
                        titRutina.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                        titRutina.setTypeface(Typeface.SANS_SERIF)

                        //agregando salto cuando sea despues del primer ciclo
                        if(cJson > 0){
                            val mt3 = TextView(this@Rutinas)
                            mt3.setText(Html.fromHtml("<br>", Html.FROM_HTML_MODE_COMPACT))
                            layTitulo.addView(mt3)
                        }

                        //agregando objetos a linearlayout PRINCIPAL
                        layTitulo.addView(titRutina)

                        ////////////// linearlayout de botones
                        //////////////////////////////////////////////////
                        val layBotones = LinearLayout(this@Rutinas)

                        layBotones.layoutParams =
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        layBotones.orientation = LinearLayout.HORIZONTAL



                        //layTitulo.addView(layout2)
                        if(tipoArchivo == "PDF") {

                            val mt1 = TextView(this@Rutinas)
                            mt1.setText("   ")
                            val mt2 = TextView(this@Rutinas)
                            mt2.setText("   ")

                            val miVer = ImageView(this@Rutinas)
                            miVer.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext, // Context
                                    R.drawable.ver_app_standar // Drawable
                                )
                            )

                            layBotones.addView(miVer)
                            layBotones.addView(mt1)

                            val miMail = ImageView(this@Rutinas)
                            miMail.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext, // Context
                                    R.drawable.mail_app_standar // Drawable
                                )
                            )

                            layBotones.addView(miMail)
                            layBotones.addView(mt2)

                            val miDescarga = ImageView(this@Rutinas)
                            miDescarga.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext, // Context
                                    R.drawable.down_app_standar // Drawable
                                )
                            )

                            layBotones.addView(miDescarga)

                            //configurando eventos
                            //configurando eventos, aqui iguale la variable de lo contrario no ejecutaba
                            val miUrlPdf = sortedDatesDescending?.get(cJson)?.rsx_url.toString()
                            miVer.setOnClickListener {
                                //abrePdfVideo(miUrlPdf)
                                abrePdf(miUrlPdf)
                            }
                            miDescarga.setOnClickListener {
                                downPdf(miUrlPdf)
                            }


                            /////////////////////////////////////////////////////
                            /////////////////////////////////////////////////////
                            val emailRutina = sortedDatesDescending?.get(cJson)?.routine_id.toString()

                            miMail.setOnClickListener {
                                ///////////////////
                                //////Enviando MAIL
                                val builDialogMail = AlertDialog.Builder(this@Rutinas)
                                val miVista = layoutInflater.inflate(R.layout.envia_mail_rutina,null)
                                builDialogMail.setView(miVista)
                                val mailDialog = builDialogMail.create()
                                mailDialog.show()

                                //eventos de botones
                                val btnEnviarMail = miVista.findViewById<Button>(R.id.btnMailEnviar)
                                val btnCancelaMail = miVista.findViewById<Button>(R.id.btnMailCancelar)
                                btnEnviarMail.setOnClickListener {
                                    val mailEnviar = miVista.findViewById<EditText>(R.id.txtMailEnviar)
                                    //Toast.makeText(this, mailEnviar.text.toString(), Toast.LENGTH_SHORT).show()

                                    Toast.makeText(this@Rutinas, emailRutina.toString(), Toast.LENGTH_SHORT).show()
                                    if (checkEmail(mailEnviar.text.toString())) {

                                        sendMailRutinas(emailRutina, mailEnviar.text.toString())
                                        mailDialog.hide()
                                        Toast.makeText(this@Rutinas, "Rutina enviada!", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(this@Rutinas, "El correo electrónico es incorrecto", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                btnCancelaMail.setOnClickListener {
                                    mailDialog.hide()
                                }
                            }



                        }else{
                            ////imagen play
                            val miPlay = ImageView(this@Rutinas)
                            miPlay.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext, // Context
                                    R.drawable.btn_play_3x // Drawable
                                )
                            )

                            val layoutParamsImg = layBotones.layoutParams as LinearLayout.LayoutParams

                            layoutParamsImg.width = 200
                            layoutParamsImg.height = 100
                            //layoutParamsImg.setMargins(0, 0, 0, 50)

                            //aplicando parametros
                            miPlay.layoutParams = layoutParamsImg
                            layBotones.addView(miPlay)

                            //configurando eventos, aqui iguale la variable de lo contrario no ejecutaba
                            val miUrlVideo = sortedDatesDescending?.get(cJson)?.rsx_url.toString()
                            miPlay.setOnClickListener {
                                abrePdfVideo(miUrlVideo)
                            }

                        }

                        //agregando layouts al padre
                        miPadre.addView(layTitulo)
                        miPadre.addView(layBotones)

                        //incementando el recorrido al json
                        cJson++

                    }

                }
                //Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<UserRutina>, t: Throwable) {
                Toast.makeText(this@Rutinas, "Error Rutinas Usuario", Toast.LENGTH_SHORT).show()
            }


        })
    }

    private fun abrePdfVideo(miUrl: String) {
        //Toast.makeText(this@Rutinas, miUrl, Toast.LENGTH_SHORT).show()
        val url = Uri.parse(miUrl)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setDataAndType(url, "video/*")
        startActivity(intent)
    }

    private fun abrePdf(miUrl: String) {
        //Toast.makeText(this@Rutinas, miUrl, Toast.LENGTH_SHORT).show()
        val url = Uri.parse(miUrl)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setDataAndType(url, "application/pdf")
        //startActivity(intent)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this@Rutinas,
                "No hay aplicación en su dispositivo para leer PDFs",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun downPdf(miUrl: String) {
        val url = miUrl
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    fun checkEmail(email: String?): Boolean {
        val EMAIL_ADDRESS_PATTERN: Pattern = Pattern
            .compile(
                "[a-zA-Z0-9+._%-+]{1,256}" + "@"
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
                        + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+"
            )
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    //funcion set acepto responsabilidad
    private fun sendMailRutinas(rutId: String, cemail: String) {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = aceptoDocRutina(routine_id = rutId, email = cemail)

        RetroService.sendMailDocRutina(userData).enqueue(object: Callback<DocRutinaResponse> {
            override fun onResponse(call: Call<DocRutinaResponse>, response: Response<DocRutinaResponse>) {
                //Toast.makeText(this@InstruccionesGenerales, response.code().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<DocRutinaResponse>, t: Throwable) {
                Toast.makeText(this@Rutinas, "error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /// funciones generales
    override fun onBackPressed() {
        if(avisoMiPerfil == 0) {
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