package com.c360app.appjson

import android.widget.EditText
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Path

interface RetroService {

    //get all : https://gorest.co.in/public/v1/users
    //get all : http://demo4531215.mockable.io/profiles/get
    //@GET("user")
    @GET("get")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getUsersList(): Call<UserList>

    //POST acepto reposnsabilidad
    @POST("users/getresp")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun aceptoResponsabilidad(@Body userData: acepto): Call<acepto>

    //POST acepto reposnsabilidad
    @POST("profiles/get")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getPerfilUsuario(@Body userData: acepto): Call<cPerfil>
    /////////////////SOLO PARA PERFIL
    /////////////////////////////////////////////////////////////////

    ////////////////////////////////////////
    //////////////// SOLO Ejercicios
    //POST acepto reposnsabilidad
    @POST("profiles/edit")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun setDataUsuario(@Body userData: editPerfil): Call<editPerfilResponse>

    //agrega usuario
    @POST("profiles/add")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun addDataUsuario(@Body userData: addPerfil): Call<addPerfil>

    //POST acepto reposnsabilidad
    @POST("users/setresp")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun setAceptoResponsabilidad(@Body userData: acepto): Call<acepto>



    //POST visite ejercicios
    @POST("users/setsect")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun setVisiteEjercicios(@Body userData: acepto): Call<acepto>

    //POST get visito ejercicios
    @POST("users/getsect")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getVisiteEjercicios(@Body userData: acepto): Call<acepto>

    //POST acepto reposnsabilidad
    @POST("routines/getall")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getRutinasUsuario(@Body userData: aceptoRutina): Call<UserRutina>

    //POST acepto reposnsabilidad
    @POST("routines/get")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getOneRutina(@Body userData: aceptoIdRutina): Call<UserIdRutina>

    //POST acepto reposnsabilidad
    @POST("aerobic_strength_integrations/get")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getDocAero(@Body userData: aceptoRutina): Call<UserDocAero>

    //POST acepto reposnsabilidad
    @POST("exercises/getall")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun getUsrEjercicios(@Body userData: aceptoIdEjercicio): Call<UserEjercicio>

    //POST SEND MAIL Doc Aero
    @POST("aerobic_strength_integrations/send")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun sendMailDocAero(@Body userData: aceptoDocAero): Call<DocAeroResponse>

    //POST SEND MAIL Doc Rutina
    @POST("routines/send")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun sendMailDocRutina(@Body userData: aceptoDocRutina): Call<DocRutinaResponse>

    //POST SEND MAIL Doc Rutina
    @POST("exercises/send")
    @Headers("Accept:application/json","Content-Type:application/json")
    fun sendMailDocEjercicio(@Body userData: aceptoDocEjercicio): Call<DocEjercicioResponse>

    ////////////////////////////////////////



}