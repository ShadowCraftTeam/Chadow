package io.github.shadowcreative.shadow.platform

import io.github.shadowcreative.shadow.Handle
import java.lang.reflect.ParameterizedType

/**
 * GenericInstance is a sub-class that can refer to an instance of a superclass type.
 * It can be referenced from outside without creating a separate instance.
 * @param C The type of inherited classes
 */
abstract class GenericInstance<C> : Handle
{
    @Suppress("UNCHECKED_CAST")
    private var persistentClass : Class<C>? = null
    fun getPersistentClass() : Class<C>? = this.persistentClass

    protected var genericInstance : C? = null
    fun getSuperclassInstance() : C? = this.genericInstance

    init {
        val parameterizedType = (javaClass.genericSuperclass as? ParameterizedType)
        if(parameterizedType != null) {
            this.persistentClass = parameterizedType.actualTypeArguments[0] as? Class<C>
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onInit(handleInstance: Any?): Any?
    {
        this.genericInstance = handleInstance as? C?
        return this.genericInstance
    }
}