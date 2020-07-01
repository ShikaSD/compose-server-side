package me.shika

import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.dom.createElement

class NodeUpdater(private val rootElement: HTMLElement, private val eventDispatcher: EventDispatcher) {
    private val nodes = HashMap<Long, Any>()
    private var rootId: Long? = null

    fun insert(parentId: Long, index: Int, node: NodeDescription) {
        if (rootId == null) {
            rootId = parentId
            nodes[parentId] = rootElement
        }
        val parent = nodes[parentId]!! as HTMLElement
        val element: Node = when (node) {
            is NodeDescription.Text -> document.createTextNode(node.value).apply {
                nodes[node.id] = this
            }
            is NodeDescription.Tag -> document.createElement(node.tag) {
                with (asDynamic()) {
                    node.attributes.forEach {
                        this[it.key] = it.value
                    }
                }
                node.events.forEach { event ->
                    eventDispatcher.registerEvent(node.id, this, event)
                }
                nodes[node.id] = this
            }
        }
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

    fun update(id: Long, newValues: Map<String, String?>) {
        val node = nodes[id]!!
        if (node is Text) {
            node.textContent = newValues["value"]
        } else if (node is HTMLElement) {
            node.className = newValues["className"].orEmpty()
        }
    }
}
