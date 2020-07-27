package compose.screens

import androidx.compose.Composable
import compose.GITHUB_BASE_LINK
import me.shika.compose.*
import me.shika.compose.core.Modifier
import me.shika.compose.core.text
import me.shika.compose.values.style

@Composable
fun MainScreen() {
    div(modifier = Modifier.style("line-height", "2")) {
        h1 {
            text("Compose server side demo")
        }

        p {
            text("Hi, welcome to the demo of running Jetpack Compose on a server.")
            br()
            text("You can check different single page examples using navigation on the side.")
            br()
            br()
            text("Don't forget to check out source code on ")
            a(href = GITHUB_BASE_LINK) {
                text("GitHub")
            }
            text(" or using the links at the bottom of the sidebar.")
            br()
            br()
            text("You can also use browser console to check what data is sent between client and server.")
        }
    }
}
