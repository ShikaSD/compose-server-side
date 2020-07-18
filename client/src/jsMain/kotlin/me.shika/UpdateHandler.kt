package me.shika

import org.w3c.dom.HTMLElement

class UpdateHandler(rootElement: HTMLElement, eventDispatcher: EventDispatcher) {
    private val nodeUpdater by lazy { NodeUpdater(rootElement, eventDispatcher) }

    fun handleCommand(renderCommand: RenderCommand) {
        for (update in renderCommand.nodeUpdates) {
            when (val command = update.command) {
                is NodeUpdate.Command.Insert -> {
                    nodeUpdater.insert(parentId = update.nodeId, index = command.index, node = command.node)
                }
                is NodeUpdate.Command.Remove -> {
                    nodeUpdater.remove(parentId = update.nodeId, index = command.index, count = command.count)
                }
                is NodeUpdate.Command.Move -> {
                    nodeUpdater.move(
                        parentId = update.nodeId,
                        from = command.from,
                        to = command.to,
                        count = command.count
                    )
                }
            }
        }

        for (update in renderCommand.valueUpdates) {
            nodeUpdater.update(
                id = update.nodeId,
                newEvents = update.events,
                newAttrs = update.attributes,
                newStyles = update.styles,
                newProperties = update.properties
            )
        }
    }
}
