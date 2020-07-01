package me.shika

import kotlinx.serialization.Serializable

@Serializable
data class ClientEvent(
    val targetId: Long,
    val name: String,
    val values: Map<String, String>
)
