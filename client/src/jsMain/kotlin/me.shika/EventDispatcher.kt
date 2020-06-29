package me.shika

class EventDispatcher(private val dispatchEvent: (ClientEvent) -> Unit) {
    fun dispatchEvent(nodeId: Long, name: String, values: Map<String, String>) {
        val data = ClientEvent(nodeId, name, values)
        dispatchEvent(data)
    }
}
