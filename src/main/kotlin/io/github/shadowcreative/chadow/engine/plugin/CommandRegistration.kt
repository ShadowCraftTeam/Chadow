package io.github.shadowcreative.chadow.engine.plugin

import io.github.shadowcreative.chadow.command.ChadowCommand
import io.github.shadowcreative.chadow.command.ChadowCommandBase
import io.github.shadowcreative.chadow.engine.RuntimeTaskScheduler
import io.github.shadowcreative.chadow.plugin.IntegratedPlugin
import io.github.shadowcreative.chadow.util.ReflectionUtility
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import java.util.*

class CommandRegistration : RuntimeTaskScheduler()
{
    override fun onInit(handleInstance: Any?): Any?
    {
        registerCommand()
        return true
    }

    companion object
    {
        private val instance : CommandRegistration = CommandRegistration()
        @JvmStatic fun getInstance() : CommandRegistration = instance

        private fun registerCommand()
        {
            val commandMap = simpleCommandMap
            val knownCommands = getSimpleCommandMapRegistered(commandMap)
            val nameTargets = HashMap<String, ChadowCommand<*>>()
            for (abstractCommand in ChadowCommand.EntireCommand())
            {
                nameTargets[abstractCommand.getCommand()] = abstractCommand
            }

            for ((name, target) in nameTargets)
            {
                target.setEnabled(IntegratedPlugin.CorePlugin)
                val current = knownCommands[name]
                val commandTarget = getChadowCommand(current)

                if (target === commandTarget) continue

                if (current != null)
                {
                    knownCommands.remove(name)
                    current.unregister(commandMap)
                }

                val command = ChadowCommandBase(name, target)

                val plugin = command.basedChadowCommand.getPlugin()
                val pluginName = if (plugin != null) plugin.name else "ChadowFrameworkEngine"
                commandMap.register(pluginName, command)
            }
        }

        private fun getChadowCommand(command: Command?): ChadowCommand<*>?
        {
            if (command == null) return null
            if (command !is ChadowCommandBase) return null
            val cbc = command as ChadowCommandBase?
            return cbc!!.basedChadowCommand
        }

        private var commandMapField = ReflectionUtility.GetField(Bukkit.getServer().javaClass, "commandMap")
        private var simpleCommandField = ReflectionUtility.GetField(SimpleCommandMap::class.java, "knownCommands")

        private val simpleCommandMap: SimpleCommandMap
            get() {
                val server = Bukkit.getServer()
                return ReflectionUtility.GetField(commandMapField!!, server)
            }

        private fun getSimpleCommandMapRegistered(simpleCommandMap: SimpleCommandMap): HashMap<String, Command>
        {
            return ReflectionUtility.GetField(simpleCommandField!!, simpleCommandMap)
        }
    }
}