// ═══════════════════════════════════════════════════════════════
// 1. En DropQuestion.java — agrega el campo optionsPoke si no existe
// ═══════════════════════════════════════════════════════════════
//
// private ArrayList<String> optionsPoke = null; // parámetros de PokeAPI
//
// public ArrayList<String> getOptionsPoke() { return optionsPoke; }
//
// En addProperty() agrega:
// case "options_poke":
//     if (prop.getValue() instanceof ArrayList) {
//         optionsPoke = (ArrayList<String>) prop.getValue();
//     }
//     break;

// ═══════════════════════════════════════════════════════════════
// 2. En AndroidManifest.xml — agrega permiso de internet
// ═══════════════════════════════════════════════════════════════
//
// <uses-permission android:name="android.permission.INTERNET" />
//
// Va ANTES del tag <application>

// ═══════════════════════════════════════════════════════════════
// 3. En build.gradle (app) — agrega dependencias si hacen falta
// ═══════════════════════════════════════════════════════════════
//
// Las corrutinas ya deben estar porque Compose las usa.
// Si no están:
//
// implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// ═══════════════════════════════════════════════════════════════
// 4. PKMCache.kt — verifica que existe
// ═══════════════════════════════════════════════════════════════
/*
package com.example.proyecto1_compi1.generate_lenguaje

object PKMCache {
    var elements: List<Any>? = null
}
*/
// ═══════════════════════════════════════════════════════════════
// 5. Flujo completo who_is_that_pokemon → PokeAPI → UI
// ═══════════════════════════════════════════════════════════════
//
// .form:
//   special qPoke = DROP_QUESTION [
//       who_is_that_pokemon(NUMBER, 1, 10),
//       label: "Elige un Pokémon",
//       correct: 0
//   ]
//   qPoke.draw()
//
// Parser CUP:
//   poke_call retorna ArrayList<String>["NUMBER", "1", "10"]
//   → PropertyItem("options_poke", ["NUMBER", "1", "10"])
//   → DropQuestion.optionsPoke = ["NUMBER", "1", "10"]
//
// DropAnswerUI (Compose):
//   LaunchedEffect detecta optionsPoke != null
//   → PokeRepository.obtenerPokemones(1, 10) en Dispatchers.IO
//   → GET https://pokeapi.co/api/v2/pokemon/1 → "bulbasaur" → "Bulbasaur"
//   → GET https://pokeapi.co/api/v2/pokemon/2 → "ivysaur" → "Ivysaur"
//   → ... hasta 10
//   → opciones = ["Bulbasaur", "Ivysaur", ..., "Caterpie"]
//   → Muestra dropdown con #1 Bulbasaur, #2 Ivysaur, etc.
//
// Al guardar (guardarRespuestas):
//   Spec: "Al guardar las respuestas se debe guardar como si el usuario
//          lo hubiera escrito."
//   → Guarda el NOMBRE del pokémon, no su índice
//   → <drop=...,{"Bulbasaur","Ivysaur",...},2/>  ← índice del seleccionado
