package com.c360app.ejerciciosnuevoyo

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.c360app.appjson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class Pectorales : AppCompatActivity() {
    private var usrCategpry = "0"
    private var pNivel = "0"
    private var usrAvailability = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pectorales)

        //eventos del toolbarr
        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnRegresar.setOnClickListener {
            startActivity(Intent(this, VariantesGrupoMuscular::class.java))
        }
        btnHome.setOnClickListener {
            startActivity(Intent(this, LanzadorNuevoYo::class.java))
        }

        val usrEjercicio = intent.getStringExtra("muscle_group_id")
        val usrTituloGP = intent.getStringExtra("tituloGP")
        val tituloPantalla = findViewById<TextView>(R.id.txtTituloMuscular)
        tituloPantalla.setText(usrTituloGP.toString())
        getMiEjercicio(usrEjercicio.toString())
        //Toast.makeText(this@Pectorales, usrEjercicio.toString(), Toast.LENGTH_SHORT).show()
    }


    //funcion get ejercicio grupo muscular

    private fun getMiEjercicio(idMuscle:String) {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = aceptoIdEjercicio(muscle_group_id = idMuscle)

        RetroService.getUsrEjercicios(userData).enqueue(object: Callback<UserEjercicio> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<UserEjercicio>, response: Response<UserEjercicio>) {
                if(response.code() == 200){
                    //Toast.makeText(this@Pectorales, response.body().toString(), Toast.LENGTH_SHORT).show()
                    /////////////////
                    val miListaEjercisio = response.body()?.Ejercicios

                    //////////////////////
                    val sortedEjercicios = miListaEjercisio?.sortedBy { it.exercise_order }

                    //vista padre para agregarlas todas
                    //val miPadre = findViewById<LinearLayout>(R.id.miLayOut)
                    val miPadre = findViewById<LinearLayout>(R.id.vistaGrupoMuscular)


                    ///recooriendo el JSON recibido
                    var tSize = response.body()?.Ejercicios?.size
                    tSize = tSize?.minus(1)

                    var cJson = 0
                    for (i in 0 .. tSize!!){


                        /// linearlayout de titulo de video o infografía
                        val layTitulo = LinearLayout(this@Pectorales)

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

                        val titRutina = TextView(this@Pectorales)
                        var tipoArchivo = sortedEjercicios?.get(cJson)?.rsx_type_name.toString()
                        var cadTitInicio = "Video"
                        if(tipoArchivo == "PDF"){
                            cadTitInicio = "Infografía"
                        }
                        val cadHtmlFinal = "<strong>"+cadTitInicio+": </strong>" + sortedEjercicios?.get(cJson)?.exercise_name.toString() + "<br>"
                        titRutina.setText(Html.fromHtml(cadHtmlFinal, Html.FROM_HTML_MODE_COMPACT))


                        titRutina.setTextColor(Color.parseColor("#6e6f71"))
                        titRutina.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                        titRutina.setTypeface(Typeface.SANS_SERIF)

                        //agregando salto cuando sea despues del primer ciclo
                        if(cJson > 0){
                            val mt3 = TextView(this@Pectorales)
                            mt3.setText(Html.fromHtml("<br>", Html.FROM_HTML_MODE_COMPACT))
                            layTitulo.addView(mt3)
                        }

                        //agregando objetos a linearlayout PRINCIPAL
                        layTitulo.addView(titRutina)

                        ////////////// linearlayout de botones
                        //////////////////////////////////////////////////
                        val layBotones = LinearLayout(this@Pectorales)

                        layBotones.layoutParams =
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        layBotones.orientation = LinearLayout.HORIZONTAL



                        //layTitulo.addView(layout2)
                        if(tipoArchivo == "PDF") {

                            val mt1 = TextView(this@Pectorales)
                            mt1.setText("   ")
                            val mt2 = TextView(this@Pectorales)
                            mt2.setText("   ")

                            val miVer = ImageView(this@Pectorales)
                            miVer.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext, // Context
                                    R.drawable.ver_app_standar // Drawable
                                )
                            )

                            layBotones.addView(miVer)
                            layBotones.addView(mt1)

                            val miMail = ImageView(this@Pectorales)
                            miMail.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext, // Context
                                    R.drawable.mail_app_standar // Drawable
                                )
                            )

                            layBotones.addView(miMail)
                            layBotones.addView(mt2)

                            val miDescarga = ImageView(this@Pectorales)
                            miDescarga.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext, // Context
                                    R.drawable.down_app_standar // Drawable
                                )
                            )

                            layBotones.addView(miDescarga)

                            //configurando eventos
                            //configurando eventos, aqui iguale la variable de lo contrario no ejecutaba
                            val miUrlPdf = sortedEjercicios?.get(cJson)?.rsx_url.toString()
                            miVer.setOnClickListener {
                                //abrePdfVideo(miUrlPdf)
                                abrePdf(miUrlPdf)
                            }
                            miDescarga.setOnClickListener {
                                downPdf(miUrlPdf)
                            }

                            /////////////////////////////////////////////////////
                            /////////////////////////////////////////////////////
                            val emailRutina = sortedEjercicios?.get(cJson)?.exercise_id

                            miMail.setOnClickListener {
                                ///////////////////
                                //////Enviando MAIL
                                val builDialogMail = AlertDialog.Builder(this@Pectorales)
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

                                    Toast.makeText(this@Pectorales, emailRutina.toString(), Toast.LENGTH_SHORT).show()
                                    if (checkEmail(mailEnviar.text.toString())) {

                                        sendMailEjercicio(emailRutina.toString(), mailEnviar.text.toString())
                                        mailDialog.hide()
                                        Toast.makeText(this@Pectorales, "Rutina enviada!", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(this@Pectorales, "El correo electrónico es incorrecto", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                btnCancelaMail.setOnClickListener {
                                    mailDialog.hide()
                                }
                            }


                        }else{
                            ////imagen play
                            val miPlay = ImageView(this@Pectorales)
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
                            val miUrlVideo = sortedEjercicios?.get(cJson)?.rsx_url.toString()
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


                    /////////////////
                }



            override fun onFailure(call: Call<UserEjercicio>, t: Throwable) {
                Toast.makeText(this@Pectorales, "Error de grupo muscular", Toast.LENGTH_SHORT).show()
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
                this@Pectorales,
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
    private fun sendMailEjercicio(exerciseId: String, cemail: String) {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = aceptoDocEjercicio(exercise_id = exerciseId, email = cemail)

        RetroService.sendMailDocEjercicio(userData).enqueue(object: Callback<DocEjercicioResponse> {
            override fun onResponse(call: Call<DocEjercicioResponse>, response: Response<DocEjercicioResponse>) {
                //Toast.makeText(this@InstruccionesGenerales, response.code().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<DocEjercicioResponse>, t: Throwable) {
                Toast.makeText(this@Pectorales, "error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}