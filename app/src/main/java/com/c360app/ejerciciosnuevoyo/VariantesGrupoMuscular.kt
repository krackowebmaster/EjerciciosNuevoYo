package com.c360app.ejerciciosnuevoyo

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.c360app.appjson.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class VariantesGrupoMuscular : AppCompatActivity() {

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
        setContentView(R.layout.activity_variantes_grupo_muscular)

        //eventos del toolbarr
        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnRegresar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        btnHome.setOnClickListener {
            startActivity(Intent(this, LanzadorNuevoYo::class.java))
        }


        //inicializanmdo botnes
        val btnPectorales = findViewById<Button>(R.id.btnPectorales)
        val btnEspaldaAlta = findViewById<Button>(R.id.btnEspalda)
        val btnMuslos = findViewById<Button>(R.id.btnMuslos)
        val btnPantorrillas = findViewById<Button>(R.id.btnPantorrilla)
        val btnEspaldaBaja = findViewById<Button>(R.id.btnEspaldaBaja)
        val btnAbdomen = findViewById<Button>(R.id.btnAbdomen)
        val btnHombros = findViewById<Button>(R.id.btnHombros)
        val btnBiceps = findViewById<Button>(R.id.btnBiceps)
        val btnTriceps = findViewById<Button>(R.id.btnTriceps)

        //eventos click botones
        btnPectorales.setOnClickListener {
            val i = Intent(this,Pectorales::class.java)
            i.putExtra("muscle_group_id","1")
            i.putExtra("tituloGP","Pectorales")
            startActivity(i)
        }
        btnEspaldaAlta.setOnClickListener {
            val i = Intent(this,Pectorales::class.java)
            i.putExtra("muscle_group_id","2")
            i.putExtra("tituloGP","Espalda alta\n(dorsales)")
            startActivity(i)
        }
        btnMuslos.setOnClickListener {
            val i = Intent(this,Pectorales::class.java)
            i.putExtra("muscle_group_id","3")
            i.putExtra("tituloGP","Muslos")
            startActivity(i)
        }
        btnPantorrillas.setOnClickListener {
            val i = Intent(this,Pectorales::class.java)
            i.putExtra("muscle_group_id","4")
            i.putExtra("tituloGP","Pantorrillas")
            startActivity(i)
        }
        btnEspaldaBaja.setOnClickListener {
            val i = Intent(this,Pectorales::class.java)
            i.putExtra("muscle_group_id","5")
            i.putExtra("tituloGP","Espalda baja\n(lumbar)")
            startActivity(i)
        }
        btnAbdomen.setOnClickListener {
            val i = Intent(this,Pectorales::class.java)
            i.putExtra("muscle_group_id","6")
            i.putExtra("tituloGP","Abdomen")
            startActivity(i)
        }
        btnHombros.setOnClickListener {
            val i = Intent(this,Pectorales::class.java)
            i.putExtra("muscle_group_id","7")
            i.putExtra("tituloGP","Hombros")
            startActivity(i)
        }
        btnBiceps.setOnClickListener {
            val i = Intent(this,Pectorales::class.java)
            i.putExtra("muscle_group_id","8")
            i.putExtra("tituloGP","Bíceps")
            startActivity(i)
        }
        btnTriceps.setOnClickListener {
            val i = Intent(this,Pectorales::class.java)
            i.putExtra("muscle_group_id","9")
            i.putExtra("tituloGP","Tríceps")
            startActivity(i)
        }

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
                Toast.makeText(this@VariantesGrupoMuscular, "Error de conexión", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@VariantesGrupoMuscular, "error", Toast.LENGTH_SHORT).show()
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
                    usrCategpry = uCategory.toString()

                    pNivel = response.body()?.Perfil?.activity_level_id.toString()
                    usrAvailability = response.body()?.Perfil?.availability.toString()

                }
                //Toast.makeText(this@IntegracionAerobicoFuerza, response.body().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<cPerfil>, t: Throwable) {
                Toast.makeText(this@VariantesGrupoMuscular, "error", Toast.LENGTH_SHORT).show()
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
                this@VariantesGrupoMuscular,
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


}