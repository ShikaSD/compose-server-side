package me.shika

import androidx.compose.Composable
import io.ktor.application.*
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.timeout
import io.ktor.routing.Route
import io.ktor.util.AttributeKey
import io.ktor.websocket.WebSockets
import io.ktor.websocket.application
import io.ktor.websocket.webSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.stringify
import me.shika.compose.*
import java.time.Duration
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(60) // Disabled (null) by default
        timeout = Duration.ofSeconds(15)
    }
}

class Compose: CoroutineScope {
    private val job = SupervisorJob()
    private val eventDispatcherThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    override val coroutineContext: CoroutineContext = job + eventDispatcherThread

    fun shutdown() {
        job.cancel()
    }

    companion object : ApplicationFeature<Application, Nothing, Compose> {
        override val key: AttributeKey<Compose> = AttributeKey("compose-render")

        override fun install(pipeline: Application, configure: Nothing.() -> Unit): Compose {
            val feature = Compose()

            pipeline.environment.monitor.subscribe(ApplicationStopPreparing) {
                feature.shutdown()
            }

            with(pipeline) {
                install(WebSockets) {
                    pingPeriod = Duration.ofSeconds(60)
                    timeout = Duration.ofSeconds(15)
                }
            }


            return feature
        }
    }
}

@OptIn(ImplicitReflectionSerializer::class)
fun Route.compose(webSocketPath: String, body: @Composable() () -> Unit) {
    webSocket(webSocketPath) {
        val feature = application.feature(Compose)

        val json = Json(JsonConfiguration.Stable)

        val commandDispatcher = RenderCommandDispatcher(coroutineContext = feature.coroutineContext)
        val eventDispatcher = EventDispatcher()
        val eventProcessor = EventProcessor(eventDispatcher)

        feature.launch {
            commandDispatcher.consumeEach {
                outgoing.send(Frame.Text(json.stringify(it)))
            }
        }

        val root = HtmlNode.Tag(commandDispatcher, "<root>")
        root.eventDispatcher = eventDispatcher

        val composition = composition(root, commandDispatcher) {
            body()
        }

        incoming.consumeEach {
            when (it) {
                is Frame.Text -> {
                    val event = json.parse(ClientEvent.serializer(), it.readText())
                    eventProcessor.process(event)
                }
            }
        }

        composition.dispose()
    }
}
