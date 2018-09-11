package io.github.shadowcreative.chadow.command

import io.github.shadowcreative.chadow.command.plugin.DocumentCommand
import io.github.shadowcreative.chadow.command.plugin.parameter.CommandDetailDescriptor
import io.github.shadowcreative.chadow.util.ReflectionUtility
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin
import java.lang.reflect.Method
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

open class ChadowCommandBase(name: String, command: ChadowCommand<*>) :
        Command(name,
                command.getCommandDescription().apply().rawMessage(),
                command.getPermissionMessage()!!.apply().rawMessage(),
                command.getAlias()),
        PluginIdentifiableCommand {

    var basedChadowCommand: ChadowCommand<*>; protected set

    init
    {
        this.basedChadowCommand = command
        ConfigureDocument(this.basedChadowCommand)
        ConfigureFilter(this.basedChadowCommand)
    }

    @Suppress("MemberVisibilityCanBePrivate", "FunctionName")
        companion object
        {
            fun IsCommandImplemented(chadowCommand: ChadowCommand<*>): Boolean {
                val performMethod: Method = ReflectionUtility.MethodFromClass(chadowCommand::class.java, "perform", onTargetOnly = true)!!
                return ReflectionUtility.IsImplemented(performMethod)
            }

            fun ConfigureFilter(chadowCommand: ChadowCommand<*>)
            {
                chadowCommand.getCommandDescription().addFilter("plugin_name", chadowCommand.getPlugin()!!.name)
                chadowCommand.getCommandDescription().addFilter("server_name", Bukkit.getServerName())
                chadowCommand.getCommandDescription().addFilter("server_time", Date().toString())
                chadowCommand.getCommandDescription().addFilter("parent_command",
                        chadowCommand.getRawCurrentCommand(null, false).replace(" ",  "."))
                if(chadowCommand.getChildCommands().isNotEmpty())
                {
                    for(c in chadowCommand.getChildCommands())
                    {
                        ConfigureFilter(c)
                    }
                }
            }

            private fun ConfigureDocument(chadowCommand: ChadowCommand<*>) {
                if (chadowCommand is Document)
                    return

                if (chadowCommand.getChildCommands().isNotEmpty()) {
                    if (IsCommandImplemented(chadowCommand)) {
                        // WARNING: This command class was implemented the perform, but has child command, Something wrong.
                        Logger.getGlobal().log(Level.WARNING, "The object ChadowCommand[${chadowCommand.getRawCurrentCommand()}] | Implemented perform but it has child command")
                    }
                    // Add document command. and check the child command has others.
                    chadowCommand.addChildCommands(DocumentCommand())

                    // Add default parameter.
                    // What it needs is show it available other commands.
                    //val defaultParameter = ArrayList<Parameter>()
                    //defaultParameter.add(Parameter("args", true))
                    //val parameterField = command::class.java.superclass.getDeclaredField("params")
                    //ReflectionUtility.SetField(parameterField, command, defaultParameter)

                    for (child in chadowCommand.getChildCommands())
                        ConfigureDocument(child)

                } else {
                    if (IsCommandImplemented(chadowCommand)) {
                        // Add command description command.
                        // In other word, It uses only execute something.
                        // It registers information how to use this command or what it is.
                        chadowCommand.addChildCommands(CommandDetailDescriptor())
                    }
                    else
                    {
                        // This command wasn't implemented command and hasn't child command.
                        // In other word, It is unavailable command.
                    }
                }
            }
        }

    override fun getPlugin(): Plugin
    {
        return this.basedChadowCommand.getPlugin() as Plugin
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean
    {
        return this.basedChadowCommand.execute(sender, ArrayList(args.asList())) != null
    }
}