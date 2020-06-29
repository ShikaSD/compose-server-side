package me.shika

import kotlinx.serialization.Serializable

@Serializable
class ClientEvent(
    val targetId: Long,
    val name: String,
    val values: Map<String, String>
)
