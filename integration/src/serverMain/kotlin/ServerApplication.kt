import androidx.compose.*
import androidx.compose.frames.modelListOf
import me.shika.compose.*
import me.shika.compose.core.Modifier
import me.shika.compose.core.text
import me.shika.compose.event.change
import me.shika.compose.event.keyup

val messages = modelListOf<String>()
private const val MESSAGE_LIMIT = 10

fun addMessage(from: String, message: String) {
    messages.add("Message from $from: $message")
    if (messages.size > MESSAGE_LIMIT) {
        messages.remove(messages.elementAt(0))
    }
}

@Composable
fun ComposeApp() {
    var name by state<String?> { null }

    div {
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

    messages.forEach {
        p {
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
                .change { message = it }
                .keyup { if (it == "Enter") send() }
        )
        button(text = "Send") { send() }
    }
}
