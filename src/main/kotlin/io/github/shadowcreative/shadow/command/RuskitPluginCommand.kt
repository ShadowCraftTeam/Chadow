package io.github.shadowcreative.shadow.command

import io.github.shadowcreative.shadow.command.plugin.PluginCommand
import io.github.shadowcreative.shadow.command.plugin.ReloadCommand
import io.github.shadowcreative.shadow.command.plugin.UpdateCommand
import io.github.shadowcreative.shadow.command.plugin.policy.PolicyCommand

class RuskitPluginCommand : RuskitCommand<RuskitPluginCommand>("ruskit", "rusk", "rus")
{
    companion object
    {
        private val instance = RuskitPluginCommand()
        @JvmStatic fun getInstance() : RuskitPluginCommand = instance
    }

    private val reloadCommand : ReloadCommand = ReloadCommand()
    private val updateCommand : UpdateCommand = UpdateCommand()
    private val policyCommand : PolicyCommand = PolicyCommand()
    private val pluginCommand : PluginCommand = PluginCommand()
    
    init
    {
        this.addChildCommands(reloadCommand, updateCommand, policyCommand, pluginCommand)
        this.setPermission("ruskit")
    }
}
