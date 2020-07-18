import androidx.compose.*
import androidx.compose.frames.modelListOf
import me.shika.compose.*
import me.shika.compose.attributes.id
import me.shika.compose.attributes.labelFor
import me.shika.compose.core.Modifier
import me.shika.compose.core.text
import me.shika.compose.event.onChange
import me.shika.compose.event.onKeyUp
import me.shika.compose.styles.background
import me.shika.compose.styles.style
import me.shika.compose.styles.textColor

val messages = modelListOf<String>()
private const val MESSAGE_LIMIT = 10

fun addMessage(from: String, message: String) {
    messages.add("Message from $from: $message")
    if (messages.size > MESSAGE_LIMIT) {
        messages.remove(messages.elementAt(0))
    }
}

private fun Modifier.fullSize(): Modifier =
    style("overflow", "hidden")
        .style("height", "100%")
        .style("width", "100%")

private fun Modifier.topRight(): Modifier =
    style("position", "absolute")
        .style("right", "0")

@Composable
fun ComposeApp() {
    var name by state<String?> { null }
    var darkTheme by state { false }

    div(
        modifier = Modifier
            .textColor(if (darkTheme) "white" else "black")
            .background(if (darkTheme) "black" else "white")
            .fullSize()
    ) {
        div(Modifier.topRight()) {
            val id = "is-dark"
            checkbox(
                isChecked = darkTheme,
                modifier = Modifier
                    .id(id)
                    .onChange { darkTheme = !darkTheme }
            )
            label("Dark theme", modifier = Modifier.labelFor(id))
        }

        if (name == null) {
            h1 {
                text("Hi, please enter your name")
            }

            Input(onSent = { name = it })
        } else {
            val nameNotNull = name!!
            MessageList(name = nameNotNull)

            Input(onSent = {
                addMessage(from = nameNotNull, message = it)
            })

        }
    }
}

@Composable
fun MessageList(name: String) {
    h1 {
        text("Hi, $name, this chat has ${messages.size} messages out of max $MESSAGE_LIMIT")
    }

    messages.forEachIndexed { i, it ->
        p(Modifier.textColor(if (i % 2 == 0) "red" else "blue")) {
            text(it)
        }
    }
}

@Composable
fun Input(onSent: (message: String) -> Unit) {
    var message by mutableStateOf("")

    fun send() {
        onSent(message)
        message = ""
    }

    div {
        input(
            type = "text",
            value = message,
            modifier = Modifier
                .onChange { message = it }
                .onKeyUp { if (it == "Enter") send() }
        )
        button(text = "Send") { send() }
    }
}
