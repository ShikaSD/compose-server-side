package me.shika

import org.w3c.dom.*
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
                this as HTMLElement
                diffAttributes(node.attributes)
                diffStyles(node.styles)

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
            eventDispatcher.clearEvents(removed)
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

    fun update(id: Long, newEvents: List<String>, newAttrs: Map<String, String?>, newStyles: Map<String, String>) {
        val node = nodes[id]!!
        if (node is Text) {
            node.textContent = newAttrs["value"]
        } else if (node is HTMLElement) {
            eventDispatcher.updateEvents(id, node, newEvents)
            node.diffAttributes(newAttrs)
            node.diffStyles(newStyles)
        }
    }

    private fun HTMLElement.diffAttributes(newAttrs: Map<String, String?>) {
        // todo maybe set properties of the node differently?
        newAttrs.forEach { (key, value) ->
            asDynamic()[key] = value
        }
    }

    private fun HTMLElement.diffStyles(newStyles: Map<String, String>) {
        val toRemove = mutableSetOf<String>()
        for (prop in style.asList()) {
            if (prop !in newStyles) {
                toRemove.add(prop)
            }
        }

        toRemove.forEach {
            style.removeProperty(it)
        }

        newStyles.forEach { (key, value) ->
            style.setProperty(key, value)
        }
    }
}
