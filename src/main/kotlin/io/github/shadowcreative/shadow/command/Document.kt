package io.github.shadowcreative.shadow.command

import io.github.shadowcreative.shadow.command.misc.CommandOrder
import org.bukkit.command.CommandSender

interface Document {
    fun output(listener   : CommandSender,
               targetPage : Int,
               sizeOfLine : Int,
               rawType    : Boolean,
               order      : CommandOrder = CommandOrder.ALPHABET) : Any?
}
