import me.shika.ComposeContent
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.browser.window

lateinit var content: ComposeContent

fun main() {
    val root = document.querySelector(".content") as HTMLElement
    val protocol = if (window.location.protocol == "https:") "wss" else "ws"
    content = ComposeContent(root, "$protocol://${window.location.host}/websocket")
}
