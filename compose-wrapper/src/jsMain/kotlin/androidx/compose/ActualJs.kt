package androidx.compose

internal actual open class ThreadLocal<T> actual constructor(initialValue: () -> T) {
    private var value: T = initialValue()

    actual fun get(): T = value
    actual fun set(value: T) {
        this.value = value
    }
}

// TODO use weak map
actual typealias WeakHashMap<K, V> = HashMap<K, V>

internal actual inline fun <R> synchronized(lock: Any, block: () -> R): R =
    block()

actual class AtomicReference<V> actual constructor(value: V) {
    private var value = value

    actual fun get(): V = value
    actual fun set(value: V) {
        this.value = value
    }
    actual fun getAndSet(value: V): V = this.value.also { this.value = value }
    actual fun compareAndSet(expect: V, newValue: V): Boolean =
        (this.value == expect).also { if (it) { this.value = newValue } }
}

@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CONSTRUCTOR
)
actual annotation class TestOnly()

internal actual class BuildableMapBuilder<K, V>(private val map: MutableMap<K, V>) : MutableMap<K, V> by map {
    actual fun build(): BuildableMap<K, V> =
        BuildableMap(map)
}

actual class BuildableMap<K, V>(private val map: Map<K, V> = emptyMap()) : Map<K, V> by map {
    internal actual fun builder(): BuildableMapBuilder<K, V> =
        BuildableMapBuilder(map.toMutableMap())
}

internal actual fun <K, V> buildableMapOf(): BuildableMap<K, V> = BuildableMap()

internal actual class BuildableListBuilder<T>(private val list: MutableList<T>) : MutableList<T> by list {
    actual fun build(): BuildableList<T> =
        BuildableList(list)
}

internal actual class BuildableList<T>(private val list: List<T> = emptyList()) : List<T> by list {
    internal actual fun builder(): BuildableListBuilder<T> = BuildableListBuilder(list.toMutableList())
    internal actual fun add(element: T): BuildableList<T> = BuildableList(list + element)
    internal actual fun add(index: Int, element: T): BuildableList<T> = BuildableList(list.toMutableList().apply { add(index, element) })
    internal actual fun addAll(elements: Collection<T>): BuildableList<T> = BuildableList(list.toMutableList().apply { addAll(elements) })
    internal actual fun remove(element: T): BuildableList<T> = BuildableList(list - element)
    internal actual fun removeAll(elements: Collection<T>): BuildableList<T> = BuildableList(list - elements)
    internal actual fun removeAt(index: Int): BuildableList<T> = BuildableList(list.toMutableList().apply { removeAt(index) })
    internal actual fun set(index: Int, element: T): BuildableList<T> = BuildableList(list.toMutableList().apply { set(index, element) })
}

internal actual fun <T> buildableListOf(): BuildableList<T> = BuildableList()

internal actual fun identityHashCode(instance: Any?): Int = instance?.hashCode() ?: 0

actual class ObserverMap<K : Any, V : Any> {
    // todo use weak references
    private val keyToValues = HashMap<K, MutableSet<V>>()
    private val valueToKeys = HashMap<V, MutableSet<K>>()

    actual fun add(key: K, value: V) {
        val valueSet = keyToValues.getOrPut(key) { mutableSetOf() }
        valueSet.add(value)

        val keySet = valueToKeys.getOrPut(value) { mutableSetOf() }
        keySet.add(key)
    }
    actual fun remove(key: K) {
        keyToValues.remove(key)?.forEach { valueToKeys.remove(it) }
    }
    actual fun remove(key: K, value: V) {
        keyToValues.get(key)?.remove(value)
        valueToKeys.remove(value)
    }
    actual fun contains(key: K, value: V): Boolean =
        keyToValues.get(key)?.contains(value) == true

    actual fun clear() {
        keyToValues.clear()
        valueToKeys.clear()
    }
    actual operator fun get(keys: Iterable<K>): List<V> =
        keys.flatMap {
            keyToValues.get(it) ?: emptyList()
        }
    actual fun getValueOf(key: K): List<V> =
        keyToValues.get(key)?.toList() ?: emptyList()

    actual fun clearValues(predicate: (V) -> Boolean) {
        val toRemove = valueToKeys.keys.filter { predicate(it) }
        toRemove.forEach {
            val keys = valueToKeys.remove(it)
            keys?.forEach { key ->
                keyToValues.get(key)?.remove(it)
            }
        }
    }
    actual fun removeValue(value: V) {
        val keys = valueToKeys.remove(value)
        keys?.forEach {
            keyToValues.get(it)?.remove(value)
        }
    }
}
