package io.github.shadowcreative.chadow.entity.collection

import io.github.shadowcreative.chadow.entity.SimpleEntity
import io.github.shadowcreative.eunit.EntityUnitCollection

class SimpleEntityCollection : EntityUnitCollection<SimpleEntity>()
{
    init {
        this.addIdentity("test1")
    }
}