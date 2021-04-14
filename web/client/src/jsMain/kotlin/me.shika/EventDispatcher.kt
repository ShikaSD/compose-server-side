package me.shika

import org.w3c.dom.Node

class EventDispatcher(
    private val eventRegistry: EventRegistry,
    private val dispatchEvent: (ClientEvent) -> Unit
) {
    fun registerEvent(
        nodeId: Long,
        node: Node,
        name: String
    ) {
        eventRegistry.addEvent(
            name,
            node
        ) { value -> dispatchEvent(nodeId, name, value) }
    }

    fun updateEvents(nodeId: Long, node: Node, events: List<String>) {
        val existing = eventRegistry.listeners(node)
        val toRemove = existing.filter { it.name !in events }
        val toAdd = events.filter { newEvent -> existing.none { it.name == newEvent } }
        toRemove.forEach { eventRegistry.removeEvent(it, node) }
        toAdd.forEach { name ->
            eventRegistry.addEvent(name, node) { dispatchEvent(nodeId, name, it) }
        }
    }

    fun clearEvents(node: Node) {
        eventRegistry.listeners(node).forEach {
            eventRegistry.removeEvent(it, node)
        }
    }

    private fun dispatchEvent(nodeId: Long, name: String, values: Map<String, String>) {
        val data = ClientEvent(nodeId, name, values)
        dispatchEvent(data)
    }
}
