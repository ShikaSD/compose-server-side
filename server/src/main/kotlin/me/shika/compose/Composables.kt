package me.shika.compose

import androidx.compose.*
import kotlinx.coroutines.*

val ComposeThreadDispatcher = composeThreadExecutor.asCoroutineDispatcher()
val composer: ServerComposer
    get() = throw IllegalStateException("Required for compiler")

suspend fun composition(
    root: HtmlNode,
    commandDispatcher: RenderCommandDispatcher,
    composable: @Composable() () -> Unit
) = coroutineScope {
        withContext(ComposeThreadDispatcher) {
            val composition = compositionFor(
                root,
                Recomposer.current(),
                composerFactory = { slotTable, recomposer ->
                    ServerComposer(root, slotTable, recomposer = recomposer, commandDispatcher = commandDispatcher)
                }
            )

            composition.setContent(composable)
            composition
        }
    }

@Composable
fun h1(className: String? = null, children: @Composable() () -> Unit = {}) {
    tag(tagName = "h1", className = className, children = children)
}

@Composable
fun h2(className: String? = null, children: @Composable() () -> Unit = {}) {
    tag(tagName = "h2", className = className, children = children)
}


@Composable
fun button(className: String? = null, text: String, onClick: () -> Unit) {
    tag(tagName = "button", className = className, onClick = onClick) {
        text(text)
    }
}

@Composable
fun tag(
    tagName: String,
    className: String? = null,
    onClick: (() -> Unit)? = null,
    children: @Composable() () -> Unit
) {
    val events = if (onClick != null) {
        mapOf<Event, () -> Unit>(Event.Click to onClick)
    } else {
        emptyMap()
    }
    HtmlNode.Tag(tag = tagName, attributes = mapOf("className" to className), events = events) {
        children()
    }
}

@Composable
fun text(value: String) {
    HtmlNode.Text(value = value)
}
