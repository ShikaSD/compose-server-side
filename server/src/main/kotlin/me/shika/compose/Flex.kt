package me.shika.compose

import androidx.compose.Composable
import me.shika.compose.core.Modifier
import me.shika.compose.values.display
import me.shika.compose.values.style

@Composable
fun Column(modifier: Modifier = Modifier, children: @Composable FlexScope.() -> Unit) {
    div(
        modifier.display("flex").style("flex-direction", "column"),
    ) {
        FlexScope.children()
    }
}

@Composable
fun Row(modifier: Modifier = Modifier, children: @Composable FlexScope.() -> Unit) {
    div(
        modifier.display("flex").style("flex-direction", "row"),
    ) {
        FlexScope.children()
    }
}

object FlexScope {
    fun Modifier.weight(value: Int) = style("flex-grow", "$value")

    fun Modifier.end() = style("align-self", "flex-end")
}
