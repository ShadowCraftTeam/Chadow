package io.github.shadowcreative.chadow.entity.collection

import io.github.shadowcreative.chadow.entity.AbstractInventory
import io.github.shadowcreative.eunit.EntityUnitCollection

class AbstractInventoryCollection : EntityUnitCollection<AbstractInventory>()
{
    init
    {
        this.addIdentity("uuid", "owner")
    }
}