package me.shika.compose.values

import me.shika.compose.core.Modifier
import me.shika.compose.core.wrap

/**
 * Controls JS properties of DOM elements
 *
 * @see [Attribute] for adding attributes to html tags
 */
data class Property(
    val key: String,
    val value: String?
) : Modifier

fun Modifier.property(key: String, value: String): Modifier =
    this wrap Property(key, value)

fun Modifier.value(text: String): Modifier =
    property("value", text)
