package me.shika.compose.core

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import me.shika.NodeDescription
import me.shika.compose.RenderCommandDispatcher
import me.shika.compose.event.Event
import me.shika.compose.event.EventDispatcher
import me.shika.compose.event.EventPayload
import me.shika.compose.values.Attribute
import me.shika.compose.values.Property
import me.shika.compose.values.Style
import java.util.concurrent.atomic.AtomicLong

sealed class HtmlNode {
    val id: Long = nextId.getAndIncrement()

    var parent: HtmlNode? = null

    data class Tag(
        private val commandDispatcher: RenderCommandDispatcher,
        val tag: String
    ) : HtmlNode() {
        lateinit var eventDispatcher: EventDispatcher

        // todo: replace with a method?
        var modifier: Modifier = Modifier
            set(value) {
                if (value == field) {
                    return
                }
                field = value

                val modifiers = value.toList()
                val events = modifiers.filterIsInstance<Event.Callback<*, *>>().associateBy {
                    it.descriptor
                }
                val attributes = modifiers.filterIsInstance<Attribute>().associateBy({ it.key }, { it.value })
                val styles = modifiers.filterIsInstance<Style>().associateBy({ it.property }, { it.value })
                val properties = modifiers.filterIsInstance<Property>().associateBy({ it.key }, { it.value })

                // todo: maybe diffing
                commandDispatcher.update(
                    node = this,
                    events = events.map { it.key.type },
                    attributes = attributes,
                    styles = styles,
                    properties = properties
                )

                this.styles = styles
                this.attributes = attributes
                this.events = events
            }

        internal var events: Map<Event, Event.Callback<*, *>> = emptyMap()
        internal var attributes: Map<String, String> = emptyMap()
        internal var styles: Map<String, String> = emptyMap()
        internal var properties: Map<String, String?> = emptyMap()

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
            "Tag(id=$id, tag=$tag, events=$events, attrs=$attributes, styles=$styles)"
    }

    class Text(private val commandDispatcher: RenderCommandDispatcher): HtmlNode() {
        var value: String = ""
            set(value) {
                field = value
                // todo: maybe pass description, text does not require all this info
                commandDispatcher.update(
                    this,
                    emptyList(),
                    mapOf("value" to value),
                    emptyMap(),
                    emptyMap()
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
                styles = styles,
                properties = properties,
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
