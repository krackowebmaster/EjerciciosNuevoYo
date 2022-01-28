package com.c360app.ejerciciosnuevoyo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.c360app.appjson.RetroInstance
import com.c360app.appjson.RetroService
import com.c360app.appjson.acepto
import com.c360app.appjson.cPerfil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {
    //variables globales
    //llave preferencemanager
    private var siAcepto = 0
    private var usrCategpry = "0"
    private var siEntreEjercicios = 0
    private var posSpn1 = 0
    private var posSpn2 = 0
    private var miPesoG = 0
    private var avisoMiPerfil = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //eventos del toolbarr
        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        val btnHome = findViewById<ImageView>(R.id.btnHome)
        btnRegresar.setOnClickListener {
            startActivity(Intent(this, LanzadorNuevoYo::class.java))
        }
        btnHome.setOnClickListener {
            startActivity(Intent(this, LanzadorNuevoYo::class.java))
        }


        //llamando a las actividades
        //instrucciones
        val btnInstrucciones = findViewById<Button>(R.id.btnInstrucciones)
        val btnIntegracion = findViewById<Button>(R.id.btnIntegracion)
        val btnRutinas = findViewById<Button>(R.id.btnRutinas)
        val btnVariantes = findViewById<Button>(R.id.btnGrupoMuscular)

        btnInstrucciones.setOnClickListener {
            startActivity(Intent(this,InstruccionesGenerales::class.java))
        }
        btnIntegracion.setOnClickListener {
            if(siEntreEjercicios == 1) {
                startActivity(Intent(this, IntegracionAerobicoFuerza::class.java))
            }else{
                noVisitaEjercicios()
            }
        }
        btnRutinas.setOnClickListener {
            if(siEntreEjercicios == 1) {
                startActivity(Intent(this,Rutinas::class.java))
            }else{
                noVisitaEjercicios()
            }
        }
        btnVariantes.setOnClickListener {
            if(siEntreEjercicios == 1) {
                startActivity(Intent(this,VariantesGrupoMuscular::class.java))
            }else{
                noVisitaEjercicios()
            }
        }

        //obteniendo datos de usuarioo
        //obteniendo datos usuario
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
                Toast.makeText(this@MainActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainActivity, "error", Toast.LENGTH_SHORT).show()
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
                    //Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_SHORT).show()
                    //mostrando datosde usuario

                    val uCategory = response.body()?.Perfil?.category_id

                    usrCategpry = uCategory.toString()

                }
                //Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<cPerfil>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de usuario", Toast.LENGTH_SHORT).show()
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

    fun noVisitaEjercicios() {
        AlertDialog.Builder(this).apply {
            setTitle("NOTIFICACIÓN")
            setMessage("Usted no ha ingresado a la sección de \"Instrucciones generales\" porlo que no es posible acceder a las demás secciones.")

            setPositiveButton("Enterado") { _, _ ->

            }

            setCancelable(true)
        }.create().show()
    }
}