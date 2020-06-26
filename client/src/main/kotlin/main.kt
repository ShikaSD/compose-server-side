import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import me.shika.ClientEvent
import me.shika.NodeDescription
import me.shika.NodeUpdate.Command.*
import me.shika.RenderCommand
import org.w3c.dom.*
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.createElement

val socket = WebSocket("ws://localhost:8080/websocket")
private val nodeUpdater by lazy { NodeUpdater(socket) }

fun main() {
    (window.unsafeCast<dynamic>()).socket = socket
    socket.onopen = {
    }
    socket.onmessage = {
        console.log(it.data)
        val json = Json(JsonConfiguration.Stable)
        val data = json.parse(RenderCommand.serializer(), it.data as String)
        for (update in data.nodeUpdates) {
            when (val command = update.command) {
                is Insert -> {
                    nodeUpdater.insert(parentId = update.nodeId, index = command.index, node = command.node)
                }
                is Remove -> {
                    nodeUpdater.remove(parentId = update.nodeId, index = command.index, count = command.count)
                }
                is Move -> {
                    nodeUpdater.move(
                        parentId = update.nodeId,
                        from = command.from,
                        to = command.to,
                        count = command.count
                    )
                }
            }
        }

        for (update in data.valueUpdates) {
            nodeUpdater.update(update.nodeId, update.updates)
        }
    }
}

class NodeUpdater(val socket: WebSocket) {
    private val nodes = HashMap<Long, Any>()
    private var bodyId: Long? = null

    fun insert(parentId: Long, index: Int, node: NodeDescription) {
        if (bodyId == null) {
            bodyId = parentId
            nodes[parentId] = document.body!!
        }
        val parent = nodes[parentId]!! as HTMLElement
        val element = when (node) {
            is NodeDescription.Text -> document.createTextNode(node.value).apply {
                nodes[node.id] = this
            }
            is NodeDescription.Tag -> document.createElement(node.tag) {
                if (node.attributes["className"] != null) {
                    className = node.attributes["className"]!!
                }
                node.events.forEach { event ->
                    addEventListener(event, callback = {
                        val data = ClientEvent(node.id, "click", emptyMap())
                        socket.send(Json.stringify(ClientEvent.serializer(), data))
                    })
                }
                nodes[node.id] = this
            }
        } as Node
        if (index < parent.childNodes.length) {
            val removed = parent.childNodes[index]!!
            parent.insertBefore(element, removed)
        } else {
            parent.appendChild(element)
        }
    }

    fun remove(parentId: Long, index: Int, count: Int) {
        val parent = nodes[parentId]!! as HTMLElement
        repeat(count) {
            val removed = parent.childNodes[index]!!
            parent.removeChild(removed)
        }
    }

    fun move(parentId: Long, from: Int, to: Int, count: Int) {
        val parent = nodes[parentId]!! as HTMLElement
        if (from > to) {
            var current = to
            repeat(count) {
                val node = parent.childNodes[from]!!
                parent.removeChild(node)
                parent.insertBefore(parent.childNodes[current]!!, node)
                current++
            }
        } else {
            repeat(count) {
                val node = parent.childNodes[from]!!
                parent.removeChild(node)
                parent.insertBefore(parent.childNodes[to - 1]!!, node)
            }
        }
    }

    fun update(id: Long, newValues: Map<String, String>) {
        val node = nodes[id]!!
        if (node is Text) {
            node.textContent = newValues["value"]
        }
    }
}
