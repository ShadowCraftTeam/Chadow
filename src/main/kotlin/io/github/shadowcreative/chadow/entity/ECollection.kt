package io.github.shadowcreative.chadow.entity

import io.github.shadowcreative.eunit.EntityUnitCollection

open class ECollection : EntityUnitCollection<Sample>()
{
    companion object {
        private val instance : ECollection = ECollection()
        @JvmStatic
        fun getInstance() : ECollection = instance
    }
}
