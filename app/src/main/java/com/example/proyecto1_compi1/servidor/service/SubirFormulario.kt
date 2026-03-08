package com.example.proyecto1_compi1.servidor.service

import com.example.proyecto1_compi1.servidor.controller.ApiCliente
import com.example.proyecto1_compi1.servidor.controller.ApiCliente.api
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class SubirFormulario {

    suspend fun subirFormulario(ruta: String, autor: String) {

        val file = File(ruta)

        val requestFile = file.asRequestBody("multipart/form-data".toMediaType())

        val archivo = MultipartBody.Part.createFormData("archivo", file.name, requestFile)

        val autorBody = autor.toRequestBody("text/plain".toMediaType())

        val response = ApiCliente.api.subirFormulario(archivo, autorBody)

        if (response.isSuccessful) {
            println("Si se subio")
        } else {
            println("No jalooooo")
        }
    }
}