package compose.screens

import androidx.compose.Composable
import androidx.compose.mutableStateListOf
import androidx.compose.remember
import compose.Theme
import compose.components.InputState
import me.shika.compose.*
import me.shika.compose.core.Modifier
import me.shika.compose.core.text
import me.shika.compose.event.onChange
import me.shika.compose.event.onClick
import me.shika.compose.event.onInput
import me.shika.compose.event.onKeyUp
import me.shika.compose.values.attribute
import me.shika.compose.values.lineHeight
import me.shika.compose.values.style
import me.shika.compose.values.textColor

data class Todo(val text: String, val isDone: Boolean)

@Composable
fun TodoList() {
    h1 { text("TodoList") }
    p(modifier = Modifier.lineHeight(1.5f)) {
        text("Classic example interacting with a list of items.")
        br()
        text("Use input below to add more items to a list (submit using Enter)")
    }

    val todos = remember {
        mutableStateListOf(
            Todo("Wash some dishes", isDone = true),
            Todo("Finally inject heater into that thermosiphon", isDone = false),
            Todo("Cleanup the rest of the code", isDone = false)
        )
    }

    Column {
        TodoInput {
            todos += Todo(it, isDone = false)
        }

        todos.forEachIndexed { i, todo ->
            TodoItem(
                todo,
                onCompleted = { todos[i] = todo.copy(isDone = !todo.isDone) },
                onRemoved = { todos.removeAt(i) }
            )
        }
    }
}

@Composable
fun TodoItem(model: Todo, onCompleted: () -> Unit, onRemoved: () -> Unit) {
    Row(Modifier
        .style("padding", "10px 15px")
        .style("border-bottom", "1px solid ${Theme.Ambient.current.highlight}")
    ) {
        checkbox(
            isChecked = model.isDone,
            modifier = Modifier
                .onChange { onCompleted() }
                .style("margin-right", "15px")
        )
        label(
            text = model.text,
            modifier = Modifier
                .grow(1)
                .run {
                    if (model.isDone) {
                        style("opacity", "0.6")
                            .style("text-decoration", "line-through")
                    } else {
                        this
                    }
                }
        )
        a(modifier = Modifier.onClick { onRemoved() }) { text("remove") }
    }
}

@Composable
fun TodoInput(onSubmit: (String) -> Unit) {
    val inputValue = remember { InputState("") }
    input(
        type = "text",
        value = inputValue.value,
        modifier = Modifier
            .attribute("placeholder", "what needs to be done?")
            .style("border-radius", "8px")
            .style("padding", "10px")
            .style("border", "1px solid ${Theme.Ambient.current.highlight}")
            .style("margin-bottom", "15px")
            .textColor(Theme.Ambient.current.foreground)
            .onInput { inputValue.value = it }
            .onKeyUp {
                if (it == "Enter") {
                    inputValue.submit(onSubmit)
                }
            }
    )
}
