package me.shika.compose.core

import androidx.compose.Composable

val composer: ServerComposer
    get() = throw IllegalStateException("Required for compiler")

@Composable
fun tag(
    tagName: String,
    modifier: Modifier = Modifier,
    children: @Composable() () -> Unit
) {
    HtmlNode.Tag(tag = tagName, modifier = modifier) {
        children()
    }
}

@Composable
fun text(value: String) {
    HtmlNode.Text(value = value)
}
