package me.shika.compose

import androidx.compose.FrameManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import me.shika.NodeUpdate
import me.shika.RenderCommand
import me.shika.ValueUpdate
import me.shika.compose.core.HtmlNode
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

@OptIn(ImplicitReflectionSerializer::class, UnstableDefault::class)
class RenderCommandDispatcher(
    override val coroutineContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher(),
    private val channel: Channel<RenderCommand> = Channel()
): CoroutineScope, ReceiveChannel<RenderCommand> by channel {
    private val pendingNodeUpdates = mutableListOf<NodeUpdate>()
    private val pendingValueUpdates = mutableListOf<ValueUpdate>()

    fun insert(parent: HtmlNode, index: Int, node: HtmlNode) {
        pendingNodeUpdates +=
            NodeUpdate(
                nodeId = parent.id,
                command = NodeUpdate.Command.Insert(
                    index = index,
                    node = node.toDescription()
                )
            )
    }

    fun remove(parent: HtmlNode, index: Int, count: Int) {
        pendingNodeUpdates +=
            NodeUpdate(
                nodeId = parent.id,
                command = NodeUpdate.Command.Remove(
                    index = index,
                    count = count
                )
            )
    }

    fun move(parent: HtmlNode, from: Int, to: Int, count: Int) {
        pendingNodeUpdates +=
            NodeUpdate(
                nodeId = parent.id,
                command = NodeUpdate.Command.Move(
                    from = from,
                    to = to,
                    count = count
                )
            )
    }

    fun update(node: HtmlNode, events: List<String>, values: Map<String, String?>) {
        if (node.parent == null) return // ignore if node is not attached, it will be serialized fully when added
        pendingValueUpdates +=
            ValueUpdate(
                node.id,
                events,
                values
            )
    }

    private suspend fun send(command: RenderCommand) {
        channel.send(command)
    }

    fun commit() {
        val nodeUpdates = pendingNodeUpdates.toList()
        val valueUpdates = pendingValueUpdates.toList()
        pendingNodeUpdates.clear()
        pendingValueUpdates.clear()

        launch {
            if (nodeUpdates.isEmpty() && valueUpdates.isEmpty()) return@launch
            send(
                RenderCommand(
                    nodeUpdates = nodeUpdates,
                    valueUpdates = valueUpdates
                )
            )
        }

        FrameManager.nextFrame()
    }

    fun dispose() {
        channel.close()
    }
}
