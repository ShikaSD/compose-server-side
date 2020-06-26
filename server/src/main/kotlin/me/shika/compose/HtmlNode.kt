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
    var parent: HtmlNode? = null

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
    }

    class Text(private val commandDispatcher: RenderCommandDispatcher): HtmlNode() {
        var value: String = ""
            set(value) {
                field = value
                println("update $this")
                commandDispatcher.update(
                    this,
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
