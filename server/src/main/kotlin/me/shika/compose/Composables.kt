package me.shika.compose

import androidx.compose.Composable
import androidx.compose.Recomposer
import androidx.compose.composeThreadDispatcher
import androidx.compose.compositionFor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.shika.compose.attributes.attribute
import me.shika.compose.core.HtmlNode
import me.shika.compose.core.Modifier
import me.shika.compose.core.ServerComposer
import me.shika.compose.core.tag
import me.shika.compose.event.onClick

suspend fun composition(
    root: HtmlNode,
    commandDispatcher: RenderCommandDispatcher,
    composable: @Composable() () -> Unit
) = coroutineScope {
    withContext(composeThreadDispatcher) {
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
fun div(modifier: Modifier = Modifier, children: @Composable() () -> Unit = {}) {
    tag(tagName = "div",  modifier = modifier, children = children)
}

@Composable
fun h1(modifier: Modifier = Modifier, children: @Composable() () -> Unit = {}) {
    tag(tagName = "h1", modifier = modifier, children = children)
}

@Composable
fun p(modifier: Modifier = Modifier, children: @Composable() () -> Unit) {
    tag(tagName = "p", modifier = modifier, children = children)
}

@Composable
fun button(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    tag(tagName = "button", modifier = modifier.onClick(onClick)) {
        me.shika.compose.core.text(text)
    }
}

@Composable
fun checkbox(isChecked: Boolean, modifier: Modifier = Modifier) {
    tag(
        tagName = "input",
        modifier = modifier
            .attribute("type", "checkbox")
            .attribute("value", "$isChecked")
    ) { }
}

@Composable
fun input(
    type: String,
    value: String,
    modifier: Modifier
) {
    tag(
        tagName = "input",
        modifier = modifier
            .attribute("type", type)
            .attribute("value", value),
        children = { }
    )

}

