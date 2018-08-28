package io.github.shadowcreative.shadow.command

import io.github.shadowcreative.shadow.command.plugin.DocumentCommand
import io.github.shadowcreative.shadow.util.ReflectionUtility

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginIdentifiableCommand
import org.bukkit.plugin.Plugin

import java.lang.reflect.Method
import java.util.*

open class RuskitCommandBase(name: String, command: RuskitCommand<*>) :
        Command(name,
                command.getCommandDescription().apply().rawMessage(),
                command.getPermissionMessage()!!.apply().rawMessage(),
                command.getAlias()),
        PluginIdentifiableCommand {

    var basedRuskitCommand: RuskitCommand<*>; protected set

    init
    {
        this.basedRuskitCommand = command
        ConfigureDocument(this.basedRuskitCommand)
        ConfigureFilter(this.basedRuskitCommand)
    }

    @Suppress("MemberVisibilityCanBePrivate", "FunctionName")
        companion object
        {
            fun IsCommandImplemented(ruskitCommand: RuskitCommand<*>): Boolean {
                val performMethod: Method = ReflectionUtility.MethodFromClass(ruskitCommand::class.java, "perform", onTargetOnly = true)!!
                return ReflectionUtility.IsImplemented(performMethod)
            }

            fun ConfigureFilter(ruskitCommand: RuskitCommand<*>)
            {
                ruskitCommand.getCommandDescription().addFilter("plugin_name", ruskitCommand.getPlugin()!!.name)
                ruskitCommand.getCommandDescription().addFilter("server_name", Bukkit.getServerName())
                ruskitCommand.getCommandDescription().addFilter("server_time", Date().toString())
                ruskitCommand.getCommandDescription().addFilter("parent_command",
                        ruskitCommand.getRawCurrentCommand(null, false).replace(" ",  "."))
                if(ruskitCommand.getChildCommands().isNotEmpty())
                {
                    for(c in ruskitCommand.getChildCommands())
                    {
                        ConfigureFilter(c)
                    }
                }
            }

            private fun ConfigureDocument(ruskitCommand: RuskitCommand<*>) {
                if (ruskitCommand is Document)
                    return

                if (ruskitCommand.getChildCommands().isNotEmpty()) {
                    if (IsCommandImplemented(ruskitCommand)) {
                        /// WARNING: this command class was implemented the perform, but has child command, Something wrong.
                    }
                    // Add document command. and check the child command has others.
                    ruskitCommand.addChildCommands(DocumentCommand())

                    // Add default parameter.
                    // What it needs is show it available other commands.
                    //val defaultParameter = ArrayList<Parameter>()
                    //defaultParameter.add(Parameter("args", true))
                    //val parameterField = command::class.java.superclass.getDeclaredField("params")
                    //ReflectionUtility.SetField(parameterField, command, defaultParameter)

                    for (child in ruskitCommand.getChildCommands())
                        ConfigureDocument(child)

                } else {
                    if (IsCommandImplemented(ruskitCommand)) {
                        /// Add command description command.
                        // ruskitCommand.addChildCommands(CommandDetailDescriptor())
                        // TODO()
                    } else {
                        // This command wasn't implemented command and hasn't child command.
                        // In other word, It is unavailable command.
                    }
                }
            }
        }

    override fun getPlugin(): Plugin
    {
        return this.basedRuskitCommand.getPlugin() as Plugin
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean
    {
        return this.basedRuskitCommand.execute(sender, ArrayList(args.asList())) != null
    }
}