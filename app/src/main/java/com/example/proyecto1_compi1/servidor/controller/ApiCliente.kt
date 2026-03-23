package com.example.proyecto1_compi1.servidor.controller

import com.example.proyecto1_compi1.servidor.interfaz.FormularioAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiCliente {

    private const val BASE_URL = "https://loretta-nonoperable-aphylly.ngrok-free.dev/Api-Pkm-Forms/"

    val api: FormularioAPI by lazy {

        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(FormularioAPI::class.java)
    }
}