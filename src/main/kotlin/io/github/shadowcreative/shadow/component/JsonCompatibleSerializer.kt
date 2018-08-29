package io.github.shadowcreative.shadow.component

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import io.github.shadowcreative.shadow.platform.GenericInstance

abstract class JsonCompatibleSerializer<A> : GenericInstance<A>(), JsonDeserializer<A>, JsonSerializer<A>
{

}