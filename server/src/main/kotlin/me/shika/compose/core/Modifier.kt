package me.shika.compose.core

interface Modifier {
    fun <R> fold(acc: R, operation: (R, Modifier) -> R): R =
        operation(acc, this)

    companion object : Modifier {
        override fun <R> fold(acc: R, operation: (R, Modifier) -> R): R = acc
    }
}

infix fun Modifier.wrap(other: Modifier): Modifier =
    if (other === Modifier) this else CombinedModifier(this, other)

class CombinedModifier(
    private val outer: Modifier,
    private val inner: Modifier
): Modifier {
    override fun <R> fold(acc: R, operation: (R, Modifier) -> R): R =
        inner.fold(outer.fold(acc, operation), operation)
}

internal fun Modifier.toList(): List<Modifier> =
    fold(mutableListOf()) { list, modifier ->
        list.add(modifier)
        list
    }
