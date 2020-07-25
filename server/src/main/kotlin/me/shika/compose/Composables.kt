package me.shika.compose

import androidx.compose.Composable
import me.shika.compose.core.Modifier
import me.shika.compose.core.tag
import me.shika.compose.values.attribute
import me.shika.compose.values.value

@Composable
fun div(modifier: Modifier = Modifier, children: @Composable() () -> Unit = {}) {
    tag(tagName = "div",  modifier = modifier, children = children)
}

@Composable
fun a(href: String = "#", modifier: Modifier = Modifier, children: @Composable() () -> Unit) {
    tag(tagName = "a", modifier.attribute("href", href), children)
}

@Composable
fun p(modifier: Modifier = Modifier, children: @Composable() () -> Unit) {
    tag(tagName = "p", modifier = modifier, children = children)
}

@Composable
fun button(modifier: Modifier = Modifier, children: @Composable() () -> Unit) {
    tag(tagName = "button", modifier = modifier) {
        children()
    }
}

@Composable
fun h1(modifier: Modifier = Modifier, children: @Composable() () -> Unit = {}) {
    tag(tagName = "h1", modifier = modifier, children = children)
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
            .value(value),
        children = { }
    )
}

@Composable
fun textarea(
    modifier: Modifier
) {
    tag(
        tagName = "textarea",
        modifier = modifier,
    ) { }
}

@Composable
fun checkbox(isChecked: Boolean, modifier: Modifier = Modifier) {
    input(
        type = "checkbox",
        value = "$isChecked",
        modifier = modifier
    )
}

@Composable
fun label(text: String, modifier: Modifier = Modifier) {
    tag("label", modifier) {
        me.shika.compose.core.text(text)
    }
}

@Composable
fun nav(modifier: Modifier, children: @Composable() () -> Unit) {
    tag("nav", modifier) {
        children()
    }
}

@Composable
fun br(modifier: Modifier = Modifier) {
    tag("br", modifier) { }
}

@Composable
fun section(modifier: Modifier, children: @Composable() () -> Unit) {
    tag("section", modifier) {
        children()
    }
}
