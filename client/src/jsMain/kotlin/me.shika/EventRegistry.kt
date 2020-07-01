package me.shika

import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node

class EventRegistry(default: EventHandler = EventHandler.Default) {
    private val eventHandlers = mutableListOf(default)

    fun addEventHandler(
        eventHandler: EventHandler
    ) {
        eventHandlers.add(eventHandler)
    }

    fun removeEventHandler(
        eventHandler: EventHandler
    ) {
        eventHandlers.remove(eventHandler)
    }

    fun handlers(): List<EventHandler> =
        eventHandlers.toList()
}

interface EventHandler {
    fun addEvent(
        name: String,
        node: Node,
        sendEvent: (value: Map<String, String>) -> Unit
    )

    object Default : EventHandler {
        override fun addEvent(name: String, node: Node, sendEvent: (value: Map<String, String>) -> Unit) {
            if (name == "click") {
                node.addEventListener(name, {
                    sendEvent(emptyMap())
                })
            } else if (name == "change" && node is HTMLInputElement) {
                node.addEventListener(name, {
                    sendEvent(mapOf("value" to node.value))
                })
            } else if (name == "input" && node is HTMLInputElement) {
                node.addEventListener(name, {
                    sendEvent(mapOf("value" to node.value))
                })
            }
        }
    }
}
