package me.shika.compose.event

import androidx.compose.composeThreadDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import me.shika.ClientEvent
import me.shika.compose.core.HtmlNode
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
        if (node is HtmlNode.Tag) {
            node.observe(this, channel)
        }
    }

    fun removeNode(node: HtmlNode) {
        nodeEventChannels[node.id]?.cancel()
        nodeEventChannels.remove(node.id)
    }
}

interface EventProcessor {
    fun process(event: ClientEvent): Event.Payload<*>?

    companion object Default : EventProcessor {
        override fun process(event: ClientEvent): Event.Payload<*>? =
            when (event.name) {
                Click.type -> Click.Payload
                Change.type -> Change.Payload(event.values["value"]!!)
                Input.type -> Input.Payload(event.values["value"]!!)
                KeyUp.type -> KeyUp.Payload(event.values["value"]!!)
                MouseEnter.type -> MouseEnter.Payload
                MouseLeave.type -> MouseLeave.Payload
                else -> null
            }
    }
}

class EventDistributor(
    private val dispatcher: EventDispatcher,
    private val processor: EventProcessor = EventProcessor
) {
    fun evaluate(event: ClientEvent) {
        val payload = processor.process(event)

        if (payload != null) {
            dispatcher.dispatchEvent(
                EventPayload(
                    targetId = event.targetId,
                    payload = payload
                )
            )
        }
    }
}
