package com.c360app.appjson

import com.c360app.ejerciciosnuevoyo.global_url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroInstance {

    companion object {
        //val baseUrl = "https://gorest.co.in/public/v1/"
        //val baseUrl = "https://nuevoyoejercicio.mx/api.nuevoyoejerc.com/v1/"
        val baseUrl = global_url.globalUrlAPI

        fun getRetroInstance(): Retrofit {
            val logging = HttpLoggingInterceptor()
            logging.level = (HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
            client.addInterceptor(logging)

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}