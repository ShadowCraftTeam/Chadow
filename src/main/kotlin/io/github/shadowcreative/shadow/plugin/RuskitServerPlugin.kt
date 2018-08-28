package io.github.shadowcreative.shadow.plugin

import io.github.shadowcreative.shadow.MessageHandler
import io.github.shadowcreative.shadow.component.Prefix
import io.github.shadowcreative.shadow.engine.SustainableHandler

interface RuskitServerPlugin
{
    fun getMessageHandler(): MessageHandler

    fun getHandleInstance(): Any?

    fun getServerPrefix(): Prefix?

    fun getRegisterHandlers(): List<SustainableHandler>

    fun reload()
}
