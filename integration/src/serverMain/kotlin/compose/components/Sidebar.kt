package compose.components

import androidx.compose.Composable
import compose.Screen
import compose.Theme
import me.shika.compose.a
import me.shika.compose.core.Modifier
import me.shika.compose.core.text
import me.shika.compose.div
import me.shika.compose.event.onClick
import me.shika.compose.nav
import me.shika.compose.section
import me.shika.compose.values.background
import me.shika.compose.values.height
import me.shika.compose.values.style
import me.shika.compose.values.width

@Composable
fun Sidebar(modifier: Modifier = Modifier, children: @Composable SidebarScope.() -> Unit) {
    div(
        modifier = modifier
            .style("display", "flex")
            .height("100%")
    ) {
        SidebarScope().apply { children() }
    }
}

class SidebarScope {
    @Composable
    fun side(children: @Composable() () -> Unit) {
        val theme = Theme.Ambient.current
        nav(
            modifier = Modifier
                .style("flex-basis", "20rem")
                .style("flex-grow", "1")
                .style("border-right", "1px solid ${theme.highlightColor}")
        ) {
            children()
        }
    }

    @Composable
    fun content(children: @Composable () -> Unit) {
        section(
            modifier = Modifier
                .style("flex-grow", "999")
                .style("flex-basis", "0")
                .style("padding", "20px")
        ) {
            children()
        }
    }
}

@Composable
fun SidebarHeader(item: Screen, onMainClick: () -> Unit, onThemeChange: (Theme) -> Unit) {
    val theme = Theme.Ambient.current
    div(Modifier
        .style("display", "flex")
        .style("margin-bottom", "20px")
        .style("border-bottom", "1px solid ${theme.highlightColor}")
    ) {
        a(modifier = Modifier
            .onClick(onMainClick)
            .style("flex-grow", "1")
            .style("padding", "25px 20px")
            .style("display", "block")
            .style("text-decoration", "none")
            .style("font-size", Theme.FontSize.MEDIUM)
        ) {
            text(item.description)
        }

        ThemeSwitcher(
            labelModifier = Modifier.style("margin", "22px 20px")
        ) { onThemeChange(it) }
    }
}

@Composable
fun SidebarItems(current: Screen, items: List<Screen>, onItemClick: (Screen) -> Unit) {
    items.forEach {
        div {
            a(
                modifier = Modifier.onClick { onItemClick(it) }
                    .width("100%")
                    .style("padding", "20px 20px")
                    .style("display", "block")
                    .style("font-size", Theme.FontSize.SMALL)
                    .run {
                        if (current == it) {
                            background(Theme.Ambient.current.highlightColor)
                        } else {
                            this
                        }
                    }
            ) {
                text(it.description)
            }
        }
    }
}
