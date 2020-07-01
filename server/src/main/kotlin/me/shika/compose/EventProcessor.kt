package me.shika.compose

import androidx.compose.composeThreadDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import me.shika.ClientEvent
import java.util.*
import kotlin.coroutines.CoroutineContext

class EventDispatcher : CoroutineScope {
    override val coroutineContext: CoroutineContext = composeThreadDispatcher

    private val nodeEventChannels = WeakHashMap<Long, Channel<EventPayload<*>>>()

    fun dispatchEvent(event: EventPayload<*>) {
        launch {
            nodeEventChannels[event.targetId]?.send(event)
        }
    }

    fun registerNode(node: HtmlNode) {
        if (nodeEventChannels.contains(node.id)) {
            throw IllegalStateException("Already registered")
        }
        val channel = Channel<EventPayload<*>>()
        nodeEventChannels[node.id] = channel
        node.observe(this, channel)
    }

    fun removeNode(node: HtmlNode) {
        nodeEventChannels[node.id]?.cancel()
        nodeEventChannels.remove(node.id)
    }
}

class EventProcessor(private val dispatcher: EventDispatcher) {
    fun process(event: ClientEvent) {
        if (event.name == Click.type) {
            val event = EventPayload(targetId = event.targetId, payload = Click.Payload)
            dispatcher.dispatchEvent(event)
        } else if (event.name == InputChange.type) {
            val event = EventPayload(
                targetId = event.targetId,
                payload = InputChange.Payload(event.values["value"]!!)
            )
            dispatcher.dispatchEvent(event)
        }
    }
}
