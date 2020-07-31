package compose

import androidx.compose.*
import compose.Screen.*
import compose.components.Sidebar
import compose.components.SidebarFooter
import compose.components.SidebarHeader
import compose.components.SidebarItems
import compose.screens.ChatScreen
import compose.screens.MainScreen
import compose.screens.TodoList
import me.shika.compose.core.Modifier
import me.shika.compose.div
import me.shika.compose.values.*

const val GITHUB_BASE_LINK = "https://github.com/ShikaSD/compose-server-side/"

enum class Screen(val description: String, val relativePath: String) {
    MAIN("Home", "screens/Main.kt"),
    TODO("Todo app", "screens/Todos.kt"),
    CHAT("Chat room", "screens/Chat.kt")
}

@Composable
fun DemoApp() {
    var currentScreen by state { MAIN }
    var currentTheme by state { Theme.LIGHT }

    Providers(
        Theme.Ambient provides currentTheme
    ) {
        FullPage {
            Sidebar {
                side {
                    SidebarHeader(
                        item = MAIN,
                        onMainClick = { currentScreen = MAIN },
                        onThemeChange = { currentTheme = it }
                    )
                    SidebarItems(
                        current = currentScreen,
                        items = listOf(TODO, CHAT),
                        onItemClick = { currentScreen = it }
                    )
                    SidebarFooter(currentScreen)
                }

                content {
                    when (currentScreen) {
                        MAIN -> MainScreen()
                        TODO -> TodoList()
                        CHAT -> ChatScreen()
                    }
                }
            }
        }
    }
}

private fun Modifier.fullSize(): Modifier =
    style("overflow", "hidden")
        .height("100%")
        .width("100%")

@Composable
private fun FullPage(children: @Composable () -> Unit) {
    div(modifier = Modifier
        .fullSize()
        .className("content")
        .background(Theme.Ambient.current.background)
        .textColor(Theme.Ambient.current.foreground)
    ) {
        children()
    }
}
