package me.shika.compose

data class EventPayload<T : Event.Payload<*>>(val targetId: Long, val payload: T)

interface Event {
    val type: String

    interface Payload<T : Event> {
        val descriptor: T
    }
}

object Click : Event {
    override val type: String = "click"

    object Payload : Event.Payload<Click> {
        override val descriptor: Click = Click
    }
}

object InputChange : Event {
    override val type: String = "change"

    data class Payload(val value: String) : Event.Payload<InputChange> {
        override val descriptor: InputChange = InputChange
    }
}
