package compose.components

import androidx.compose.MutableState
import androidx.compose.mutableStateOf

/**
 * Emulates Compose state without triggering recompose until submit, where it recomposes with initial value
 */
class InputState(
    private val initialValue: String,
    private val remoteState: MutableState<String> = mutableStateOf(initialValue)
) : MutableState<String> by remoteState {
    private var localState: String = initialValue

    override var value: String
        get() = remoteState.value
        set(value) { localState = value }

    fun submit(callback: (String) -> Unit) {
        if (localState.isBlank()) return

        callback(localState)
        remoteState.value = localState
        localState = initialValue
        remoteState.value = initialValue
    }
}
