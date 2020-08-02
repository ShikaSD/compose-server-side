package compose.screens

import androidx.compose.*
import compose.Theme
import compose.components.InputState
import me.shika.compose.*
import me.shika.compose.core.Modifier
import me.shika.compose.core.text
import me.shika.compose.event.hover
import me.shika.compose.event.onClick
import me.shika.compose.event.onInput
import me.shika.compose.event.onKeyUp
import me.shika.compose.values.*

@Composable
fun ChatScreen() {
    var name by state<String?> { null }
    val isLoggedIn = name != null

    if (!isLoggedIn) {
        NameInput { name = it }
    } else {
        MessageList(name!!)
    }
}

@Composable
private fun NameInput(onNameEntered: (String) -> Unit) {
    h1 { text("Welcome to (quite limited) chat room") }
    p(modifier = Modifier.style("line-height", "2")) {
        text("Try to type some messages and check if you see them. Note, that they are not persisted, so server restart will wipe them out.")
        br()
        text("If this room is empty (happens because Heroku), you can open another window and send messages from there.")
    }
    br()
    h1 { text("Let's start") }
    p { text("To enter chat room you need to log in first:") }

    div {
        p(
            modifier = Modifier
                .style("font-weight", "700")
                .style("display", "inline-block")
                .style("margin-right", "10px")
        ) {
            text("Your name:")
        }

        Input(
            inputModifier = Modifier
                .attribute("placeholder", "enter here")
                .style("padding", "5px 10px")
                .style("margin", "5px")
                .style("border", "1px solid ${Theme.Ambient.current.highlight}")
                .style("border-radius", "8px"),
            buttonModifier = Modifier
                .style("padding", "5px 10px"),
            buttonText = "Go",
            onSubmit = onNameEntered
        )
    }
}

@Composable
private fun MessageList(name: String) {
    Column(Modifier.style("flex", "1")) {
        h1 {
            text("The chat room")
        }
        p {
            text("This room support maximum of $MESSAGE_LIMIT messages, and now ${messages.size} is already here. Old messages will be removed when you send new ones :)")
        }
        Column(modifier = Modifier
            .grow(1)
            .style("margin-bottom", "10px")
        ) {
            messages.forEachIndexed { i, message ->
                MessageItem(i, message)
            }
        }

        p(Modifier.textSize(Theme.FontSize.SMALL).margin("5px")) {
            text("Your message as $name:")
        }
        Row {
            Input(
                inputModifier = Modifier
                    .attribute("placeholder", "your message")
                    .grow(1)
                    .style("padding", "10px 15px")
                    .style("margin-right", "15px")
                    .style("border", "1px solid ${Theme.Ambient.current.highlight}")
                    .style("border-radius", "8px"),
                buttonModifier = Modifier
                    .style("padding", "0 10px"),
                buttonText = "Send"
            ) {
                addMessage(Message(name, it))
            }
        }
    }
}

@Composable
private fun MessageItem(index: Int, message: Message) {
    val theme = Theme.Ambient.current

    div(
        modifier = Modifier
            .style("font-size", Theme.FontSize.SMALL)
            .style("padding", "10px 5px")
            .background(if (index % 2 == 0) theme.highlight else theme.background)
            .run {
                if (index == 0) style("margin-top", "auto") else this
            }
    ) {
        p(
            modifier = Modifier
                .style("font-size", Theme.FontSize.MEDIUM)
                .style("font-weight", "700")
                .style("margin", "0")
        ) {
            text(message.from)
        }
        text(message.text)
    }
}

@Composable
private fun Input(
    inputModifier: Modifier,
    buttonModifier: Modifier = Modifier,
    buttonText: String,
    onSubmit: (message: String) -> Unit
) {
    val text = remember { InputState("") }
    val theme = Theme.Ambient.current

    fun send() {
        if (text.value.isNotBlank()) {
            onSubmit(text.value)
            text.reset()
        }
    }

    input(
        type = "text",
        value = text.compositionValue(),
        modifier = inputModifier
            .textColor(theme.foreground)
            .onInput { text.value = it }
            .onKeyUp { if (it == "Enter") send() }
    )

    InputButton(
        modifier = buttonModifier,
        text = buttonText
    ) {
        send()
    }
}

@Composable
private fun InputButton(modifier: Modifier, text: String, onClick: () -> Unit) {
    val theme = Theme.Ambient.current
    button(
        modifier = modifier
            .onClick(onClick)
            .hover {
                background(if (it) theme.accentHighlight else theme.accent)
            }
            .style("border-radius", "8px")
            .textColor(theme.white)
    ) {
        text(text)
    }
}

private val messages = mutableStateListOf<Message>()
private const val MESSAGE_LIMIT = 10

private data class Message(
    val from: String,
    val text: String
)

private fun addMessage(message: Message) {
    messages.add(message)
    if (messages.size > MESSAGE_LIMIT) {
        messages.remove(messages.elementAt(0))
    }
}
