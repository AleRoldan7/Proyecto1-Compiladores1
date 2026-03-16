package com.example.proyecto1_compi1.ui.question

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1.modelo.question.SectionsModel
import com.example.proyecto1_compi1.ui.utils.EngineStyle
import com.example.proyecto1_compi1.ui.utils.RenderElement

@Composable
fun SectionUI(section: SectionsModel) {
    val style = EngineStyle.resolveStyle(section.styles)

    val modifier = Modifier
        .offset(section.pointX.dp, section.pointY.dp)
        .width(section.width.dp)
        .height(section.height.dp)
        .background(style.backgroundColor)

    if(section.orientation.name == "HORIZONTAL"){

        Row(modifier = modifier){

            section.elements.forEach{

                RenderElement(it)

            }

        }

    }else{

        Column(modifier = modifier){

            section.elements.forEach{

                RenderElement(it)

            }

        }

    }
}