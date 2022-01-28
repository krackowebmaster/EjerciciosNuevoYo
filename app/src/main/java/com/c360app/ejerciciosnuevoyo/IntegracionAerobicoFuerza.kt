package com.c360app.ejerciciosnuevoyo

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.c360app.appjson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Patterns
import java.util.regex.Pattern


class IntegracionAerobicoFuerza : AppCompatActivity() {
    private var siAcepto = 0
    private var usrCategpry = "0"
    private var siEntreEjercicios = 0
    private var posSpn1 = 0
    private var posSpn2 = 0
    private var miPesoG = 0
    private var minutosUsr = 0
    private var avisoMiPerfil = 0
    private var pNivel = "0"
    private var usrAvailability = "0"
    private var docUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_integracion_aerobico_fuerza)

        //eventos del toolbarr
        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnRegresar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        btnHome.setOnClickListener {
            startActivity(Intent(this, LanzadorNuevoYo::class.java))
        }

        //iniciailzando usuario
        getUsuario()
        //detectando si ha aceptado la responsabilidad
        getAceptoResponsabilidad()
        //detectando visita de ejercicios
        getVisitoEjercicios()

        ///eventos para abrir documentos
        val btnVerPdf = findViewById<ImageView>(R.id.btnVerDocI)
        val btnDownDoc = findViewById<ImageView>(R.id.btnDescargaDocI)
        val btnEnviaMail = findViewById<ImageView>(R.id.enviarMailDocI)
        btnVerPdf.setOnClickListener {
            abrePdf(docUrl)
        }
        btnDownDoc.setOnClickListener {
            downPdf(docUrl)
        }
        btnEnviaMail.setOnClickListener {
            val builDialogMail = AlertDialog.Builder(this@IntegracionAerobicoFuerza)
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

                if (checkEmail(mailEnviar.text.toString())) {
                    sendMailAero("2", mailEnviar.text.toString())
                    mailDialog.hide()
                    Toast.makeText(this@IntegracionAerobicoFuerza, "Documento Enviado!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@IntegracionAerobicoFuerza, "El correo electrónico es incorrecto", Toast.LENGTH_SHORT).show()
                }
            }

            btnCancelaMail.setOnClickListener {
                mailDialog.hide()
            }

        }
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
                Toast.makeText(this@IntegracionAerobicoFuerza, "Error de conexión", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@IntegracionAerobicoFuerza, "Error vista de ejericios", Toast.LENGTH_SHORT).show()
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

                    val uCategory = response.body()?.Perfil?.category_id
                    minutosUsr = response.body()?.Perfil?.week_mins.toString().toInt()
                    val txtMinutosS = findViewById<EditText>(R.id.txtMinutosSemana)
                    txtMinutosS.setText(response.body()?.Perfil?.week_mins.toString())
                    usrCategpry = uCategory.toString()

                    pNivel = response.body()?.Perfil?.activity_level_id.toString()
                    usrAvailability = response.body()?.Perfil?.availability.toString()
                    getDocFuerza(pNivel,usrAvailability)

                }
                //Toast.makeText(this@IntegracionAerobicoFuerza, response.body().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<cPerfil>, t: Throwable) {
                Toast.makeText(this@IntegracionAerobicoFuerza, "Error de Usuario", Toast.LENGTH_SHORT).show()
            }


        })
    }

    //funcion set acepto responsabilidad
    private fun getDocFuerza(miNivel: String, miDisponibilidad: String) {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = aceptoRutina(act_level_id = miNivel, availability = miDisponibilidad)

        RetroService.getDocAero(userData).enqueue(object: Callback<UserDocAero> {
            override fun onResponse(call: Call<UserDocAero>, response: Response<UserDocAero>) {
                //Toast.makeText(this@InstruccionesGenerales, response.body()?.aerobic_strength_integration?.get(0)?.rsx_url.toString(), Toast.LENGTH_SHORT).show()
                docUrl = response.body()?.aerobic_strength_integration?.get(0)?.rsx_url.toString()
            }

            override fun onFailure(call: Call<UserDocAero>, t: Throwable) {
                Toast.makeText(this@IntegracionAerobicoFuerza, "error", Toast.LENGTH_SHORT).show()
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
                this@IntegracionAerobicoFuerza,
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
    private fun sendMailAero(asiId: String, cemail: String) {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = aceptoDocAero(asi_id = asiId, email = cemail)

        RetroService.sendMailDocAero(userData).enqueue(object: Callback<DocAeroResponse> {
            override fun onResponse(call: Call<DocAeroResponse>, response: Response<DocAeroResponse>) {
                //Toast.makeText(this@InstruccionesGenerales, response.code().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<DocAeroResponse>, t: Throwable) {
                Toast.makeText(this@IntegracionAerobicoFuerza, "error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}