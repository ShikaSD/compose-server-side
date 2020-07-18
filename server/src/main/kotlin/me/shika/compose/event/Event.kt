package me.shika.compose.event

import me.shika.compose.core.Modifier

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

fun Modifier.click(callback: () -> Unit) =
    this + Click.Callback { callback() }

object Change : Event {
    override val type: String = "change"

    data class Payload(val value: String) : Event.Payload<Change> {
        override val descriptor: Change = Change
    }

    class Callback(override val onReceive: (payload: Payload) -> Unit) : Event.Callback<Change, Payload>, Modifier {
        override val descriptor: Change = Change
    }
}

fun Modifier.change(callback: (String) -> Unit) =
    this + Change.Callback { callback(it.value) }

object Input : Event {
    override val type: String = "input"

    data class Payload(val value: String) : Event.Payload<Input> {
        override val descriptor: Input = Input
    }

    class Callback(override val onReceive: (payload: Payload) -> Unit) : Event.Callback<Input, Payload>, Modifier {
        override val descriptor: Input = Input
    }
}

fun Modifier.input(callback: (String) -> Unit) =
    this + Input.Callback { callback(it.value) }

object KeyUp : Event {
    override val type: String = "keyup"

    data class Payload(val value: String) : Event.Payload<KeyUp> {
        override val descriptor: KeyUp = KeyUp
    }

    class Callback(override val onReceive: (payload: Payload) -> Unit) : Event.Callback<KeyUp, Payload>, Modifier {
        override val descriptor: KeyUp = KeyUp
    }
}

fun Modifier.keyup(callback: (String) -> Unit) =
    this + KeyUp.Callback { callback(it.value) }
