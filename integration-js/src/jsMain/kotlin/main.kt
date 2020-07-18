@file:OptIn(ExperimentalComposeApi::class)

import androidx.compose.*

@OptIn(ExperimentalComposeApi::class)
fun main() {
    val root = Node("body")

    val composition = compositionFor(
        123,
        Applier(root),
        Recomposer.current()
    )
    composition.setContent {
        tag("div") {
            tag("img") { }
        }
    }
}

class Node(val tag: String) {
    private val children: MutableList<Node> = mutableListOf()

    fun insertAt(index: Int, instance: Node) {
        children.add(index, instance)
    }

    fun move(from: Int, to: Int, count: Int) {
        if (from > to) {
            var current = to
            repeat(count) {
                val node = children[from]
                children.removeAt(from)
                children.add(current, node)
                current++
            }
        } else {
            repeat(count) {
                val node = children[from]
                children.removeAt(from)
                children.add(to - 1, node)
            }
        }
    }

    fun remove(index: Int, count: Int) {
        repeat(count) {
            val instance = children.removeAt(index)
        }
    }
}

private class Applier(root: Node) : AbstractApplier<Node>(root) {
    override fun insert(index: Int, instance: Node) {
        current.insertAt(index, instance)
    }

    override fun remove(index: Int, count: Int) {
        current.remove(index, count)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.move(from, to, count)
    }

    override fun onClear() {

    }
}

@Composable
fun tag(tag: String, children: @Composable () -> Unit) {
    emit<Node, Applier>(
        ctor = { Node(tag) },
        update = {  },
        children = children
    )
}
