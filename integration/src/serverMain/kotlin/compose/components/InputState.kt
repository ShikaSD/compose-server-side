package compose.components

import androidx.compose.MutableState
import androidx.compose.mutableStateOf

/**
 * Emulates Compose state without triggering recompose until submit, where it recomposes with initial value
 */
class InputState<T>(
    private val initialValue: T,
    private val remoteState: MutableState<T> = mutableStateOf(initialValue)
) : MutableState<T> by remoteState {
    private var localState: T = initialValue

    override var value: T
        get() = remoteState.value
        set(value) {
            localState = value
        }

    fun submit(callback: (T) -> Unit) {
        remoteState.value = localState
        callback(remoteState.value)
        remoteState.value = initialValue
        localState = initialValue
    }
}
