package com.c360app.appjson

import android.text.Editable
import com.google.gson.annotations.SerializedName

//data class UserList (val data: List<User>)
data class UserList (val Perfil: List<Perfil>)
//data class User(val id: String?, val name: String?, val email: String?,val gender: String?, val status: String?)
data class Perfil(
    @SerializedName("user_id") val user_id: String,
    @SerializedName("responsability_accept") val responsability_accept: String?,
    @SerializedName("height") val height: String?,
    @SerializedName("weight") val weight: String?,
    @SerializedName("activity_level_id") val activity_level_id: String?,
    @SerializedName("availability") val availability: String?,
    @SerializedName("session_duration") val session_duration: String?,
    @SerializedName("week_mins") val week_mins: String?,
    @SerializedName("category_id") val category_id: String?)

data class cPerfil(
        @SerializedName("state") val state: Int,
        @SerializedName("Perfil") val Perfil: Perfil)
//data class UserResponse(val code: Int?, val meta: String?, val data: User?)
data class UserResponse(val code: Int?, val Perfil: Perfil?)
//parametro acepto responsabilidad
data class acepto(@SerializedName("user_id") val user_id: Int)

data class editPerfil(
    @SerializedName("user_id") val user_id: String?,
    @SerializedName("responsability_accept") val responsability_accept: String?,
    @SerializedName("height") val height: Int?,
    @SerializedName("weight") val weight: Int?,
    @SerializedName("activity_level_id") val activity_level_id: Int?,
    @SerializedName("availability") val availability: Int?,
    @SerializedName("week_mins") val week_mins: Int,
    @SerializedName("category_id") val category_id: Int?)
data class editPerfilResponse(@SerializedName("state") val state: Int?, @SerializedName("message") val message: String?)

//parametros para rutinas
data class aceptoRutina(@SerializedName("act_level_id") val act_level_id: String?, @SerializedName("availability") val availability: String?)
data class Rutinas(
    @SerializedName("routine_order") val routine_order: String,
    @SerializedName("routine_id") val routine_id: String,
    @SerializedName("routine_description") val routine_description: String,
    @SerializedName("activity_level_id") val activity_level_id: String,
    @SerializedName("availability") val availability: String,
    @SerializedName("rsx_url") val rsx_url: String,
    @SerializedName("rsx_type_name") val rsx_type_name: String)

data class UserRutina(@SerializedName("state") val state: Int,
                      @SerializedName("Rutinas") val Rutinas: List<Rutinas>)

//rutinas por ID
data class aceptoIdRutina(@SerializedName("routine_id") val routine_id: String)
data class UserIdRutina(@SerializedName("state") val state: Int,
                      @SerializedName("Rutinas") val Rutina: Rutinas)


//rutinas por ID
data class UserDocAero(@SerializedName("state") val state: Int,
                        @SerializedName("aerobic_strength_integration") val aerobic_strength_integration: List<DocAeroFuerza>)
data class DocAeroFuerza(@SerializedName("asi_id") val asi_id: String,
    @SerializedName("activity_level_id") val activity_level_id: String,
    @SerializedName("availability") val availability: String,
    @SerializedName("rsx_url") val rsx_url: String)

///ejercicios
data class aceptoIdEjercicio(@SerializedName("muscle_group_id") val muscle_group_id: String)
data class Ejercicio(
    @SerializedName("exercise_order") val exercise_order: String,
    @SerializedName("exercise_id") val exercise_id: String,
    @SerializedName("exercise_name") val exercise_name: String,
    @SerializedName("rsx_url") val rsx_url: String,
    @SerializedName("rsx_type_name") val rsx_type_name: String)
data class UserEjercicio(@SerializedName("state") val state: Int,
                       @SerializedName("Ejercicios") val Ejercicios: List<Ejercicio>)

/// documento aerobica SEND MAIL
//parametro acepto responsabilidad
data class aceptoDocAero(@SerializedName("asi_id") val asi_id: String?, @SerializedName("email") val email: String?)
data class DocAeroResponse(@SerializedName("state") val state: Int?, @SerializedName("message") val message: String?)

//rutinas
data class aceptoDocRutina(@SerializedName("routine_id") val routine_id: String?, @SerializedName("email") val email: String?)
data class DocRutinaResponse(@SerializedName("state") val state: Int?, @SerializedName("message") val message: String?)

//Ejercicios
data class aceptoDocEjercicio(@SerializedName("exercise_id") val exercise_id: String?, @SerializedName("email") val email: String?)
data class DocEjercicioResponse(@SerializedName("state") val state: Int?, @SerializedName("message") val message: String?)

//// add user
data class addPerfil(
    @SerializedName("user_id") val user_id: String,
    @SerializedName("responsability_accept") val responsability_accept: String?,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("activity_level_id") val activity_level_id: Int?,
    @SerializedName("availability") val availability: Int?,
    @SerializedName("session_duration") val session_duration: Int?,
    @SerializedName("week_mins") val week_mins: Int,
    @SerializedName("category_id") val category_id: Int?)

