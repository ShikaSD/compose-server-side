package me.shika

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.w3c.dom.HTMLElement
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket

class ComposeContent(
    rootElement: HTMLElement,
    socketPath: String,
    eventRegistry: EventRegistry = EventRegistry()
) {
    private val socket = WebSocket(socketPath)
    private val json = Json(JsonConfiguration.Stable)

    private val eventDispatcher = EventDispatcher(eventRegistry, ::dispatchEvent)
    private val updateHandler = UpdateHandler(rootElement, eventDispatcher)

    init {
        socket.onmessage = ::handleMessage
    }

    private fun handleMessage(event: MessageEvent) {
        val data = event.data as String
        println(data)

        val command = json.parse(RenderCommand.serializer(), data)
        updateHandler.handleCommand(command)
    }

    private fun dispatchEvent(clientEvent: ClientEvent) {
        val value = json.stringify(ClientEvent.serializer(), clientEvent)
        console.log(value)
        socket.send(value)
    }
}

