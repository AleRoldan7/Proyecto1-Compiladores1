package com.example.proyecto1_compi1.ui.server.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto1_compi1.servidor.controller.ApiCliente
import com.example.proyecto1_compi1.servidor.dto.FormularioDTO
import com.example.proyecto1_compi1.servidor.interfaz.FormularioAPI
import kotlinx.coroutines.launch

class FormularioViewModel: ViewModel() {

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

