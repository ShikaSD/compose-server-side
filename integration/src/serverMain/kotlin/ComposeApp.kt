import Screen.*
import androidx.compose.*
import me.shika.compose.core.Modifier
import me.shika.compose.div
import me.shika.compose.values.*
import screens.ChatScreen
import screens.MainScreen
import screens.TodoList

private fun Modifier.fullSize(): Modifier =
    style("overflow", "hidden")
        .height("100%")
        .width("100%")



enum class Screen(val description: String) {
    MAIN("Main screen"),
    TODO("Todo app"),
    CHAT("Chat room")
}

enum class Theme(
    val backgroundColor: String,
    val textColor: String,
    val buttonColor: String,
    val buttonHovered: String
) {
    LIGHT(
        backgroundColor = "white",
        textColor = "black",
        buttonColor = "red",
        buttonHovered = "orange"
    ),
    DARK(
        backgroundColor = "black",
        textColor = "white",
        buttonColor = "red",
        buttonHovered = "orange"
    );

    companion object {
        val Ambient = ambientOf<Theme>()
    }
}

@Composable
fun DemoApp() {
    var currentScreen by state { MAIN }
    var currentTheme by state { Theme.LIGHT }

    div(modifier = Modifier
        .fullSize()
        .className("content")
        .background(currentTheme.backgroundColor)
        .textColor(currentTheme.textColor)
    ) {
        Providers(
            Theme.Ambient provides currentTheme
        ) {
            when (currentScreen) {
                MAIN -> MainScreen(items = listOf(TODO, CHAT), onScreenChange = { currentScreen = it })
                TODO -> TodoList()
                CHAT -> ChatScreen()
            }
        }
    }
}

