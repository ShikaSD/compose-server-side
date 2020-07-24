package compose.screens

import androidx.compose.*
import me.shika.compose.*
import me.shika.compose.core.Modifier
import me.shika.compose.core.text
import me.shika.compose.event.onChange
import me.shika.compose.event.onClick
import me.shika.compose.event.onKeyUp
import me.shika.compose.values.textColor

val messages = mutableStateListOf<String>()
private const val MESSAGE_LIMIT = 10

fun addMessage(from: String, message: String) {
    messages.add("Message from $from: $message")
    if (messages.size > MESSAGE_LIMIT) {
        messages.remove(messages.elementAt(0))
    }
}

@Composable
fun ChatScreen() {
    var name by state<String?> { null }
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
        button(Modifier.onClick { send() }) {
            text("Send")
        }
    }
}
