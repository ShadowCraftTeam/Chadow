/*
Copyright (c) 2018 ruskonert
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package io.github.shadowcreative.shadow.command

import io.github.shadowcreative.shadow.Activator
import io.github.shadowcreative.shadow.command.misc.CommandOrder
import io.github.shadowcreative.shadow.command.misc.Parameter
import io.github.shadowcreative.shadow.command.misc.Permission
import io.github.shadowcreative.shadow.component.FormatDescription
import io.github.shadowcreative.shadow.event.command.RuskitCommandEvent
import io.github.shadowcreative.shadow.exception.PermissionPolicyException
import io.github.shadowcreative.shadow.platform.GenericInstance
import io.github.shadowcreative.shadow.platform.code.NotImplemented
import io.github.shadowcreative.shadow.plugin.IntegratedPlugin
import io.github.shadowcreative.shadow.util.CommandUtility
import io.github.shadowcreative.shadow.util.StringUtility

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * RuskitCommand is an framework that build the relative command or plugin base.
 * It creates the command documentation, navigate function, permission plugin, etc.
 * @param S The command class that inherited it
 * @see io.github.ruskonert.ruskit.command.RuskitCommand
 */
@Suppress("DEPRECATION")
abstract class RuskitCommand<S : RuskitCommand<S>> : GenericInstance<RuskitCommand<S>>, CommandExecutable, Activator<IntegratedPlugin?>
{
    constructor(command: String)
    {
        this.command = command
    }
    constructor(command: String, vararg alias: String) : this(command)
    {
        this.addAlias(alias)
    }

    private var permissionMessage : FormatDescription? = FormatDescription()
    fun getPermissionMessage() : FormatDescription? = this.permissionMessage
    fun setPermissionMessage(fd : FormatDescription) { this.permissionMessage = fd }
    fun setPermissionMessage(s : String) { this.permissionMessage = FormatDescription(s) }

    // PLEASE DO NOT REFLECT DIRECTLY THIS FIELD. IT WILL BE NULL.
    // BECAUSE THIS VALUE INHERITED TO PARENT COMMAND. USE getPlugin() METHOD.
    private var handlePlugin : IntegratedPlugin? = null
    fun getPlugin() : IntegratedPlugin?
    {
        return if(this.handlePlugin == null) {
            if(this.parent != null)
                this.parent!!.getPlugin()
            else null
        }
        else {
            this.handlePlugin
        }
    }

    @Deprecated("Command Activator is not implemented")
    override fun setEnabled(active: Boolean)
    {
        if(active)
        {
            if(this.isEnabled()) return
            ENTIRE_COMMANDS.add(this)
        }
        else
        {
            if(this.isEnabled()) { ENTIRE_COMMANDS.remove(this); }
        }
    }

    @Deprecated("Command Activator is not implemented")
    override fun setEnabled(handleInstance: IntegratedPlugin?)
    {
        this.handlePlugin = handleInstance
        this.setEnabled(handleInstance != null)
    }

    @Deprecated("Command Activator is not implemented")
    override fun isEnabled(): Boolean
    {
        return ENTIRE_COMMANDS.contains(this)
    }
    companion object
    {
        @Deprecated("Command Activator is not implemented")
        private val ENTIRE_COMMANDS : ArrayList<RuskitCommand<*>> = ArrayList()

        @Deprecated("Command Activator is not implemented")
        private fun Register(c : RuskitCommand<*>) { ENTIRE_COMMANDS.add(c) }

        @Deprecated("Command Activator is not implemented")
        @JvmStatic fun EntireCommand() : List<RuskitCommand<*>> = ENTIRE_COMMANDS
    }

    fun getCurrentCommand(target: CommandSender? = null, itself: Boolean = true) : FormatDescription = this.getCurrentCommandBase(target, false, itself) as FormatDescription

    fun getRawCurrentCommand(target : CommandSender? = null, itself : Boolean = true) : String = this.getCurrentCommandBase(target, true, itself) as String

    protected open fun getCurrentCommandBase(target : CommandSender? = null, rawType : Boolean = false, itself: Boolean = true) : Any
    {
        try
        {
            if(target == null)
                return this.getCurrentCommandBase(Bukkit.getConsoleSender(), rawType)

            val currentTreeCommand = FormatDescription("")
            var tree: RuskitCommand<*>?
            tree = if(itself)
                this
            else
                this.getParentCommand()

            var treeIndex = 0

            while (tree != null)
            {
                // get relative permission value.
                var permissionName = tree.getRelativePermission()!!.getPermissionName()
                permissionName = if (target.hasPermission(permissionName)) "&a$permissionName" else "&4$permissionName"

                // So, What is this code?
                // If the user touched command sentence on chat, It will be show the message box.
                // In other words, That's JSON Message.
                val description = ArrayList<String>()
                description.add("Another commands: ${tree.alias}")
                description.add("Permission: $permissionName")

                currentTreeCommand.appendFront(" {$treeIndex}")
                currentTreeCommand.setDescriptionSelector(treeIndex, tree.command, description)
                treeIndex++
                tree = tree.getParentCommand()
            }

            // remove trim.
            currentTreeCommand.format(currentTreeCommand.getFormat().trim())

            // check raw type is true.
            return if (rawType)
                currentTreeCommand.rawMessage()
            else
                currentTreeCommand
        }
        catch(e : Throwable)
        {
            IntegratedPlugin.CorePlugin!!.getMessageHandler().defaultMessage("Some of classes caused error. Check reason:")
            e.printStackTrace()
            return "NULL"
        }
    }

    open fun getDocumentCommand(handlePlugin : IntegratedPlugin? = this.getPlugin(), findPattern : String = "(help|document|\\?)") : RuskitCommand<*>?
    {
        for(c in this.getChildCommands())
        {
            if(c is Document && c.command.matches(Regex(findPattern)))
                return c
        }
        return null
    }

    protected open fun executeDocument(target: CommandSender, handleInstance : RuskitCommand<*>) : Any?
    {
        val document = this.getDocumentCommand()
        return if(document == null)
        {
            this.getPlugin()!!.getMessageHandler().defaultMessage("&cSorry, There's no provided document or description.")
            false
        }
        else
        {
            this.getDocumentCommand()!!.perform(target, 0, ArrayList(), handleInstance)
        }
    }

    internal fun hasPermission(target: CommandSender) : Boolean
    {
        val permission = this.getRelativePermission()!!
        if(target.isOp)
        {
            if(!this.defaultOP)
            {
                return target.hasPermission(permission.getPermissionName())
            }
            return true
        }
        else
        {
            if(!this.defaultUser)
            {
                return target.hasPermission(permission.getPermissionName())
            }
            return true
        }
    }


    @Suppress("UNCHECKED_CAST")
    internal fun execute(target: CommandSender, argv: ArrayList<String>, handleInstance : Any? = null) : Any?
    {
        // Get the current message handler.
        val messageHandler = this.getPlugin()!!.getMessageHandler()

        // Get current command & Add description.
        val currentTreeCommand = this.getCurrentCommand(target)

        // Check if the argument value exists.
        // If there is no argument value, it is very likely to recognize that the command is executed.
        if (argv.isEmpty()) {
            val checksum = this.parameterBasicCheckResult(this.params, argv)
            when (checksum) {

                // checksum = 0xFFFFFFFF -> require argument at least one.
                // checksum = 0xFFFFFFFE -> parameter is empty, but sender tried to ether the arguments.
                // The return value 0xFFFFFFFE doesn't exist this method, Because parameter is originally nothing.
                -1 -> {
                    messageHandler.defaultMessage("&6It requires the arguments value at least.", target)
                    messageHandler.defaultMessage(StringUtility.WithIndex("&cRequired parameter:&f {0}", this.params[0].getName()), target)
                    messageHandler.defaultMessage(StringUtility.WithIndex("&cNeed a Help? /{0} {1}", currentTreeCommand.rawMessage(), "? | help"), target)
                    return false
                }
                else -> {
                    return if(RuskitCommandBase.IsCommandImplemented(this)) {
                        var hInstance = handleInstance
                        if(hInstance == null)
                            hInstance = this

                        val event = RuskitCommandEvent(target, this, argv, hInstance)
                        event.run()

                        if(! event.isCancelled) {
                            return if(this.hasPermission(target))
                                this.perform(event.sender, 0, event.argv, event.handleInstance)
                            else {
                                messageHandler.defaultMessage(this.permissionMessage!!.apply().rawMessage(), target)
                                false
                            }
                        }
                        else
                            null
                    } else {
                        this.executeDocument(target, this)
                    }
                }
            }
        }
        // Separate the next command with the actual argument through the argument value.
        else {

            // If there is at least one parameter value, it is necessary to distinguish the option value from the actual value.
            if(this.hasParameter())
            {
                // What values in the HashMap variable?
                // There are following pairs:
                // Key -> Parameter name
                // Value > The value of the parameter name
                val argumentsMap : HashMap<String, Any> = HashMap()
                for((index, value) in argv.withIndex()) argumentsMap[this.params[index].getName()] = value

                when(this.parameterMode)
                {
                    Parameter.Base.ARGUMENTS_KEYWORD_BASED -> {
                        // not support yet.
                        // val option = this.getArgumentsKeywordBased(argv)
                        // return this.perform(target, argv.size, argv, option)
                    }

                    Parameter.Base.PARAM_NAME_BASED -> {

                        if(argv.size > this.params.size)
                        {
                            // Exceeded arguments size. You need change arguments type.
                            messageHandler.defaultMessage("&6Exceeded the parameter value(s).", target)
                            messageHandler.defaultMessage(StringUtility.WithIndex("&cExceeded from:&f {0}...", argv[this.params.size]), target)
                            messageHandler.defaultMessage(StringUtility.WithIndex("&cNeed a Help? /{0} {1}", currentTreeCommand.rawMessage(), "? | help"), target)
                            return false
                        }

                        for(param in params)
                        {
                            if(param.isRequirement() && !argumentsMap.containsKey(param.getName()))
                            {
                                // required argument's value is missing.
                                messageHandler.defaultMessage("required argument's value is missing.", target)
                                messageHandler.defaultMessage(StringUtility.WithIndex("&cNeed a Help? /{0} {1}", currentTreeCommand.rawMessage(), "? | help"), target)
                                return false
                            }
                        }

                        var hInstance : Any? = handleInstance
                        if(hInstance == null) hInstance = this

                        val event = RuskitCommandEvent(target, this, argv, hInstance)
                        event.run()
                        return if(! event.isCancelled)
                            return if(this.hasPermission(target))
                                this.perform(event.sender, 0, event.argv, event.handleInstance)
                            else {
                                messageHandler.defaultMessage(this.permissionMessage!!.apply().rawMessage(), target)
                                false
                            }
                        else
                            null
                    }
                }
            }
            // The absence of a parameter to specify means to execute the command.
            // The first argument value will be the next command (or alias), and the second argument
            // value will be the argument value of the next command.
            else
            {
                for(c in this.child)
                {
                    // If finds the argument value equals one of child command or alias
                    if (c.getCommand().equals(argv[0], true) || c.getAlias().contains(argv[0])) {
                        var hInstance = handleInstance
                        if (hInstance == null) hInstance = this
                        return c.execute(target, this.receiveArguments(argv), hInstance)
                    }
                }
                messageHandler.defaultMessage(StringUtility.WithIndex("&eUnknown command: {0}", currentTreeCommand.rawMessage()), target)
                messageHandler.defaultMessage(StringUtility.WithIndex("&cNeed a Help? /{0} {1}", currentTreeCommand.rawMessage(), "? | help"), target)
                return false
            }
        }
        return false
    }

    fun getArgumentsKeywordBased(argv : ArrayList<String>) : Map<String, String>
    {
        val argumentsOption : HashMap<String, String> = HashMap()
        for((index, argument) in argv.withIndex())
        {
            if(StringUtility.isArgumentOption(argument))
            {
                if(StringUtility.isArgumentOption(argv[index + 1])) {
                    // warning!!
                    // Where is this argument value?
                    // Then, check the option whether the argument don't need or not.
                }

                if(argumentsOption.containsKey(StringUtility.getArgumentOptionName(argument))) {
                    // warning!!
                    // Already used argument option {option}. overwrite.
                }

                argumentsOption[StringUtility.getArgumentOptionName(argument)] = argv[index + 1]
            }
            else
            {
                try
                {
                    if(index > 0 && StringUtility.isArgumentOption(argv[index - 1]))
                    {
                        argumentsOption[StringUtility.getArgumentOptionName(argv[index - 1])] = argument
                    }
                }
                catch(e : IndexOutOfBoundsException)
                {
                    // warning!
                    // Where is argument option first?
                    continue
                }
            }
        }
        return argumentsOption
    }


    private fun <E> receiveArguments(argv : ArrayList<E>) : ArrayList<E>
    {
        argv.removeAt(0)
        return argv
    }

    private fun parameterBasicCheckResult(p : ArrayList<Parameter>, argv: List<String>) : Int
    {
        when(argv.size)
        {
            0 -> {
                // require argument at least one.
                if(p.isNotEmpty() && p[0].isRequirement()) return -1
            }
            else -> {
                // parameter is empty, but sender tried to ether the arguments.
                if(p.isEmpty()) return -2
                else
                {

                }

            }
        }
        return 0
    }

    @Suppress("UNCHECKED_CAST")
    private fun <E> performArguments(argv : E) : E
    {
        return if(argv is List<*>)
        {
            val a0 = Arrays.asList(argv)
            a0.remove(0)
            a0 as E
        }
        else argv
    }

    private val command: String

    @Suppress("MemberVisibilityCanBePrivate")
    fun getCommand() : String = command

    /**
     * Perform the code.
     * @param sender The sender who response the command from server
     * @param argc The arguments size, It sames `argv.size`
     * @param argv The command arguments
     * @param handleInstance The command arguments which are input the value using keyword base style
     * @return the specific value of customized
     */
    @NotImplemented("Not implemented. Make sure override this method")
    override fun perform(sender: CommandSender, argc: Int, argv: List<String>?, handleInstance: Any?): Any?
    {
        throw NotImplementedError()
    }

    internal fun relativeCommands(isMain : Boolean = false, split : Char = ',') : String
    {
        var str = ""
        if(!isMain)
        {
            if (this.alias.isEmpty()) return ""
            for ((index, alia) in this.alias.withIndex())
            {
                str = "$str$alia"
                if (this.alias.size != index + 1)
                {
                    str = "$str$split"
                }
            }
        }
        else
        {
            var command : RuskitCommand<*> = this
            while(true)
            {
                str += command.getCommand()
                if(command.isRoot()) break
                else {  str += ""; command = command.getParentCommand()!! }
            }
        }
        return str
    }

    /**
     * Decides whether to activate the command help mode.
     * If perform is not implemented, the command help will be printed if document mode was enabled.
     * This mode is very recommended.
     * If there is at least child commands, The help documention is printed when you enter a command
     * that is not registered.
     */
    private var documentMode: Boolean = true

    /**
     *
     */
    fun setDocumentMode(enabled: Boolean) { this.documentMode = enabled }

    /**
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun documentModeEnabled(): Boolean = this.documentMode

    /**
     *
     */
    private var parent : RuskitCommand<*>? = null

    /**
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getParentCommand() : RuskitCommand<*>? = this.parent

    /**
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun isRoot() : Boolean = this.getParentCommand() == null

    /**
     *
     */
    fun isFinal() : Boolean = this.isRoot() && this.child.isEmpty()

    /**
     *
     */
    private val child: ArrayList<RuskitCommand<*>> = ArrayList()

    /**
     *
     */
    fun addChildCommands(vararg args: Class<in RuskitCommand<*>>) {
        this.addCommandFrameworks(args, this.child)
    }

    /**
     *
     */
    fun addChildCommands(vararg args: RuskitCommand<*>) {
        this.addCommandFrameworks(args, this.child)
    }

    /**
     *
     */
    fun getChildCommands(): Collection<RuskitCommand<*>> = Collections.unmodifiableList(child)

    /**
     *
     */
    private val externalCommand: ArrayList<RuskitCommand<*>> = ArrayList()

    /**
     *
     */
    fun addExternalCommands(vararg args: Class<in RuskitCommand<*>>) {
        this.addCommandFrameworks(args, this.externalCommand)
    }

    /**
     *
     */
    fun addExternalCommands(vararg args: RuskitCommand<*>) {
        this.addCommandFrameworks(args, this.externalCommand)
    }

    /**
     *
     */
    fun getExternalCommands(): Collection<RuskitCommand<*>> = Collections.unmodifiableList(externalCommand)

    /**
     *
     */
    private fun addCommandFrameworks(args: Array<out RuskitCommand<*>>, target: ArrayList<RuskitCommand<*>>)
    {
        for(c in args) c.parent = this
        target.addAll(args)
    }

    /**
     *
     */
    private fun addCommandFrameworks(args: Array<out Class<in RuskitCommand<*>>>, target: ArrayList<RuskitCommand<*>>) {
        for (c in args) {
            try {
                val constructor = c.getConstructor()
                val instance: RuskitCommand<*> = constructor.newInstance() as RuskitCommand<*>
                instance.parent = this
                target.add(instance)
            } catch (e: ClassCastException) {
                e.printStackTrace()
            } catch (e2: NoSuchFieldException) {
                e2.printStackTrace()
            }
        }
    }

    /**
     *
     */
    private val alias: ArrayList<String> = ArrayList()

    /**
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getAlias(): List<String> = this.alias

    /**
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun addAlias(a: Array<out String>) { this.alias.addAll(a) }

    /**
     *
     */
    fun addAlias(a: Collection<String>) { this.alias.addAll(a) }

    /**
     *
     */
    private var commandDescription: FormatDescription = FormatDescription("No description.")

    /**
     *
     */
    fun setCommandDescription(s: String) { val f = FormatDescription(s); this.commandDescription = f }

    /**
     *
     */
    fun setCommandDescription(fd: FormatDescription) { this.commandDescription = fd }

    fun getCommandDescription() : FormatDescription = this.commandDescription

    /**
     *
     */
    private var prefix: FormatDescription = FormatDescription("[prefix]")

    /**
     *
     */
    private var prefixIncluded: Boolean = false

    /**
     *
     */
    fun setPrefixIncluded(b: Boolean) { this.prefixIncluded = b }

    /**
     *
     */
    fun setPrefix(x: String) { this.prefix = FormatDescription(x)
    }

    /**
     *
     */
    fun setPrefix(x: FormatDescription) { this.prefix = x }

    /**
     *
     */
    fun getPrefix(): FormatDescription = this.prefix

    /**
     *
     */
    private var parameterMode: Parameter.Base = Parameter.Base.PARAM_NAME_BASED

    /**
     *
     */
    fun setParameterMode(hex: Parameter.Base) { this.parameterMode = hex }

    /**
     *
     */
    fun getParameterMode(): Parameter.Base = this.parameterMode

    /**
     * This is the permission to use this command. The plugin value is the same as the value
     * of the main command.
     */
    private var permission: Permission? = null

    /**
     * Determines whether the parent command class inherits its Permission value.
     * If the value is not set correctly, the value of permission name may not be correct.
     * Assume that the following class is created on the server:
     *
     * <pre><code>
     * Generated instances = PreParentCommand, ParentCommand, TargetCommand
     * PreParentCommand.isExtendablePermission = true, permvalue = "maincommand", parent = null
     * ParentCommand.isExtendablePermission = true, permvalue = "subcommand", parent = PreParentCommand
     * TargetCommand.isExtendablePermission = true, permvalue = "foo", parent = ParentCommand
     * </code></pre>
     *
     * Suppose the code calls getRelativePermission() on TargetCommand, it's permission value is
     * "maincommand.subcommand.foo". However, let's assume that isExtendablePermission equals `false`.
     * If the code tries to get the Permission value from the TargetCommand, the ParentCommand no longer
     * loads the permssion value of its parent class. Therefore, the value is `"subcommand.foo"`.
     *
     * This is because ParentCommand does not allow inheritance permission, Invalid inheritance settings
     * can result in ambiguous permission values.
     *
     * The ultimate reason for using it is to use the full-based permission such as "completely.value"
     * without depends on the inheritance command.
     */
    private var commandExtendable : Boolean = false

    /**
     * Check that permission is allowed to inherit from the parent command.
     * @see commandExtendable
     * @return whether inheritance permission value is allow
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected fun isExtendablePermission() : Boolean = this.commandExtendable

    /**
     * Sets the value of the Permission.
     * If inheriting permissions from a parent class, Don't use values with the full-based such as "permission.full.value".
     * @param value The permission string value
     * @param extendable Whether to inherit the parent's permission value
     * @see commandExtendable
     */
    fun setPermission(value: String, extendable : Boolean = true) { this.permission = Permission(value, defaultOP, defaultUser); this.commandExtendable = extendable }

    /**
     * Sets the value of the Permission.
     * If inheriting permissions from a parent class, Don't use values with the full-based such as "permission.full.value".
     * @param p The permission you want to use
     * @param extendable Whether to inherit the parent's permission value
     * @see commandExtendable
     */
    fun setPermission(p: Permission, extendable : Boolean = true) { this.permission = p; this.commandExtendable = extendable }

    /**
     * Gets only this permission value, regardless of whether the parent has inherited permissions.
     * It doesn't get relative permission values.
     * @return The permission regardless of the parent command
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getPermission() : Permission? { return this.permission }

    /**
     * Gets the actual permission value that determines whether the parent class has inherited permissions.
     * @return The relative permission
     * @throws PermissionPolicyException throws an exception when the part
     * of permission'sextenable mode is incorrect
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getRelativePermission() : Permission?
    {
        if(!isExtendablePermission() || this.isRoot()) return this.permission
        var p2 = this.parent
        while(p2 != null)
        {
            if(this.isExtendablePermission())
            {
                p2.getPermission() ?: throw PermissionPolicyException("The parent command's permission is null. " +
                                "Turn off extendable mode if you want to use this permission only.")

                if(p2.getPermission()!!.getPermissionName().isEmpty())
                {
                    throw PermissionPolicyException("The parent command's permission's value is empty. " +
                            "Turn off extendable mode if you want to use this permission only.")
                }
            }
            p2 = p2.getParentCommand()
        }

        val p = this.parent
        var permissionValue : String = this.permission!!.getPermissionName()

        permissionValue = "${p!!.getRelativePermission()!!.getPermissionName()}.$permissionValue"
        return Permission(permissionValue, this.defaultOP, this.defaultUser)
    }

    /**
     * Determines whether admin have authority by plugin for this command.
     */
    private var defaultOP : Boolean = true

    /**
     * Check the value the admin have this permission by plugin.
     * @return whether the admin have the permission
     */
    fun isDefaultOP() : Boolean = this.defaultOP

    /**
     * Set the value whether admin have authority by plugin for this command.
     * The functionality may not work because it depends on the value of the parent class.
     */
    fun setDefaultOP(enabled : Boolean) { this.defaultOP = enabled }

    /**
     *  Determines whether a normal user have authority by plugin for this command.
     */
    private var defaultUser : Boolean = false

    /**
     * Set the value whether a normal use have authority by plugin for this command.
     * The functionality may not work because it depends on the value of the parent class.
     */
    fun setDefaultUser(enabled : Boolean) { this.defaultUser = enabled }

    /**
     * Check the value a normal user have this permission by plugin.
     * @return whether a normal user have the permission
     */
    fun isDefaultUser() : Boolean = this.defaultUser

    /**
     *
     */
    private val params: ArrayList<Parameter> = ArrayList()

    fun getParameters() : List<Parameter> = this.params


    /**
     *
     */
    protected fun addParameter(vararg p: Parameter) { this.params.addAll(p) }

    /**
     *
     */
    protected fun addParameter(p: Parameter) { this.params.add(p) }

    /**
     *
     */
    protected fun setParameter(index: Int, p: Parameter)
    {
        if (index - 1 < 0 || this.params.size <= index)
            throw IndexOutOfBoundsException("Out of index range: $index")
        this.params[index] = p
    }

    /**
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun hasParameter() : Boolean = !this.params.isEmpty()

    fun sortCommand(alphabet: CommandOrder)
    {
        CommandUtility.sortCommand(alphabet, this.child)
    }
}
