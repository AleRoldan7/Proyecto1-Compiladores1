package com.example.proyecto1_compi1.servidor.interfaz

import com.example.proyecto1_compi1.servidor.dto.FormularioDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

interface FormularioAPI {

    @Multipart
    @POST("api/v1/formularios/subir")
    suspend fun subirFormulario(
        @Part archivo: MultipartBody.Part,
        @Part("autor") autor: RequestBody
    ): Response<Map<String,String>>

    //@GET("api/v1/formularios/lista-formularios")
    //suspend fun listaFormularios() : List<FormularioDTO>

    @GET("api/v1/formularios/{id}")
    @Streaming
    suspend fun descargarFormulario(
        @Path("id") idFormulario: Int,
    ) : Response<ResponseBody>

    @GET("api/v1/formularios/lista-formularios")
    suspend fun obtenerJson() : List<FormularioDTO>
}