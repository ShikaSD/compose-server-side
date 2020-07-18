package me.shika.compose.values

import me.shika.compose.core.Modifier

/**
 * Controls JS properties of DOM elements
 *
 * @see [Attribute] for adding attributes to html tags
 */
data class Property(
    val key: String,
    val value: String?
) : Modifier

fun Modifier.value(text: String): Modifier =
    this + Property("value", text)
