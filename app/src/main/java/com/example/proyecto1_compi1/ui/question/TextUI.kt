package com.example.proyecto1_compi1.ui.question

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyecto1_compi1.modelo.question.TextModel
import com.example.proyecto1_compi1.ui.utils.EngineStyle

@Composable
fun TextUi(textModel: TextModel) {

    val style = EngineStyle.resolveStyle(textModel.styles)
    val width = textModel.width ?: 300
    val height = textModel.height ?: 60
    val content = textModel.content ?: ""


    val modifier = Modifier
        .padding(vertical = 4.dp)
        .then(
            if (width > 0) Modifier.width(width.dp) else Modifier.fillMaxWidth()
        )
        .then(
            if (height > 0) Modifier.height(height.dp) else Modifier.height(60.dp)
        )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = style.backgroundColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = content,
                color = style.textColor,
                fontFamily = style.fontFamily,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}