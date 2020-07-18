package me.shika.compose.core

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import me.shika.NodeDescription
import me.shika.compose.RenderCommandDispatcher
import me.shika.compose.attributes.Attribute
import me.shika.compose.event.Event
import me.shika.compose.event.EventDispatcher
import me.shika.compose.event.EventPayload
import java.util.concurrent.atomic.AtomicLong

sealed class HtmlNode {
    val id: Long = nextId.getAndIncrement()

    var parent: HtmlNode? = null

    data class Tag(
        private val commandDispatcher: RenderCommandDispatcher,
        val tag: String
    ) : HtmlNode() {
        lateinit var eventDispatcher: EventDispatcher

        var modifier: Modifier = Modifier
            set(value) {
                field = value
                val modifiers = value.toList()
                val events = modifiers.filterIsInstance<Event.Callback<*, *>>().associateBy {
                    it.descriptor
                }
                val attributes = modifiers.filterIsInstance<Attribute>().associateBy({ it.key }, { it.value })

                commandDispatcher.update(
                    this,
                    events.map { it.key.type },
                    attributes
                )

                this.attributes = attributes
                this.events = events
            }

        // todo: combine dispatch of events and attributes
        internal var events: Map<Event, Event.Callback<*, *>> = emptyMap()
            set(value) {
                field = value
            }

        internal var attributes: Map<String, String?> = emptyMap()
            set(value) {
                field = value
            }

        private val children: MutableList<HtmlNode> = mutableListOf()

        fun insertAt(index: Int, instance: HtmlNode) {
            children.add(index, instance)
            instance.parent = this

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
                instance.parent = null

                eventDispatcher.removeNode(instance)
            }
        }

        fun observe(eventDispatcher: EventDispatcher, channel: Channel<EventPayload<*>>) {
            this.eventDispatcher = eventDispatcher
            eventDispatcher.launch {
                channel.consumeEach {
                    val receive = events[it.payload.descriptor]?.onReceive as? (Any) -> Unit
                    receive?.invoke(it.payload) ?: error("Callback for event ${it.payload.descriptor} not found")
                }
            }
        }

        override fun toString(): String =
            "Tag(id=$id, tag=$tag, events=$events, attrs=$attributes)"
    }

    class Text(private val commandDispatcher: RenderCommandDispatcher): HtmlNode() {
        var value: String = ""
            set(value) {
                field = value
                commandDispatcher.update(
                    this,
                    emptyList(),
                    mapOf("value" to value)
                )
            }

        override fun toString(): String =
            "Text(id=$id, value=$value)"
    }

    fun toDescription(): NodeDescription =
        when (this) {
            is Tag -> NodeDescription.Tag(
                id = id,
                tag = tag,
                attributes = attributes,
                events = events.keys.map { it.type }
            )
            is Text -> NodeDescription.Text(
                id = id,
                value = value
            )
        }

    companion object {
        private val nextId = AtomicLong(0L)
    }
}
