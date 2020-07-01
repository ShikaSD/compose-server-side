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
        eventRegistry.handlers().forEach {
            it.addEvent(
                name,
                node
            ) { value -> dispatchEvent(nodeId, name, value) }
        }
    }

    private fun dispatchEvent(nodeId: Long, name: String, values: Map<String, String>) {
        val data = ClientEvent(nodeId, name, values)
        dispatchEvent(data)
    }
}
