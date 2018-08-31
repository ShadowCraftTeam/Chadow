package io.github.shadowcreative.chadow.command

import io.github.shadowcreative.chadow.command.misc.Parameter
import org.bukkit.command.CommandSender

abstract class ParameterizeCommand(name: String, required: Boolean) : Parameter(name, required)
{
    private var parameterFunction : ((CommandSender, String) -> Any?)? = null
    fun setParameterFunction(function: (CommandSender, String) -> Any?) { this.parameterFunction = function }
    fun runParameterFunction(target: CommandSender, arguments: String) : Any?
    {
        return if(this.parameterFunction == null)
        {
            null
        }
        else
        {
            this.parameterFunction!!(target, arguments)
        }
    }

    init {
        if(true)
        {

        }
    }

}