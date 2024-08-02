package com.theghostofinky.trackingprotector

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle



@Composable
fun AboutView(){
    val annotatedString = buildAnnotatedString {
        append("")

        pushStringAnnotation("","")
        withStyle(SpanStyle(color= MaterialTheme.colorScheme.primary)){
            append("")
        }
        pop()
    }
    ClickableText(text = annotatedString) {
        annotatedString.getStringAnnotations("", 0, 1).firstOrNull()?.let {

        }
    }
}