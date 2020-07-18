package me.shika

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RenderCommand(
    val nodeUpdates: List<NodeUpdate>,
    val valueUpdates: List<ValueUpdate>
)

@Serializable
data class NodeUpdate(
    val nodeId: Long,
    val command: Command
) {
    @Serializable
    sealed class Command {
        @Serializable
        @SerialName("insert")
        data class Insert(
            val index: Int,
            val node: NodeDescription
        ) : Command()

        @Serializable
        @SerialName("remove")
        data class Remove(
            val index: Int,
            val count: Int
        ): Command()

        @Serializable
        @SerialName("move")
        data class Move(
            val from: Int,
            val to: Int,
            val count: Int
        ): Command()
    }
}

@Serializable
sealed class NodeDescription {
    abstract val id: Long

    @Serializable
    @SerialName("tag")
    data class Tag(
        override val id: Long,
        val tag: String,
        val attributes: Map<String, String?>,
        val events: List<String>
    ): NodeDescription()

    @Serializable
    @SerialName("text")
    data class Text(
        override val id: Long,
        val value: String
    ): NodeDescription()
}

@Serializable
data class ValueUpdate(
    val nodeId: Long,
    val events: List<String>,
    val attributes: Map<String, String?>
)


