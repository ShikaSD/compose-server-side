package me.shika.compose.core

import androidx.compose.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import me.shika.compose.RenderCommandDispatcher

private val CommandDispatcherAmbient = staticAmbientOf<RenderCommandDispatcher>()

@OptIn(ExperimentalComposeApi::class)
suspend fun composition(
    root: HtmlNode,
    commandDispatcher: RenderCommandDispatcher,
    composable: @Composable() () -> Unit
) = coroutineScope {
    withContext(composeThreadDispatcher) {
        val composition = compositionFor(
            root,
            Recomposer.current()
        ) { slots, recomposer ->
            val applyAdapter = ServerApplyAdapter(commandDispatcher, root)
            Composer(
                slots,
                applyAdapter,
                recomposer
            ).also {
                applyAdapter.observeChanges(it)
            }
        }

        composition.setContent {
            Providers(CommandDispatcherAmbient provides commandDispatcher) {
                composable()
            }
        }
        composition
    }
}

@Composable
fun tag(
    tagName: String,
    modifier: Modifier = Modifier,
    children: @Composable() () -> Unit
) {
    val renderCommandDispatcher = CommandDispatcherAmbient.current
    emit<HtmlNode.Tag, ServerApplyAdapter>(
        ctor = {  HtmlNode.Tag(renderCommandDispatcher, tagName) },
        update = {
            set(modifier) { modifier -> this.modifier = modifier }
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
