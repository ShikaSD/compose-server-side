package me.shika.compose.styles

import me.shika.compose.core.Modifier

data class Style(
    val property: String,
    val value: String
) : Modifier

fun Modifier.style(property: String, value: String): Modifier =
    this + Style(property, value)

fun Modifier.textColor(value: String): Modifier =
    style("color", value)

fun Modifier.background(value: String): Modifier =
    style("background", value)
