package com.example.proyecto1_compi1.storage

import android.content.Context
import java.io.File

class FormsStorage {
}

fun getFormsStorage(context : Context): List<File> {

    val carpeta = File(context.getExternalFilesDir(null), "formularios")

    if (!carpeta.exists()) {
        return emptyList()
    }

    return carpeta.listFiles()
        ?.filter { it.extension == "form" }
        ?.toList() ?: emptyList()
}