package com.example.proyecto1_compi1.ui.server.view

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1_compi1.analizador.pkm.LexerPKM
import com.example.proyecto1_compi1.analizador.pkm.ParserPKM
import com.example.proyecto1_compi1.generate_lenguaje.PKMCache
import com.example.proyecto1_compi1.modelo.forms.FormsModel
import com.example.proyecto1_compi1.modelo.forms.ResultParser
import com.example.proyecto1_compi1.servidor.controller.ApiCliente
import com.example.proyecto1_compi1.servidor.dto.FormularioDTO
import com.example.proyecto1_compi1.servidor.interfaz.FormularioAPI
import com.example.proyecto1_compi1.servidor.service.SubirFormulario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FormularioViewModel: ViewModel() {
    //private val subirFormularioService = SubirFormulario()

    private fun service(context: Context) =
        SubirFormulario(context)

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

    private val _isOpening  = MutableStateFlow(false)
    val isOpening: StateFlow<Boolean> = _isOpening.asStateFlow()

    private val _openResult = MutableStateFlow<String?>(null)
    val openResult: StateFlow<String?> = _openResult.asStateFlow()

    fun cargarFormularios(context: Context) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val result =
                    service(context)
                        .obtenerListaFormularios()

                if (result != null) {

                    _formularios.value = result

                } else {

                    _error.value =
                        "Error al cargar formularios"
                }

            } catch (e: Exception) {

                _error.value =
                    "Error conexión: ${e.message}"

            } finally {

                _isLoading.value = false
            }

        }

    }

    fun subirFormulario(
        file: File,
        autor: String,
        context: Context
    ) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val success =
                    service(context)
                        .subirFormulario(
                            file.absolutePath,
                            autor
                        )

                if (success) {

                    _uploadResult.value =
                        "Formulario subido"

                    cargarFormularios(context)

                } else {

                    _error.value =
                        "Error al subir"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message
            }

            _isLoading.value = false

        }

    }

    fun descargarFormulario(
        id: Int,
        nombreArchivo: String,
        context: Context
    ) {

        viewModelScope.launch {

            _isDownloading.value = true
            _downloadProgress.value = 0

            try {

                val file =
                    service(context)
                        .descargarFormularioConProgreso(
                            id,
                            nombreArchivo,
                            context
                        ) { progress, _ ->

                            _downloadProgress.value =
                                progress
                        }

                if (file != null) {

                    _downloadResult.value =
                        "Descargado en:\n${file.absolutePath}"

                } else {

                    _error.value =
                        "No se pudo descargar"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message
            }

            _isDownloading.value = false

        }

    }




    fun clearMessages() {
        _error.value = null
        _uploadResult.value = null
        _downloadResult.value = null
    }

}

