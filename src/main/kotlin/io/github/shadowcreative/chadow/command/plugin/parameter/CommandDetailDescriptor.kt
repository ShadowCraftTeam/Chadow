package io.github.shadowcreative.chadow.command.plugin.parameter

import io.github.shadowcreative.chadow.command.ParameterizeCommand

class CommandDetailDescriptor : ParameterizeCommand("detail", true)
{
    /*
    init
    {
        val description = FormatDescription("Show the detail command of {0}")
        description.setDescriptionSelector(0, this.getRawCurrentCommand(null).replace(" ", "."), "")
        this.setCommandDescription(description)
    }

    override fun output(listener: CommandSender, _: Int, __: Int, rawType: Boolean, order: CommandOrder): Any?
    {
        val componentList = ArrayList<ComponentString>()
        val rawDescription = FormatDescription("Review of command : {command}")
        rawDescription.addFilter("command", this.currentCommand)
        // Convert to base component.
        var description = CommandUtility.toBaseComponent(rawDescription) as ComponentString
        componentList.add(description)

        // &aThe framework of command: {current_command} (Included hover message)
        val cmdFramework = FormatDescription(currentCommand)
        cmdFramework.appendFront("&aThe framework of command: &e/&f")

        description = CommandUtility.toBaseComponent(cmdFramework) as ComponentString
        componentList.add(description)
        componentList.add(CommandUtility.toBaseComponent(this.getCommandDescription()) as ComponentString)
        componentList.add(CommandUtility.toBaseComponent(FormatDescription("Parameters : ")) as ComponentString)
        var parameterDescription : FormatDescription
        for(param in this.getParameters())
        {
            parameterDescription = FormatDescription(param.getName() + " | " + param.getDescription()!!.rawMessage())
            componentList.add(CommandUtility.toBaseComponent(parameterDescription) as ComponentString)
        }

        return Page(componentList).execute(sender, ArrayList())
    }


    override fun perform0(sender: CommandSender, argc: Int, argv: List<String>?, handleInstance: Any?): Any?
    {

        val componentList = ArrayList<ComponentString>()
        val rawDescription = FormatDescription("Review of command : {command}")
        rawDescription.addFilter("command", this.currentCommand)
        // Convert to base component.
        var description = CommandUtility.toBaseComponent(rawDescription) as ComponentString
        componentList.add(description)

        // &aThe framework of command: {current_command} (Included hover message)
        val cmdFramework = FormatDescription(currentCommand)
        cmdFramework.appendFront("&aThe framework of command: &e/&f")

        description = CommandUtility.toBaseComponent(cmdFramework) as ComponentString
        componentList.add(description)
        componentList.add(CommandUtility.toBaseComponent(this.getCommandDescription()) as ComponentString)
        componentList.add(CommandUtility.toBaseComponent(FormatDescription("Parameters : ")) as ComponentString)
        var parameterDescription : FormatDescription
        for(param in this.getParameters())
        {
            parameterDescription = FormatDescription(param.getName() + " | " + param.getDescription()!!.rawMessage())
            componentList.add(CommandUtility.toBaseComponent(parameterDescription) as ComponentString)
        }

        return Page(componentList).execute(sender, ArrayList())
    }
    */
}
