package io.github.shadowcreative.shadow.component

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer

abstract class JsonCompatibleSerializer<A> : JsonDeserializer<A>, JsonSerializer<A>