import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.shika.Compose
import me.shika.compose

private const val PORT_PROPERTY = "server.port"

fun main() {
    embeddedServer(
        Netty,
        port = System.getProperty(PORT_PROPERTY)?.toIntOrNull() ?: 8080,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(Compose)

    routing {
        static {
            resource("integration.js")
            resource("integration.js.map")
            resource("style.css")
        }

        accept(ContentType.Text.Html) {
            resource(remotePath = "/", resource = "index.html")
        }

        compose("/websocket") {
            DemoApp()
        }
    }
}
