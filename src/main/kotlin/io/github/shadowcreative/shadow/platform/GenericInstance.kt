package io.github.shadowcreative.shadow.platform

import io.github.shadowcreative.shadow.Handle


/**
 * GenericInstance is a sub-class that can refer to an instance of a superclass type.
 * It can be referenced from outside without creating a separate instance.
 * @param C The type of inherited classes
 */
abstract class GenericInstance<C> : Handle
{
    protected var genericInstance : C? = null
    fun getSuperclassInstance() : C? = this.genericInstance

    private var genericType : C? = null
    fun getGenericType() : C? = this.genericType

    @Suppress("UNCHECKED_CAST")
    override fun onInit(handleInstance: Any?): Any?
    {
        this.genericInstance = handleInstance as C?
        //this.genericType = (this.genericInstance as ParameterizedType).actualTypeArguments[0] as? C
        return this.genericInstance
    }
}