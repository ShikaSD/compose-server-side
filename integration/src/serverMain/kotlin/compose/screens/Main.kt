package compose.screens

import androidx.compose.Composable
import compose.Screen
import compose.Theme
import me.shika.compose.button
import me.shika.compose.core.Modifier
import me.shika.compose.core.text
import me.shika.compose.div
import me.shika.compose.event.hover
import me.shika.compose.event.onClick
import me.shika.compose.values.background
import me.shika.compose.values.className
import me.shika.compose.values.marginAuto
import me.shika.compose.values.textColor


@Composable
fun Modifier.primaryButtonStyle(theme: Theme): Modifier {

    return className("primary")
        .textColor(theme.fgColor)
        .hover {
            if (it) {
                background(theme.buttonHovered)
            } else {
                background(theme.buttonColor)
            }
        }
}

@Composable
fun MainScreen(items: List<Screen>, onScreenChange: (Screen) -> Unit) {
    div(modifier = Modifier.className("main")) {
        items.forEach {
            PrimaryButton(it, onScreenChange)
        }
    }
}

@Composable
fun PrimaryButton(item: Screen, onScreenChange: (Screen) -> Unit) {
    button(
        modifier = Modifier
            .primaryButtonStyle(Theme.Ambient.current)
            .marginAuto()
            .onClick { onScreenChange(item) }
    ) {
        text(item.description)
    }
}
