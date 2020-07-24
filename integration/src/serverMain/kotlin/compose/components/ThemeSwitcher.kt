package compose.components

import androidx.compose.Composable
import compose.Theme
import me.shika.compose.checkbox
import me.shika.compose.core.Modifier
import me.shika.compose.div
import me.shika.compose.event.hover
import me.shika.compose.event.onChange
import me.shika.compose.label
import me.shika.compose.values.*

@Composable
fun ThemeSwitcher(labelModifier: Modifier = Modifier, onThemeChanged: (Theme) -> Unit) {
    val currentTheme = Theme.Ambient.current
    val targetTheme = when (currentTheme) {
        Theme.DARK -> Theme.LIGHT
        Theme.LIGHT -> Theme.DARK
    }
    div(
        modifier = Modifier.className("theme")
    ) {
        checkbox(
            isChecked = currentTheme == Theme.DARK,
            modifier = Modifier
                .onChange { onThemeChanged(targetTheme) }
                .id("theme-switcher")
        )
        label(
            text = "Theme",
            modifier = labelModifier
                .labelFor("theme-switcher")
                .hover { hovered ->
                    this
                        .background(if (hovered) targetTheme.highlightColor else currentTheme.highlightColor)
                        .textColor(if (hovered) targetTheme.fgColor else currentTheme.fgColor)

                }
                .style("font-size", Theme.FontSize.SMALL)
        )
    }
}
