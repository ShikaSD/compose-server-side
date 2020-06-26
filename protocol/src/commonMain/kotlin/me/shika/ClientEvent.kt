package me.shika

import kotlinx.serialization.Serializable

@Serializable
open class ClientEvent(
    val targetId: Long,
    val name: String,
    val values: Map<String, String>
)
