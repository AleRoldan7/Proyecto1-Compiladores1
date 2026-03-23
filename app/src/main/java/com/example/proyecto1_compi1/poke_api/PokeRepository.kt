package com.example.proyecto1_compi1.poke

import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// ─────────────────────────────────────────────────────────────────────────────
//  Modelo de datos de un Pokémon
// ─────────────────────────────────────────────────────────────────────────────
data class PokemonEntry(
    val id:   Int,
    val name: String  // nombre capitalizado, e.g. "Bulbasaur"
)

// ─────────────────────────────────────────────────────────────────────────────
//  Tipos de número en la Pokédex que acepta who_is_that_pokemon()
//  Spec: who_is_that_pokemon(NUMBER, desde, hasta)
//  NUMBER = tipo de número en la pokédex (siempre NUMBER por ahora)
// ─────────────────────────────────────────────────────────────────────────────
object PokeRepository {

    private const val BASE_URL = "https://pokeapi.co/api/v2/pokemon"
    private const val TAG      = "PokeRepository"

    // Caché en memoria para evitar requests repetidos
    private val cache = HashMap<Int, String>()

    /**
     * Obtiene los nombres de pokemones en el rango [desde, hasta] (inclusive).
     * Llamar desde una corrutina (suspend).
     *
     * @param desde  número inicial en la pokédex (1-based)
     * @param hasta  número final en la pokédex (inclusive)
     * @return lista de nombres capitalizados, e.g. ["Bulbasaur", "Ivysaur", ...]
     */
    suspend fun obtenerPokemones(desde: Int, hasta: Int): List<String> =
        withContext(Dispatchers.IO) {
            val resultado = mutableListOf<String>()

            val rangoValido = desde.coerceAtLeast(1)..hasta.coerceAtLeast(desde)
            for (id in rangoValido) {
                val nombre = obtenerNombre(id)
                if (nombre != null) resultado.add(nombre)
            }

            Log.d(TAG, "obtenerPokemones($desde..$hasta) → $resultado")
            resultado
        }

    /**
     * Versión sincrónica — para usar desde Java (el parser CUP).
     * Bloquea el hilo actual — NO llamar en el hilo principal.
     */
    fun obtenerPokemonesSync(desde: Int, hasta: Int): List<String> {
        return runBlocking { obtenerPokemones(desde, hasta) }
    }

    // ── Obtiene el nombre de un pokémon por su ID ─────────────────────────────
    private fun obtenerNombre(id: Int): String? {
        // Revisa caché primero
        cache[id]?.let { return it }

        return try {
            val url        = URL("$BASE_URL/$id")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod    = "GET"
                connectTimeout   = 5000
                readTimeout      = 5000
                setRequestProperty("Accept", "application/json")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val body   = connection.inputStream.bufferedReader().readText()
                val json   = JSONObject(body)
                val nombre = json.getString("name").capitalizar()
                cache[id]  = nombre
                connection.disconnect()
                nombre
            } else {
                Log.w(TAG, "HTTP ${connection.responseCode} para id=$id")
                connection.disconnect()
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo pokémon $id: ${e.message}")
            null
        }
    }

    // ── Capitaliza "bulbasaur" → "Bulbasaur" ─────────────────────────────────
    private fun String.capitalizar(): String =
        replaceFirstChar { it.uppercase() }

    // ── Limpia el caché ───────────────────────────────────────────────────────
    fun limpiarCache() { cache.clear() }
}
