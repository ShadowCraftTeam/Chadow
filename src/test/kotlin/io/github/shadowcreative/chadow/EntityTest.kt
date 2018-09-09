package io.github.shadowcreative.chadow

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.github.shadowcreative.eunit.EntityUnit
import io.github.shadowcreative.eunit.EntityUnitCollection
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AnotherVirtualEntityList : EntityUnitCollection<VirtualEntity>()
class AnotherVirtualEntity : EntityUnit<AnotherVirtualEntity>()
{
    internal val value : VirtualEntity = VirtualEntity()
    internal val value2 : String = "Visualize Entity De/Serialized available"
}

// There's nothing ability, It just uses collection for VirtualEntity.
class VirtualEntityList : EntityUnitCollection<VirtualEntity>()
class VirtualEntity : EntityUnit<VirtualEntity>()
{
    internal var test0 : String = "Hello world"

    internal var test1 : Int = 1000

    internal var test2: Array<Double> = arrayOf(0.1, 22.4, -199.3419841)
}

class EntityTest
{
    // @After
    fun testFunction2()
    {
        val anotherVirtualEntity = AnotherVirtualEntity()
        val serializedString = anotherVirtualEntity.toSerializeElements() as JsonObject
        print("Result Test: " + GsonBuilder().serializeNulls().setPrettyPrinting().create().toJson(serializedString))
    }

    // @Before
    fun init() {
        this.testCollection = VirtualEntityList().generate() as? VirtualEntityList
        this.testCollection2 = VirtualEntityList().generate() as? AnotherVirtualEntityList
        this.entity = VirtualEntity().create() as? VirtualEntity
    }

    // @Test
    fun testFunction()
    {
        Assert.assertNotNull(this.entity)
        Assert.assertNotNull(this.testCollection)
        val serializedString = this.entity!!.toSerializeElements() as JsonObject

        Assert.assertEquals("Hello world", serializedString.get("test0").asString)
        Assert.assertEquals(1000, serializedString.get("test1").asInt)

        val jsonArray = serializedString.getAsJsonArray("test2")

        Assert.assertArrayEquals(arrayOf(0.1, 22.4, -199.3419841), Array(3, fun(i : Int) : Double { return jsonArray[i].asDouble } ))

        val deserializeObject : VirtualEntity = EntityUnitCollection.deserialize(serializedString, VirtualEntity::class.java)!!
        Assert.assertEquals("Hello world", deserializeObject.test0)
        Assert.assertEquals(1000, deserializeObject.test1)
        Assert.assertArrayEquals(arrayOf(0.1, 22.4, -199.3419841), deserializeObject.test2)
    }

    private var entity : VirtualEntity? = null
    private var testCollection : VirtualEntityList? = null
    private var testCollection2 : AnotherVirtualEntityList? = null
}