package io.github.shadowcreative.chadow.util

import java.util.logging.Logger

class ChadowLogger : Logger("Chadow", "ChadowBundle")
{
    companion object
    {
        var DefaultLogger : ChadowLogger? = null
    }

    enum class Level
    {
        INFO,
        WARNING,
        DANGER,
        SYSTEM,
        MESSAGE
    }
}