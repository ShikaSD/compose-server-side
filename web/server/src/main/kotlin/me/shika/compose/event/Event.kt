package me.shika.compose.event

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import me.shika.compose.core.Modifier
import me.shika.compose.core.wrap

data class EventPayload<T : Event.Payload<*>>(val targetId: Long, val payload: T)

interface Event {
    val type: String

    interface Payload<E : Event> {
        val descriptor: E
    }

    interface Callback<E : Event, P : Payload<E>> {
        val descriptor: E
        val onReceive: (payload: P) -> Unit
    }
}

object Click : Event {
    override val type: String = "click"

    object Payload : Event.Payload<Click> {
        override val descriptor: Click = Click
    }

    class Callback(override val onReceive: (payload: Payload) -> Unit) : Event.Callback<Click, Payload>, Modifier {
        override val descriptor: Click = Click
    }
}

fun Modifier.onClick(callback: () -> Unit) =
    this wrap Click.Callback { callback() }

object Change : Event {
    override val type: String = "change"

    data class Payload(val value: String) : Event.Payload<Change> {
        override val descriptor: Change = Change
    }

    class Callback(override val onReceive: (payload: Payload) -> Unit) : Event.Callback<Change, Payload>, Modifier {
        override val descriptor: Change = Change
    }
}

fun Modifier.onChange(callback: (String) -> Unit) =
    this wrap Change.Callback { callback(it.value) }

object Input : Event {
    override val type: String = "input"

    data class Payload(val value: String) : Event.Payload<Input> {
        override val descriptor: Input = Input
    }

    class Callback(override val onReceive: (payload: Payload) -> Unit) : Event.Callback<Input, Payload>, Modifier {
        override val descriptor: Input = Input
    }
}

fun Modifier.onInput(callback: (String) -> Unit) =
    this wrap Input.Callback { callback(it.value) }

object KeyUp : Event {
    override val type: String = "keyup"

    data class Payload(val value: String) : Event.Payload<KeyUp> {
        override val descriptor: KeyUp = KeyUp
    }

    class Callback(override val onReceive: (payload: Payload) -> Unit) : Event.Callback<KeyUp, Payload>, Modifier {
        override val descriptor: KeyUp = KeyUp
    }
}

fun Modifier.onKeyUp(callback: (String) -> Unit) =
    this wrap KeyUp.Callback { callback(it.value) }

object MouseEnter : Event {
    override val type: String = "mouseenter"

    object Payload : Event.Payload<MouseEnter> {
        override val descriptor: MouseEnter = MouseEnter
    }

    class Callback(override val onReceive: (payload: Payload) -> Unit) : Event.Callback<MouseEnter, Payload>, Modifier {
        override val descriptor: MouseEnter = MouseEnter
    }
}

fun Modifier.onMouseEnter(callback: () -> Unit) =
    this wrap MouseEnter.Callback { callback() }

object MouseLeave : Event {
    override val type: String = "mouseleave"

    object Payload : Event.Payload<MouseLeave> {
        override val descriptor: MouseLeave = MouseLeave
    }

    class Callback(override val onReceive: (payload: Payload) -> Unit) : Event.Callback<MouseLeave, Payload>, Modifier {
        override val descriptor: MouseLeave = MouseLeave
    }
}

fun Modifier.onMouseLeave(callback: () -> Unit) =
    this wrap MouseLeave.Callback { callback() }

@Composable
fun Modifier.hover(f: Modifier.(isHovered: Boolean) -> Modifier): Modifier {
    var mouseOver by state { false }

    return onMouseEnter { mouseOver = true }
        .onMouseLeave { mouseOver = false }
        .run { f(mouseOver) }
}
