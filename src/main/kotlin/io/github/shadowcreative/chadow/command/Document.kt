package io.github.shadowcreative.chadow.command

import io.github.shadowcreative.chadow.command.misc.CommandOrder
import org.bukkit.command.CommandSender

interface Document {
    fun output(listener   : CommandSender,
               targetPage : Int,
               sizeOfLine : Int,
               rawType    : Boolean,
               order      : CommandOrder = CommandOrder.ALPHABET) : Any?
}
