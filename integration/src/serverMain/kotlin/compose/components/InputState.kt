package compose.components

import androidx.compose.Composable
import androidx.compose.invalidate

/**
 * Emulates Compose state without triggering recompose until reset, where it recomposes with initial value
 */
class InputState(
    private val initialValue: String
) {
    var value: String = initialValue
    private var invalidateValue: () -> Unit = {  }

    @Composable
    fun compositionValue(): String = value.also {
        invalidateValue = invalidate
    }

    fun reset() {
        value = initialValue
        invalidateValue()
    }
}
