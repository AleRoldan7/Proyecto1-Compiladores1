package com.example.proyecto1_compi1.servidor.controller

import android.content.Context
import com.example.proyecto1_compi1.servidor.interfaz.FormularioAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiCliente {

    private const val PREFS_NAME = "server_prefs"
    private const val KEY_BASE_URL = "ngrok_base_url"

    private const val DEFAULT_URL =
        "https://loretta-nonoperable-aphylly.ngrok-free.dev/Api-Pkm-Forms/"

    @Volatile
    private var api: FormularioAPI? = null
    private var _currentBase: String = ""

    fun getApi(context: Context): FormularioAPI {
        val url = getSavedUrl(context)
        if (api == null || url != _currentBase) {
            synchronized(this) {
                if (api == null || url != _currentBase) {
                    _currentBase = url
                    api = Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(FormularioAPI::class.java)
                }
            }
        }
        return api!!
    }


    fun getSavedUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_BASE_URL, DEFAULT_URL)
            ?.trimEnd('/') + "/"
    }

    fun saveUrl(context: Context, url: String) {
        val clean = url.trim().trimEnd('/') + "/"
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_BASE_URL, clean)
            .apply()
        synchronized(this) { api = null; _currentBase = "" }
    }

    fun resetToDefault(context: Context) = saveUrl(context, DEFAULT_URL)
}