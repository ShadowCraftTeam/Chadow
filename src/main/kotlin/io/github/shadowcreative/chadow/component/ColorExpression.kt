package io.github.shadowcreative.chadow.component

import net.md_5.bungee.api.ChatColor

enum class ColorExpression(val colorSet : String, val colorType : ChatColor)
{
    BLACK("&0", ChatColor.BLACK),
    DARK_BLUE("&1", ChatColor.DARK_BLUE),
    DARK_GREEN("&2", ChatColor.DARK_GREEN),
    DARK_AQUA("&3", ChatColor.DARK_AQUA),
    DARK_RED("&4", ChatColor.DARK_RED),
    DARK_PURPLE("&5", ChatColor.DARK_PURPLE),
    GOLD("&6", ChatColor.GOLD),
    GRAY("&7", ChatColor.GRAY),
    DARK_GRAY("&8", ChatColor.DARK_GRAY),
    BLUE("&9", ChatColor.BLUE),
    GREEN("&a", ChatColor.GREEN),
    AQUA("&b", ChatColor.AQUA),
    RED("&c", ChatColor.RED),
    LIGHT_PURPLE("&d", ChatColor.LIGHT_PURPLE),
    YELLOW("&e", ChatColor.YELLOW),
    WHITE("&f", ChatColor.WHITE),
    MAGIC("&r", ChatColor.MAGIC),
    BOLD("&l", ChatColor.BOLD),
    STRIKE("&m", ChatColor.STRIKETHROUGH),
    UNDERLINE("&n", ChatColor.UNDERLINE),
    ITALIC("&o", ChatColor.ITALIC),
    RESET("&r", ChatColor.RESET),
    UNKNOWN("&?", ChatColor.WHITE)
}

class Helper
{
    companion object
    {
        fun fromColorSet(colorSet: String) : ChatColor
        {
            for(v in ColorExpression.values())
            {
                if(v.colorSet == colorSet)
                    return v.colorType
            }
            return ChatColor.WHITE
        }

        fun getColorExpression(colorSet: String) : ColorExpression
        {
            for(v in ColorExpression.values())
            {
                if(v.colorSet == colorSet)
                    return v
            }
            return ColorExpression.UNKNOWN
        }

        fun contains(colorSet: String) : Boolean
        {
            for(v in ColorExpression.values())
            {
                if(v.colorSet == colorSet)
                    return true
            }
            return false
        }

        fun containsString(s: String) : Boolean
        {
            if(s == "") return false
            for(v in ColorExpression.values())
            {
                if(s != s.replace(v.colorSet, ""))
                    return true
            }
            return false
        }
    }
}