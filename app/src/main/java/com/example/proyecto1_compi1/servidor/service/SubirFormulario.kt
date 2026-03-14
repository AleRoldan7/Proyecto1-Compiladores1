package com.example.proyecto1_compi1.servidor.service

import android.content.Context
import android.util.Log
import com.example.proyecto1_compi1.servidor.controller.ApiCliente
import com.example.proyecto1_compi1.servidor.controller.ApiCliente.api
import com.example.proyecto1_compi1.servidor.dto.FormularioDTO
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class SubirFormulario {

    /*
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
     */

    private val TAG = "SubirFormulario"

    suspend fun subirFormulario(ruta: String, autor: String): Boolean {
        return try {
            val file = File(ruta)

            if (!file.exists()) {
                Log.e(TAG, "El archivo no existe: $ruta")
                return false
            }

            val requestFile = file.asRequestBody("application/octet-stream".toMediaType())
            val archivo = MultipartBody.Part.createFormData("archivo", file.name, requestFile)
            val autorBody = autor.toRequestBody("text/plain".toMediaType())

            val response = ApiCliente.api.subirFormulario(archivo, autorBody)

            if (response.isSuccessful) {
                val body = response.body()
                Log.d(TAG, "Archivo subido: ${body?.get("mensaje")}")
                true
            } else {
                Log.e(TAG, "Error en la respuesta: ${response.code()} - ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al subir archivo: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun obtenerListaFormularios(): List<FormularioDTO>? {
        return try {
            val response = ApiCliente.api.obtenerJson()
            Log.d(TAG, "Lista obtenida: ${response.size} formularios")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener lista: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun descargarFormulario(id: Int, nombreArchivo: String, context: Context): File? {
        return try {
            val response = ApiCliente.api.descargarFormulario(id)

            if (response.isSuccessful && response.body() != null) {
                // Crear carpeta de descargas en el almacenamiento externo de la app
                val downloadDir = File(context.getExternalFilesDir(null), "descargas")
                if (!downloadDir.exists()) {
                    downloadDir.mkdirs()
                }

                val file = File(downloadDir, nombreArchivo)

                // Guardar el archivo
                response.body()?.byteStream()?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                Log.d(TAG, "Archivo descargado: ${file.absolutePath}")
                file
            } else {
                Log.e(TAG, "Error al descargar: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al descargar: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // Versión con progreso de descarga
    suspend fun descargarFormularioConProgreso(
        id: Int,
        nombreArchivo: String,
        context: Context,
        onProgress: (Int, Int) -> Unit
    ): File? {
        return try {
            val response = ApiCliente.api.descargarFormulario(id)

            if (response.isSuccessful && response.body() != null) {
                val downloadDir = File(context.getExternalFilesDir(null), "descargas")
                if (!downloadDir.exists()) {
                    downloadDir.mkdirs()
                }

                val file = File(downloadDir, nombreArchivo)

                // Obtener el tamaño total del archivo
                val contentLength = response.body()?.contentLength() ?: -1

                response.body()?.byteStream()?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytesRead = 0L

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead

                            // Reportar progreso
                            if (contentLength > 0) {
                                val progress = (totalBytesRead * 100 / contentLength).toInt()
                                onProgress(progress, 100)
                            }
                        }
                    }
                }

                Log.d(TAG, "Archivo descargado: ${file.absolutePath}")
                file
            } else {
                Log.e(TAG, "Error al descargar: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al descargar: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}