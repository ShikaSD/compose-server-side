package compose

import androidx.compose.ambientOf

enum class Theme(
    val background: String,
    val foreground: String,
    val highlight: String,
    val white: String,
    val accent: String,
    val accentHighlight: String,
    val outline: String
) {
    LIGHT(
        background = Palette.WHITE,
        foreground = Palette.BLACK,
        highlight = Palette.BEIGE,
        accent = Palette.RED,
        accentHighlight = Palette.YELLOW,
        white = Palette.WHITE,
        outline = Palette.LIGHT_BLUE
    ),
    DARK(
        background = Palette.BLACK,
        foreground = Palette.WHITE,
        highlight = Palette.BLUE,
        accent = Palette.RED,
        accentHighlight = Palette.YELLOW,
        white = Palette.WHITE,
        outline = Palette.LIGHT_BLUE
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
