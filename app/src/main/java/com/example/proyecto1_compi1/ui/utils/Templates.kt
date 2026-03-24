// Templates.kt - crear en com.example.proyecto1_compi1.ui.utils
package com.example.proyecto1_compi1.ui.utils

data class CodeTemplate(
    val name: String,
    val description: String,
    val code: String,
    val icon: String = "📄"
)

object CodeTemplates {
    val templates = listOf(
        CodeTemplate(
            name = "Sección Básica",
            description = "Crea una sección con propiedades básicas",
            icon = "📦",
            code = """
SECTION [
    width: 400,
    height: 600,
    pointX: 0,
    pointY: 0,
    orientation: VERTICAL,
    elements: {
        TEXT [
            content: "Mi primera sección",
            styles [
                "color": #FFFFFF,
                "text size": 16
            ]
        ]
    },
    styles [
        "background color": #1E1E2E,
        "border": (1, DOTTED, #FFFFFF)
    ]
]
            """.trimIndent()
        ),

        CodeTemplate(
            name = "Pregunta Abierta",
            description = "Pregunta de respuesta libre",
            icon = "✏️",
            code = """
OPEN_QUESTION [
    width: 400,
    height: 150,
    label: "¿Cuál es tu opinión sobre el tema?",
    styles [
        "color": #2E7D32,
        "background color": #E8F5E9,
        "font family": MONO,
        "text size": 14
    ]
]
            """.trimIndent()
        ),

        CodeTemplate(
            name = "Pregunta Desplegable con PokéAPI",
            description = "Dropdown con Pokémon de la PokéAPI",
            icon = "🎮",
            code = """
DROP_QUESTION [
    width: 400,
    height: 200,
    label: "Selecciona tu Pokémon favorito:",
    options: who_is_that_pokemon(NUMBER, 1, 10),
    correct: 0,
    styles [
        "color": #2E7D32,
        "background color": #E8F5E9,
        "font family": MONO,
        "text size": 14
    ]
]
            """.trimIndent()
        ),

        CodeTemplate(
            name = "Pregunta Selección Única",
            description = "Selección única con opciones",
            icon = "🔘",
            code = """
SELECT_QUESTION [
    width: 400,
    height: 250,
    label: "¿Cuál es tu lenguaje de programación favorito?",
    options: {"Kotlin", "Java", "Python", "JavaScript"},
    correct: 0,
    styles [
        "color": #1565C0,
        "background color": #E3F2FD,
        "font family": SANS_SERIF,
        "text size": 14
    ]
]
            """.trimIndent()
        ),

        CodeTemplate(
            name = "Pregunta Múltiple",
            description = "Selección múltiple con varias opciones",
            icon = "☑️",
            code = """
MULTIPLE_QUESTION [
    width: 450,
    height: 300,
    label: "¿Qué tecnologías te interesan?",
    options: {"Android", "iOS", "Web", "Backend", "IA"},
    correct: {0, 2, 4},
    styles [
        "color": #6A1B9A,
        "background color": #F3E5F5,
        "font family": CURSIVE,
        "text size": 14
    ]
]
            """.trimIndent()
        ),

        CodeTemplate(
            name = "Tabla con Datos",
            description = "Tabla para mostrar información estructurada",
            icon = "📊",
            code = """
TABLE [
    width: 500,
    height: 300,
    pointX: 0,
    pointY: 0,
    elements: {
        [
            { TEXT [ content: "Nombre", styles [ "text size": 12 ] ] },
            { TEXT [ content: "Edad", styles [ "text size": 12 ] ] },
            { TEXT [ content: "Ciudad", styles [ "text size": 12 ] ] }
        ],
        [
            { TEXT [ content: "Juan", styles [ "text size": 12 ] ] },
            { TEXT [ content: "25", styles [ "text size": 12 ] ] },
            { TEXT [ content: "Guatemala", styles [ "text size": 12 ] ] }
        ],
        [
            { TEXT [ content: "María", styles [ "text size": 12 ] ] },
            { TEXT [ content: "30", styles [ "text size": 12 ] ] },
            { TEXT [ content: "Quetzaltenango", styles [ "text size": 12 ] ] }
        ]
    },
    styles [
        "border": (1, SOLID, #FFFFFF)
    ]
]
            """.trimIndent()
        ),

        CodeTemplate(
            name = "Ciclo FOR con Variables",
            description = "Ejemplo de ciclo FOR con variables",
            icon = "🔄",
            code = """
number limite = 5

FOR (i in 1 .. limite) {
    SECTION [
        width: 400,
        height: 100,
        pointX: 0,
        pointY: i * 120,
        elements: {
            TEXT [
                content: "Elemento " + i,
                styles [
                    "background color": #E1BEE7,
                    "text size": 14
                ]
            ]
        }
    ]
}
            """.trimIndent()
        ),

        CodeTemplate(
            name = "Condicional IF",
            description = "Ejemplo de estructura condicional",
            icon = "⚡",
            code = """
number puntaje = 85

IF (puntaje >= 90) {
    TEXT [
        content: "Excelente trabajo!",
        styles [ "color": #4CAF50 ]
    ]
} ELSE IF (puntaje >= 70) {
    TEXT [
        content: "Buen trabajo!",
        styles [ "color": #FF9800 ]
    ]
} ELSE {
    TEXT [
        content: "Sigue mejorando!",
        styles [ "color": #F44336 ]
    ]
}
            """.trimIndent()
        )
    )
}