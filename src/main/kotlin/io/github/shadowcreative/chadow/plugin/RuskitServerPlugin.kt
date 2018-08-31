package io.github.shadowcreative.chadow.plugin

import io.github.shadowcreative.chadow.MessageHandler
import io.github.shadowcreative.chadow.component.Prefix
import io.github.shadowcreative.chadow.engine.SustainableHandler

interface RuskitServerPlugin
{
    fun getMessageHandler(): MessageHandler

    fun getHandleInstance(): Any?

    fun getServerPrefix(): Prefix?

    fun getRegisterHandlers(): List<SustainableHandler>

    fun reload()
}
