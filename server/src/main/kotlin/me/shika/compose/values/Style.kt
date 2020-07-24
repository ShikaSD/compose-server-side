package me.shika.compose.values

import me.shika.compose.core.Modifier
import me.shika.compose.core.wrap

/**
 * Inline css style definition for the node
 * Note that [property] corresponds to JS definition of CSS container rather than real css rules.
 */
data class Style(
    val property: String,
    val value: String
) : Modifier

fun Modifier.style(property: String, value: String): Modifier =
    this wrap Style(property, value)

fun Modifier.textColor(value: String): Modifier =
    style("color", value)

fun Modifier.background(value: String): Modifier =
    style("background", value)

fun Modifier.width(value: String): Modifier =
    style("width", value)

fun Modifier.height(value: String): Modifier =
    style("height", value)

fun Modifier.marginAuto(): Modifier =
    style("margin", "auto")

