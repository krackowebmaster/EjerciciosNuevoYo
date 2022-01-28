package com.c360app.ejerciciosnuevoyo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.c360app.appjson.*
import com.c360app.ejerciciosnuevoyo.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Perfil : AppCompatActivity() {

    //llave preferencemanager
    private var siAcepto = 0
    private var usrCategpry = "0"
    private var posSpn1 = 0
    private var posSpn2 = 0
    private var miPesoG = 0
    private var miNivel = 0
    private var miAvail = 0
    private var bndSiAcepto = 0
    private var usuarioNuevo = 0

    //private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_perfil)

        //eventos del toolbarr
        val btnRegresar = findViewById<ImageView>(R.id.btnRegresar)
        val btnHome = findViewById<ImageView>(R.id.btnHome)
        val btnCancelar = findViewById<Button>(R.id.button2)
        btnRegresar.setOnClickListener {
            startActivity(Intent(this, LanzadorNuevoYo::class.java))
        }
        btnHome.setOnClickListener {
            startActivity(Intent(this, LanzadorNuevoYo::class.java))
        }
        btnCancelar.setOnClickListener {
            startActivity(Intent(this, LanzadorNuevoYo::class.java))
        }

        //obteniendo datos usuario
        getUsuario()
        //detectando si ha aceptado la responsabilidad
        //getAceptoResponsabilidad()

        //inicializar elementos
        val txtDuracion = findViewById<EditText>(R.id.etnDuracion)
        var posDd = "0"

        //valores antes del json
        txtDuracion.setText("0")

        //contruyendo lista de Nivel de AF
        val spiner1 = findViewById<Spinner>(R.id.spActividadFisica)
        val listaAc = resources.getStringArray(R.array.opcionesAc)
        //val listaAc = listOf("Seleccione el nivel","Desde cero","Realiza algo")

        val adaptadorAc = ArrayAdapter(this@Perfil,android.R.layout.simple_spinner_item,listaAc)
        spiner1.adapter = adaptadorAc

        spiner1.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Toast.makeText(this@MainActivity, listaAc[p2], Toast.LENGTH_SHORT).show()
                miNivel = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nada por hacer
            }
        }

        //Construyendo  lista de Disponibilidad para rutinas
        val spiner2 = findViewById<Spinner>(R.id.spDiasDisponibles)
        val listaDd = resources.getStringArray(R.array.opcionesDd)
        val adaptadorDd = ArrayAdapter(this@Perfil,android.R.layout.simple_spinner_item,listaDd)
        spiner2.adapter = adaptadorDd

        spiner2.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                posDd = listaDd[p2].toString()
                miAvail = listaDd[p2].toInt()
                calculaMinutos(txtDuracion.text.toString(), listaDd[p2].toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nada por hacer
            }
        }

        //calculando días
        txtDuracion.onFocusChangeListener  = View.OnFocusChangeListener { view, b ->
            if (b){
                // do something when edit text get focus
                //textView.text = "EditText now focused."
            }else{
                // do something when edit text lost focus
                calculaMinutos(txtDuracion.text.toString(), posDd)
            }
        }

        //calcula dias pero cuando desde que solto la tecla
        txtDuracion.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode !== KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Toast.makeText(this@Perfil, "el usuario esta presionando", Toast.LENGTH_SHORT).show()
                calculaMinutos(txtDuracion.text.toString(), posDd)
            }
            false
        })


        ///si el usuario ha aceptado rtesponsabilidad, puede guardar de lo contrario se abre el dialogo
        val btnGuardar = findViewById<Button>(R.id.button3)
        btnGuardar.setOnClickListener {
            //val miEstadoAcepto = prefs.getString(siAcepto,"0")
            if(siAcepto == 200){
                if(usuarioNuevo == 0) {
                    setUsuario(global_usuario.globalUserId)
                    startActivity(Intent(this,LanzadorNuevoYo::class.java))
                }
                if(usuarioNuevo == 1) {
                    addUsuario(global_usuario.globalUserId)
                }

            }else{
                abreDialogoAcepto()
            }
        }


    }

    private fun abreDialogoAcepto(){
        //abriendo modal acepto terminos
        //valores
        val builder = android.app.AlertDialog.Builder(this@Perfil)
        val view = layoutInflater.inflate(R.layout.aceptar_responsabilidad,null)
        //pasando vista a builder
        builder.setView(view)
        //creando dialog
        val dialogAt = builder.create()
        //si no ha aceptado responsabilidad abre dialogo
        dialogAt.show()
        //evento para cerrar dialog
        val btnCerrar = view.findViewById<Button>(R.id.btnAceptoResponsabilidad)
        val btnNoAcepto = view.findViewById<Button>(R.id.btnNoAceptoResponsabilidad)
        btnCerrar.setOnClickListener {
            setAceptoResponsabilidad()
            siAcepto = 200
            bndSiAcepto = 1
            //Toast.makeText(this@Perfil, "Se guardo su respuesta", Toast.LENGTH_SHORT).show()
            dialogAt.hide()
        }

        btnNoAcepto.setOnClickListener {
            bndSiAcepto = 0
            dialogAt.hide()
            siAcepto = 0
        }
    }

    //funcion obtiene acepto responsabilidad
    private fun getAceptoResponsabilidad(userId: Int){
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = acepto(user_id = global_usuario.globalUserId.toInt())
        var miCodeRegreso = 0
        RetroService.aceptoResponsabilidad(userData).enqueue(object:Callback<acepto>{
            override fun onResponse(call: Call<acepto>, response: Response<acepto>) {
                //Toast.makeText(this@MainActivity, response.code().toString(), Toast.LENGTH_SHORT).show()
                if(response.code() !== 200){
                    abreDialogoAcepto()
                    bndSiAcepto = 0
                }else{
                    siAcepto = 200
                    bndSiAcepto = 1
                }
                //Toast.makeText(this@MainActivity, response.code().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<acepto>, t: Throwable) {
                Toast.makeText(this@Perfil, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //funcion set acepto responsabilidad
    private fun setAceptoResponsabilidad() {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = acepto(user_id = global_usuario.globalUserId.toInt())

        RetroService.setAceptoResponsabilidad(userData).enqueue(object:Callback<acepto>{
            override fun onResponse(call: Call<acepto>, response: Response<acepto>) {
                bndSiAcepto = 1
                //Toast.makeText(this@MainActivity, response.code().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<acepto>, t: Throwable) {
                bndSiAcepto = 0
                Toast.makeText(this@Perfil, "error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //funcion get Usuario
    private fun getUsuario() {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = acepto(user_id = global_usuario.globalUserId.toInt())

        RetroService.getPerfilUsuario(userData).enqueue(object:Callback<cPerfil>{
            override fun onResponse(call: Call<cPerfil>, response: Response<cPerfil>) {
                if(response.code() == 200){
                    //detectando si ha aceptado la responsabilidad
                    getAceptoResponsabilidad(global_usuario.globalUserId.toInt())
                    //mostrando datosde usuario
                    val pAltura = findViewById<EditText>(R.id.etnAltura)
                    val pPeso = findViewById<EditText>(R.id.etnPeso)
                    val pDuracion = findViewById<EditText>(R.id.etnDuracion)
                    val pMinutos = findViewById<EditText>(R.id.etnMinutosSemana)
                    val pNivel  = findViewById<Spinner>(R.id.spActividadFisica)
                    val pDisp  = findViewById<Spinner>(R.id.spDiasDisponibles)
                    val pSpinAf = response.body()?.Perfil?.activity_level_id?.toInt()
                    val pSpinDd = response.body()?.Perfil?.availability?.toInt()
                    val pSpinDdF = pSpinDd?.minus(3)
                    val uCategory = response.body()?.Perfil?.category_id
                    if (pSpinAf != null) {
                        posSpn1 = pSpinAf
                    }
                    if (pSpinDd != null) {
                        posSpn2 = pSpinDd
                    }


                    pAltura.setText(response.body()?.Perfil?.height)
                    pPeso.setText(response.body()?.Perfil?.weight)
                    pDuracion.setText(response.body()?.Perfil?.session_duration)
                    pMinutos.setText(response.body()?.Perfil?.week_mins)
                    pSpinAf?.let { pNivel.setSelection(it) }
                    pSpinDdF?.let { pDisp.setSelection(it) }
                    usrCategpry = uCategory.toString()
                    usuarioNuevo = 0

                }

                if(response.code() == 404){
                    Toast.makeText(this@Perfil, "Usuario desconocido", Toast.LENGTH_SHORT).show()
                    usuarioNuevo = 1
                }
                //Toast.makeText(this@MainActivity, response.body().toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<cPerfil>, t: Throwable) {
                usuarioNuevo = 0
                Toast.makeText(this@Perfil, "error", Toast.LENGTH_SHORT).show()
            }


        })
    }

    //funcion obtiene acepto responsabilidad
    private fun setUsuario(userId: String){
        //referenciamos la instacia retrofit
        val miPesoGt = findViewById<EditText>(R.id.etnPeso)
        miPesoG = miPesoGt.text.toString().toInt()

        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = editPerfil(user_id = userId, responsability_accept = bndSiAcepto.toString(), height = findViewById<EditText>(R.id.etnAltura).text.toString().toInt() , weight = miPesoG, activity_level_id = miNivel, availability = miAvail, week_mins = findViewById<EditText>(R.id.etnMinutosSemana).text.toString().toInt(), category_id = 1)

        RetroService.setDataUsuario(userData).enqueue(object:Callback<editPerfilResponse>{
            override fun onResponse(call: Call<editPerfilResponse>, response: Response<editPerfilResponse>) {
                Toast.makeText(this@Perfil, "Se guardo su información", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<editPerfilResponse>, t: Throwable) {
                Toast.makeText(this@Perfil, "Error", Toast.LENGTH_SHORT).show()
            }


        })
    }

    //funcion obtiene acepto responsabilidad
    private fun addUsuario(userId: String){
        //referenciamos la instacia retrofit
        val miPesoGt = findViewById<EditText>(R.id.etnPeso)
        miPesoG = miPesoGt.text.toString().toInt()

        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        val userData = addPerfil(user_id = userId, responsability_accept = "1", height = findViewById<EditText>(R.id.etnAltura).text.toString().toInt() ,weight = findViewById<EditText>(R.id.etnPeso).text.toString().toInt(), activity_level_id = findViewById<Spinner>(R.id.spActividadFisica).selectedItemPosition, availability = findViewById<Spinner>(R.id.spDiasDisponibles).selectedItemPosition, session_duration = findViewById<EditText>(R.id.etnDuracion).text.toString().toInt() ,week_mins = findViewById<EditText>(R.id.etnMinutosSemana).text.toString().toInt(), category_id = 1)

        RetroService.addDataUsuario(userData).enqueue(object:Callback<addPerfil>{
            override fun onResponse(call: Call<addPerfil>, response: Response<addPerfil>) {
                Toast.makeText(this@Perfil, "Se guardo su información", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<addPerfil>, t: Throwable) {
                Toast.makeText(this@Perfil, "Error", Toast.LENGTH_SHORT).show()
            }


        })
    }


    private fun callGetUsers() {
        //referenciamos la instacia retrofit
        val RetroService: RetroService = RetroInstance.getRetroInstance().create(RetroService::class.java)
        //llamamos al metodo getuserlist
        val result: Call<UserList> = RetroService.getUsersList()
        //val txtDataName = findViewById<EditText>(R.id.searchEditText)

        //llamada asyncrona enque  para un objeto no funciona para JSON array
        result.enqueue(object: Callback<UserList>{
            override fun onFailure(call: Call<UserList>, t: Throwable) {
                Toast.makeText(this@Perfil, "Error", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<UserList>, response: Response<UserList>?) {
                if(response?.isSuccessful == true) {

                    Toast.makeText(this@Perfil, "ok", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@Perfil, response.code().toString(), Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@Perfil, "sin datos", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun calculaMinutos(valor1: String, valor2:String) {

        if (valor1.isNotBlank() || valor1.isNotEmpty()){
            if (valor1.toInt() > 0) {
                val multiplica = valor1.toInt() * valor2.toInt()
                val minutosSemana = findViewById<EditText>(R.id.etnMinutosSemana)
                minutosSemana.setText(multiplica.toString())
            }
        }else{
            val miDuracion = findViewById<EditText>(R.id.etnDuracion)
            miDuracion.setText("0")
            Toast.makeText(this@Perfil, "no espacios", Toast.LENGTH_SHORT).show()
        }

    }


}






