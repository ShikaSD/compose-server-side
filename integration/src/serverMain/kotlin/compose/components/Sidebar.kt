package compose.components

import androidx.compose.Composable
import compose.GITHUB_BASE_LINK
import compose.Screen
import compose.Theme
import me.shika.compose.*
import me.shika.compose.FlexScope.grow
import me.shika.compose.core.Modifier
import me.shika.compose.core.text
import me.shika.compose.event.onClick
import me.shika.compose.values.*

@Composable
fun Sidebar(modifier: Modifier = Modifier, children: @Composable SidebarScope.() -> Unit) {
    Row(modifier = modifier.fullHeight()) {
        SidebarScope.apply { children() }
    }
}

object SidebarScope {
    @Composable
    fun side(children: @Composable() FlexScope.() -> Unit) {
        val theme = Theme.Ambient.current
        nav(
            modifier = Modifier
                .style("flex-basis", "20rem")
                .grow(1)
                .style("border-right", "1px solid ${theme.highlight}")
        ) {
            Column(Modifier.fullHeight()) {
                children()
            }
        }
    }

    @Composable
    fun content(children: @Composable () -> Unit) {
        with (FlexScope) {
            section(
                modifier = Modifier
                    .grow(999)
                    .style("flex-basis", "0")
                    .style("padding", "20px")
                    .display( "flex")
                    .style("flex-direction", "column")
            ) {
                children()
            }
        }
    }
}

@Composable
fun FlexScope.SidebarHeader(item: Screen, onMainClick: () -> Unit, onThemeChange: (Theme) -> Unit) {
    val theme = Theme.Ambient.current
    Row(
        modifier = Modifier
            .style("margin-bottom", "20px")
            .style("border-bottom", "1px solid ${theme.highlight}")
    ) {
        a(modifier = Modifier
            .onClick(onMainClick)
            .grow(1)
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
fun FlexScope.SidebarItems(current: Screen, items: List<Screen>, onItemClick: (Screen) -> Unit) {
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
                            background(Theme.Ambient.current.highlight)
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

@Composable
fun FlexScope.SidebarFooter(current: Screen) {
    div(Modifier.margin("auto auto 25px auto")) {
        a(
            href = GITHUB_BASE_LINK + "tree/master/integration/src/serverMain/kotlin/compose/" + current.relativePath,
            modifier = Modifier.textSize(Theme.FontSize.SMALL)
        ) {
            text("Check this screen on Github")
        }
    }
}
