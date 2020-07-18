package me.shika

import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent

class EventRegistry(private val handler: EventHandler = EventHandler.Default) {
    private val listeners = mutableMapOf<Node, MutableList<Event>>()

    fun addEvent(name: String, node: Node, callback: (value: Map<String, String>) -> Unit) {
        val listener = handler.addEvent(name, node, callback)
        if (listener != null) {
            val list = listeners.getOrPut(node) { mutableListOf() }
            list.add(Event(name, listener))
        }
    }

    fun removeEvent(event: Event, node: Node) {
        handler.removeEvent(event.name, node, event.listener)
    }

    fun listeners(node: Node): List<Event> = listeners[node] ?: emptyList()

    class Event(
        val name: String,
        val listener: Any
    )
}

interface EventHandler {
    fun addEvent(
        name: String,
        node: Node,
        sendEvent: (value: Map<String, String>) -> Unit
    ): Any?

    fun removeEvent(
        name: String,
        node: Node,
        listener: Any
    )

    object Default : EventHandler {
        override fun addEvent(name: String, node: Node, sendEvent: (value: Map<String, String>) -> Unit): Any? {
            val listener: EventListener? = when (name) {
                "click" -> {
                    EventListener {
                        sendEvent(emptyMap())
                    }
                }
                "change" -> {
                    if (node is HTMLInputElement) {
                        EventListener {
                            sendEvent(mapOf("value" to node.value))
                        }
                    } else {
                        null
                    }
                }
                "input" -> {
                    if (node is HTMLInputElement) {
                        EventListener {
                            sendEvent(mapOf("value" to node.value))
                        }
                    } else {
                        null
                    }
                }
                "keyup" -> {
                    EventListener {
                        it as KeyboardEvent
                        sendEvent(mapOf("value" to it.key))
                    }
                }
                else -> null
            }
            node.addEventListener(name, listener)
            return listener
        }

        override fun removeEvent(name: String, node: Node, listener: Any) {
            node.removeEventListener(name, listener as EventListener)
        }
    }
}
