package compose

import androidx.compose.ambientOf

enum class Theme(
    val bgColor: String,
    val fgColor: String,
    val highlightColor: String,
    val buttonColor: String,
    val buttonHovered: String
) {
    LIGHT(
        bgColor = Palette.WHITE,
        fgColor = Palette.BLACK,
        highlightColor = Palette.BEIGE,
        buttonColor = Palette.RED,
        buttonHovered = Palette.YELLOW
    ),
    DARK(
        bgColor = Palette.BLACK,
        fgColor = Palette.WHITE,
        highlightColor = Palette.BLUE,
        buttonColor = Palette.RED,
        buttonHovered = Palette.YELLOW
    );

    companion object {
        val Ambient = ambientOf<Theme>()
    }

    object FontSize {
        const val MEDIUM = "18px"
        const val SMALL = "14px"
        const val BIG = "22px"
    }

    private object Palette {
        const val BLACK = "#171C2B"
        const val WHITE = "#FCFBF9"
        const val BLUE = "#273253"
        const val RED = "#ED5C4D"
        const val BEIGE = "#F4E9DA"
        const val YELLOW ="#FBBE4B"
        const val LIGHT_BLUE = "#57B5ED"
    }
}
