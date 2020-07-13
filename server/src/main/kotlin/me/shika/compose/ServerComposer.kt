@file:OptIn(ExperimentalComposeApi::class)

package me.shika.compose

import androidx.compose.AbstractApplier
import androidx.compose.ExperimentalComposeApi

class ServerApplyAdapter(
    private val commandDispatcher: RenderCommandDispatcher,
    root: HtmlNode
) : AbstractApplier<HtmlNode>(root) {
    override fun up() {
        super.up()
        if (current == root) {
            commandDispatcher.commit()
        }
    }

    override fun insert(index: Int, instance: HtmlNode) {
        println("insert ${current.id} $index $instance")
        tag().insertAt(index, instance)
        commandDispatcher.insert(current, index, instance)
    }

    override fun remove(index: Int, count: Int) {
        println("remove ${current.id} $index $count")
        tag().remove(index, count)
        commandDispatcher.remove(current, index, count)
    }

    override fun move(from: Int, to: Int, count: Int) {
        println("move ${current.id} $from $to $count")
        tag().move(from, to, count)
        commandDispatcher.move(current, from, to, count)
    }

    override fun onClear() {
        // no-op
    }

    private fun tag(): HtmlNode.Tag =
        when (val node = current) {
            is HtmlNode.Tag -> node
            is HtmlNode.Text -> throw IllegalStateException("Only tag can have children")
        }

}
