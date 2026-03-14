package com.example.proyecto1_compi1.ui.server.view

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1_compi1.servidor.controller.ApiCliente
import com.example.proyecto1_compi1.servidor.dto.FormularioDTO
import com.example.proyecto1_compi1.servidor.interfaz.FormularioAPI
import com.example.proyecto1_compi1.servidor.service.SubirFormulario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class FormularioViewModel: ViewModel() {
    private val subirFormularioService = SubirFormulario()

    private val _formularios = MutableStateFlow<List<FormularioDTO>>(emptyList())
    val formularios: StateFlow<List<FormularioDTO>> = _formularios

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _uploadResult = MutableStateFlow<String?>(null)
    val uploadResult: StateFlow<String?> = _uploadResult

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()
    private val _downloadResult = MutableStateFlow<String?>(null)
    val downloadResult: StateFlow<String?> = _downloadResult.asStateFlow()

    fun cargarFormularios() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = subirFormularioService.obtenerListaFormularios()
                if (result != null) {
                    _formularios.value = result
                } else {
                    _error.value = "Error al cargar la lista de formularios"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun subirFormulario(file: File, autor: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _uploadResult.value = null

            try {
                val success = subirFormularioService.subirFormulario(file.absolutePath, autor)
                if (success) {
                    _uploadResult.value = "✅ Formulario '${file.name}' subido exitosamente"
                    // Recargar la lista después de subir
                    cargarFormularios()
                } else {
                    _error.value = "❌ Error al subir el formulario"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun descargarFormulario(id: Int, nombreArchivo: String, context: Context) {
        viewModelScope.launch {
            _isDownloading.value = true
            _downloadProgress.value = 0
            _error.value = null
            _downloadResult.value = null

            try {
                val file = subirFormularioService.descargarFormularioConProgreso(
                    id,
                    nombreArchivo,
                    context
                ) { progress, total ->
                    _downloadProgress.value = progress
                }

                if (file != null) {
                    _downloadResult.value = "Formulario descargado: ${file.absolutePath}"
                } else {
                    _error.value = "Error al descargar el formulario"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isDownloading.value = false
                _downloadProgress.value = 0
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _uploadResult.value = null
        _downloadResult.value = null
    }

}

    /*
    var formularios by mutableStateOf<List<FormularioDTO>>(listOf())

    fun cargarFormulario() {

        viewModelScope.launch {

            try {

                formularios = ApiCliente.api.obtenerJson()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
*/
    //var formulario by mutableStateOf<List<FormularioDTO>>(emptyList())
    //var respuestaJSON by mutableStateOf("Cargando")

    /*
    var formularios by mutableStateOf<List<FormularioDTO>>(emptyList())
    var mensajeError by mutableStateOf<String?>(null)
    var estadoCarga by mutableStateOf("Cargando...")

    fun cargarFormularios() {
        viewModelScope.launch {
            try {
                val response = ApiCliente.api.obtenerJson()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        formularios = body
                        estadoCarga = "Cargados: ${formularios.size} formularios"
                        println("Formularios recibidos: $formularios")
                    } else {
                        estadoCarga = "Respuesta vacía"
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                    estadoCarga = "Fallo HTTP: ${response.code()} - $errorMsg"
                    println("Error del servidor: $errorMsg")
                }
            } catch (e: Exception) {
                estadoCarga = "Excepción: ${e.message}"
                e.printStackTrace()
            }
        }
    }


     */

    /*
    fun cargarFormulario() {

        viewModelScope.launch {

            try {
                formulario = ApiCliente.api.listaFormularios()

                println("Formularios: $formulario")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun concexionPrueba() {
        viewModelScope.launch {

            try {
                val respuesta = ApiCliente.api.obtenerJson()

                respuestaJSON = respuesta
            }catch (e: Exception) {

                respuestaJSON = "Error: ${e.message}"
            }

        }
    }

     */

