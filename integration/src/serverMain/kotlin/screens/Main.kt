package screens

import Screen
import Theme
import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import me.shika.compose.button
import me.shika.compose.core.Modifier
import me.shika.compose.core.text
import me.shika.compose.div
import me.shika.compose.event.onClick
import me.shika.compose.event.onMouseEnter
import me.shika.compose.event.onMouseLeave
import me.shika.compose.values.background
import me.shika.compose.values.className
import me.shika.compose.values.marginAuto
import me.shika.compose.values.textColor


@Composable
fun Modifier.primaryButtonStyle(theme: Theme): Modifier {
    var mouseOnButton by state { false }

    return className("primary")
        .textColor(theme.textColor)
        .run {
            if (mouseOnButton) {
                background(theme.buttonHovered)
            } else {
                background(theme.buttonColor)
            }
        }
        .onMouseEnter { mouseOnButton = true }
        .onMouseLeave { mouseOnButton = false }
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
