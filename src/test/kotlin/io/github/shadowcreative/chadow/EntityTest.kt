package io.github.shadowcreative.chadow

import com.google.gson.Gson
import com.google.gson.JsonParser
import io.github.shadowcreative.eunit.EntityUnit
import io.github.shadowcreative.eunit.EntityUnitCollection
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VirtualEntity : EntityUnit<VirtualEntity>()
{
    internal var test0 : String = "Hello world"

    internal var test1 : Int = 1000

    internal var test2: Array<Double> = arrayOf(0.1, 22.4, -199.3419841)
}


class VirtualEntityList : EntityUnitCollection<VirtualEntity>()
{

}

class EntityTest
{
    @Before
    fun init() {
        this.testCollection = VirtualEntityList().generate() as? VirtualEntityList
        this.entity = VirtualEntity().create() as? VirtualEntity
    }

    @Test
    fun testFunction()
    {
        Assert.assertNotNull(this.entity)
        Assert.assertNotNull(this.testCollection)
        val serializedString = this.entity!!.toSerialize()
        print(serializedString.toString())

        val deserializedObject = EntityUnitCollection.deserialize(serializedString, VirtualEntity::class.java)
        print(deserializedObject!!.test0)
    }

    private var entity : VirtualEntity? = null
    private var testCollection : VirtualEntityList? = null
}