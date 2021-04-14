package me.shika.compose.values

import me.shika.compose.core.Modifier
import me.shika.compose.core.wrap

/**
 * Html tag attributes of the node
 *
 * @see [Property] for control of properties from JS model
 */
data class Attribute(
    val key: String,
    val value: String
) : Modifier

fun Modifier.attribute(key: String, value: String): Modifier =
    this wrap Attribute(key, value)

fun Modifier.className(value: String): Modifier =
    attribute("class", value)

fun Modifier.id(value: String): Modifier =
    attribute("id", value)

fun Modifier.labelFor(value: String): Modifier =
    attribute("for", value)
