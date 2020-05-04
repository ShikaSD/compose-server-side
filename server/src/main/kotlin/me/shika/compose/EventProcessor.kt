package me.shika.compose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

class EventDispatcher : CoroutineScope {
    override val coroutineContext: CoroutineContext = ComposeThreadDispatcher

    private val nodeEventChannels = WeakHashMap<Long, Channel<EventPayload>>()

    fun dispatchEvent(event: EventPayload) {
        launch {
            nodeEventChannels[event.targetId]?.send(event)
        }
    }

    fun registerNode(node: HtmlNode) {
        if (nodeEventChannels.contains(node.id)) {
            throw IllegalStateException("Already registered")
        }
        val channel = Channel<EventPayload>()
        nodeEventChannels[node.id] = channel
        node.observe(this, channel)
    }

    fun removeNode(node: HtmlNode) {
        nodeEventChannels[node.id]?.cancel()
        nodeEventChannels.remove(node.id)
    }
}

class EventProcessor(val dispatcher: EventDispatcher) {
    fun process(event: JsonObject) {
        if (event["type"]?.primitive?.content == "click") {
            val id = event["id"]?.primitive?.long ?: return
            val event = EventPayload(targetId = id, payload = Event.Click)
            dispatcher.dispatchEvent(event)
        }
    }
}

data class EventPayload(val targetId: Long, val payload: Event)

sealed class Event(val type: String) {
    object Click : Event("click")
}
