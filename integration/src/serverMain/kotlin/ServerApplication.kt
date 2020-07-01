import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import me.shika.compose.*

@Composable
fun ComposeApp() {
    var h1Class by mutableStateOf("some")

    div {
        var title by mutableStateOf("Hello")
        h1(className = h1Class) {
            text(title)
        }
        var counter by mutableStateOf(0)
        h2 {
            text("Counter $counter")
        }

        input(type = "text") {
            title = it
        }
        button(text = "Increment!") {
            counter += 1
        }
    }
}

