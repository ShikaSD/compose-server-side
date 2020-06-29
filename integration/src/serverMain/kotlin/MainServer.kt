import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.ContentType
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.routing.accept
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import me.shika.Compose
import me.shika.compose

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(Compose)

    routing {
        static {
            resource("integration.js")
            resource("integration.js.map")
        }

        accept(ContentType.Text.Html) {
            resource(remotePath = "/", resource = "index.html")
        }

        compose("/websocket") {
            ComposeApp()
        }
    }
}
