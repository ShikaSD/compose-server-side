package me.shika.compose

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

@OptIn(ImplicitReflectionSerializer::class, UnstableDefault::class)
class RenderCommandDispatcher(
    override val coroutineContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher(),
    private val channel: Channel<JsonElement> = Channel()
): CoroutineScope, ReceiveChannel<JsonElement> by channel {
    fun insert(parent: HtmlNode, index: Int, node: HtmlNode) {
        launch {
            sendJson(
                json {
                    "type" to "insert"
                    "parent" to parent.id
                    "index" to index
                    "node" to json {
                        when (node) {
                            is HtmlNode.Tag -> {
                                "type" to "tag"
                                "id" to node.id
                                "tag" to node.tag
                                "attributes" to Json.toJson(
                                    MapSerializer(String.serializer(), String.serializer()),
                                    node.attributes.filterValues { it != null } as Map<String, String>
                                )
                                "events" to Json.toJson(
                                    ListSerializer(String.serializer()),
                                    node.events.keys.map { it.type }
                                )
                            }
                            is HtmlNode.Text -> {
                                "type" to "text"
                                "value" to node.value
                            }
                        }
                    }
                }
            )
        }
    }

    fun remove(parent: HtmlNode, index: Int, count: Int) {
        launch {
            sendJson(
                json {
                    "type" to "remove"
                    "parent" to parent.id
                    "index" to index
                    "count" to count
                }
            )
        }
    }

    fun move(parent: HtmlNode, from: Int, to: Int, count: Int) {
        launch {
            sendJson(
                json {
                    "type" to "move"
                    "parent" to parent.id
                    "from" to from
                    "to" to to
                    "count" to count
                }
            )
        }
    }

    private suspend fun sendJson(jsonObject: JsonObject) {
        channel.send(jsonObject)
    }

    fun dispose() {
        channel.close()
    }
}
