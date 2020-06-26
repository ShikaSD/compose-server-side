package me.shika.compose

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import me.shika.NodeDescription
import java.util.concurrent.atomic.AtomicLong

sealed class HtmlNode {
    val id: Long = nextId.getAndIncrement()
    open val events: Map<Event, () -> Unit> = emptyMap()

    lateinit var eventDispatcher: EventDispatcher

    fun observe(eventDispatcher: EventDispatcher, channel: Channel<EventPayload>) {
        this.eventDispatcher = eventDispatcher
        eventDispatcher.launch {
            channel.consumeEach {
                events[it.payload]?.invoke()
            }
        }
    }

    data class Tag(
        val tag: String,
        val attributes: Map<String, String?> = emptyMap(),
        override val events: Map<Event, () -> Unit> = emptyMap()
    ) : HtmlNode() {
        private val children: MutableList<HtmlNode> = mutableListOf()

        fun insertAt(index: Int, instance: HtmlNode) {
            children.add(index, instance)

            eventDispatcher.registerNode(instance)
        }

        fun move(from: Int, to: Int, count: Int) {
            if (from > to) {
                var current = to
                repeat(count) {
                    val node = children[from]
                    children.removeAt(from)
                    children.add(current, node)
                    current++
                }
            } else {
                repeat(count) {
                    val node = children[from]
                    children.removeAt(from)
                    children.add(to - 1, node)
                }
            }
        }

        fun remove(index: Int, count: Int) {
            repeat(count) {
                val instance = children.removeAt(index)
                eventDispatcher.removeNode(instance)
            }
        }
    }

    data class Text(val value: String): HtmlNode()

    fun toDescription(): NodeDescription =
        when (this) {
            is Tag -> NodeDescription.Tag(
                id,
                tag,
                attributes,
                events.keys.map { it.type }
            )
            is Text -> NodeDescription.Text(
                value
            )
        }

    companion object {
        private val nextId = AtomicLong(0L)
    }
}
