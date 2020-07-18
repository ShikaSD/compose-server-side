import kotlinx.browser.document
import kotlinx.browser.window
import me.shika.ComposeContent
import org.w3c.dom.HTMLElement

lateinit var content: ComposeContent

fun main() {
    val root = document.querySelector(".content") as HTMLElement
    val protocol = if (window.location.protocol == "https:") "wss" else "ws"
    content = ComposeContent(root, "$protocol://${window.location.host}/websocket")
}
