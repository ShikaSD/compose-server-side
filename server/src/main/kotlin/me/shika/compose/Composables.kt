@file:OptIn(ExperimentalComposeApi::class)

package me.shika.compose

import androidx.compose.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

private val CommandDispatcherAmbient = staticAmbientOf<RenderCommandDispatcher>()

suspend fun composition(
    root: HtmlNode,
    commandDispatcher: RenderCommandDispatcher,
    composable: @Composable() () -> Unit
) = coroutineScope {
    withContext(composeThreadDispatcher) {
        val composition = compositionFor(
            root.id,
            ServerApplyAdapter(commandDispatcher, root),
            Recomposer.current()
        )

        composition.setContent {
            Providers(
                CommandDispatcherAmbient provides commandDispatcher
            ) {
                composable()
            }
        }
        composition
    }
}

@Composable
fun div(className: String? = null, children: @Composable() () -> Unit = {}) {
    tag(tagName = "div", className = className, children = children)
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
fun p(children: @Composable() () -> Unit) {
    tag(tagName = "p", attributes = emptyMap(), events = emptyMap(), children = children)
}

@Composable
fun button(className: String? = null, text: String, onClick: () -> Unit) {
    tag(tagName = "button", className = className, onClick = { onClick() }) {
        text(text)
    }
}

@Composable
fun input(
    type: String,
    onChange: ((String) -> Unit)? = null,
    onInput: ((String) -> Unit)? = null
) {
    tag(
        tagName = "input",
        attributes = mapOf("type" to type),
        events =
            listOfNotNull(
                onChange?.let { Change.Callback { it(it.value) } },
                onInput?.let { Input.Callback { it(it.value) } }
            ).associateBy { it.descriptor } as EventMap,
        children = { }
    )
}

@Composable
fun tag(
    tagName: String,
    className: String? = null,
    onClick: (() -> Unit)? = null,
    children: @Composable() () -> Unit
) {

    val attributes =
        if (className != null) {
            mapOf("className" to className)
        } else {
            emptyMap()
        }

    val events: EventMap =
        if (onClick != null) {
            mapOf(Click to Click.Callback { onClick() }) as EventMap
        } else {
            emptyMap()
        }

    tag(
        tagName,
        attributes,
        events,
        children
    )
}

@Composable
fun tag(
    tagName: String,
    attributes: Map<String, String>,
    events: EventMap,
    children: @Composable() () -> Unit
) {
    val renderCommandDispatcher = CommandDispatcherAmbient.current
    val attributes = attributes
    val events = events
    emit<HtmlNode.Tag, ServerApplyAdapter>(
        ctor = {  HtmlNode.Tag(renderCommandDispatcher, tagName, events) },
        update = {
            set(attributes) { attrs -> this.attributes = attrs }
        },
        children = children
    )
}

@Composable
fun text(value: String) {
    val renderCommandDispatcher = CommandDispatcherAmbient.current
    val value = value
    emit<HtmlNode.Text, ServerApplyAdapter>(
        ctor = {  HtmlNode.Text(renderCommandDispatcher) },
        update = {
            set(value) { value -> this.value = value }
        }
    )
}
