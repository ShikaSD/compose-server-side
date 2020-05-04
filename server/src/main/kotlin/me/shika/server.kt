package me.shika

import androidx.compose.mutableStateOf
import androidx.compose.state
import me.shika.compose.*
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.ContentType
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.timeout
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.routing.accept
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.stringify
import java.time.Duration

@OptIn(ImplicitReflectionSerializer::class)
fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(60) // Disabled (null) by default
        timeout = Duration.ofSeconds(15)
    }

    routing {
        static {
            resource("client.js")
            resource("client.js.map")
        }

        accept(ContentType.Text.Html) {
            resource(remotePath = "/", resource = "index.html")
        }

        webSocket("/websocket") {
            val json = Json(JsonConfiguration.Stable)

            val commandDispatcher = RenderCommandDispatcher()
            val eventDispatcher = EventDispatcher()
            val eventProcessor = EventProcessor(eventDispatcher)

            launch {
                commandDispatcher.consumeEach {
                    outgoing.send(Frame.Text(json.stringify(it)))
                }
            }

            val root = HtmlNode.Tag("body")
            root.eventDispatcher = eventDispatcher

            val composition = composition(root, commandDispatcher) {
                h1(className = "Test") {
                    text("Hello")
                }
                val state = state { 1 }
                h2 {
                    text("Counter ${state.value}")
                }
                button(text = "Increment!") {
                    state.value = state.value + 1
                }
            }

            incoming.consumeEach {
                when (it) {
                    is Frame.Text -> {
                        val obj = json.parseJson(it.readText())
                        if (obj.jsonObject["type"]?.primitive?.content == "event") {
                            eventProcessor.process(obj.jsonObject["payload"]!!.jsonObject)
                        }
                    }
                }
            }

            composition.dispose()
        }
    }
}

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        module = Application::module
    ).start(wait = true)
}
