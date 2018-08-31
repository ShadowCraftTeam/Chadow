package io.github.shadowcreative.chadow.command.plugin

import io.github.shadowcreative.chadow.command.RuskitCommand
import io.github.shadowcreative.chadow.command.misc.Parameter

open class PluginCommand : RuskitCommand<PluginCommand>("plugin", "pl")
{
    init {
        this.setCommandDescription("Show all plugins that depend on RuskitIntegratedPlugin")
        this.setPermission("plugin")
        this.setDefaultUser(false)
        this.addParameter(Parameter("plugin_name", true), Parameter("options"))
    }
}
