import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.WebSocket
import org.w3c.dom.get
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
        val data: dynamic = JSON.parse(it.data as String)

        when (data.type) {
            "insert" -> nodeUpdater.insert(
                parentId = data.parent,
                index = data.index,
                node = when (data.node.type) {
                    "tag" -> SerializedNode.Tag(
                        id = data.node.id,
                        tag = data.node.tag,
                        attributes = data.node.attributes,
                        events = (data.node.events as Array<String>).toList()
                    )
                    "text" -> SerializedNode.Text(
                        value = data.node.value
                    )
                    else -> throw IllegalArgumentException("Unknown node type")
                }
            )
            "remove" -> nodeUpdater.remove(
                parentId = data.parent,
                index = data.index,
                count = data.count
            )
            "move" -> nodeUpdater.move(
                parentId = data.parent,
                from = data.from,
                to = data.to,
                count = data.count
            )
        }
    }
}

sealed class SerializedNode {
    data class Tag(val id: Long, val tag: String, val attributes: dynamic, val events: List<String>) : SerializedNode()
    data class Text(val value: String) : SerializedNode()
}

class NodeUpdater(val socket: WebSocket) {
    private val nodes = HashMap<Long, HTMLElement>()
    private var bodyId: Long? = null

    fun insert(parentId: Long, index: Int, node: SerializedNode) {
        if (bodyId == null) {
            bodyId = parentId
            nodes[parentId] = document.body!!
        }
        val parent = nodes[parentId]!!
        val element = when (node) {
            is SerializedNode.Text -> document.createTextNode(node.value)
            is SerializedNode.Tag -> document.createElement(node.tag) {
                if (node.attributes.className) {
                    className = node.attributes.className
                }
                node.events.forEach { event ->
                    addEventListener(event, callback = {
                        val data = ServerMessage(
                            type = "event",
                            payload = EventMessagePayload(node.id, event)
                        )
                        socket.send(JSON.stringify(data))
                    })
                }
                nodes[node.id] = this as HTMLElement
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
        val parent = nodes[parentId]!!
        repeat(count) {
            val removed = parent.childNodes[index]!!
            parent.removeChild(removed)
        }
    }

    fun move(parentId: Long, from: Int, to: Int, count: Int) {
        val parent = nodes[parentId]!!
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
}

data class ServerMessage(
    val type: String,
    val payload: EventMessagePayload
)

data class EventMessagePayload(
    val id: Long,
    val type: String
)
