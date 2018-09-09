package io.github.shadowcreative.chadow.command

import io.github.shadowcreative.chadow.command.plugin.PluginCommand
import io.github.shadowcreative.chadow.command.plugin.ReloadCommand
import io.github.shadowcreative.chadow.command.plugin.UpdateCommand
import io.github.shadowcreative.chadow.command.plugin.policy.PolicyCommand

class ChadowPluginCommand : ChadowCommand<ChadowPluginCommand>("chadow", "rusk", "rus")
{
    companion object
    {
        private val instance = ChadowPluginCommand()
        @JvmStatic fun getInstance() : ChadowPluginCommand = instance
    }

    private val reloadCommand : ReloadCommand = ReloadCommand()
    private val updateCommand : UpdateCommand = UpdateCommand()
    private val policyCommand : PolicyCommand = PolicyCommand()
    private val pluginCommand : PluginCommand = PluginCommand()
    
    init
    {
        this.addChildCommands(reloadCommand, updateCommand, policyCommand, pluginCommand)
        this.setPermission("chadow")
    }
}
