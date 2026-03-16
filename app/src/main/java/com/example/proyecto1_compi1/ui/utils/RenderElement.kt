package com.example.proyecto1_compi1.ui.utils

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.proyecto1_compi1.modelo.question.DropQuestion
import com.example.proyecto1_compi1.modelo.question.MultipleQuestion
import com.example.proyecto1_compi1.modelo.question.OpenQuestion
import com.example.proyecto1_compi1.modelo.question.QuestionModel
import com.example.proyecto1_compi1.modelo.question.QuestionRender
import com.example.proyecto1_compi1.modelo.question.SectionsModel
import com.example.proyecto1_compi1.modelo.question.SelectQuestion
import com.example.proyecto1_compi1.modelo.question.TextModel
import com.example.proyecto1_compi1.modelo.table.TableModel
import com.example.proyecto1_compi1.ui.question.DropQuestionUI
import com.example.proyecto1_compi1.ui.question.MultipleQuestionUI
import com.example.proyecto1_compi1.ui.question.SectionUI
import com.example.proyecto1_compi1.ui.question.SelectQuestionUI
import com.example.proyecto1_compi1.ui.question.TextUi
import com.example.proyecto1_compi1.ui.table.TableUI

@Composable
fun RenderElement(element: Any) {
    Log.d("RenderElement", element.javaClass.simpleName)
    when (element) {

        is TextModel -> TextUi(element)

        is SectionsModel -> SectionUI(element)

        is TableModel -> TableUI(element)

        is OpenQuestion,
        is DropQuestion,
        is SelectQuestion,
        is MultipleQuestion -> {

            QuestionRender(element)
        }

        else -> {
            Text("No se puede crear el elemento: ${element.javaClass.simpleName}")
        }

    }
}