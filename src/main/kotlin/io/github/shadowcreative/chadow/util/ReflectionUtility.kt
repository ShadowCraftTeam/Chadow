package io.github.shadowcreative.chadow.util

import io.github.shadowcreative.chadow.platform.code.NotImplemented
import java.lang.reflect.*

@Suppress("UNCHECKED_CAST")
object ReflectionUtility
{
    @Throws(NoSuchMethodException::class)
    fun getAnnotationDefaultValue(anno: Class<out Annotation>, fieldName: String): Any
    {
        var method: Method? = null
        method = anno.getDeclaredMethod(fieldName)
        assert(method != null)
        return method!!.defaultValue
    }

    @Throws(Exception::class)
    fun SetField(field: Field, newValue: Any)
    {
        field.isAccessible = true
        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
        field.set(null, newValue)
    }

    @Throws(Exception::class)
    fun SetField(field: Field, target : Any, newValue: Any)
    {
        field.isAccessible = true
        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
        field.set(target, newValue)
    }

    @Throws(Exception::class)
    fun SetField(fieldname: String, clazz : Class<*>, newValue: Any)
    {
        val field = clazz.getDeclaredField(fieldname)
        field.isAccessible = true
        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
        field.set(null, newValue)
    }

    fun GetField(clazz: Class<*>, name: String): Field?
    {
        try {
            val ret = clazz.getDeclaredField(name)
            ret.isAccessible = true
            return ret
        } catch (e: Exception) {
            return null
        }

    }

    @Suppress("UNCHECKED_CAST")
    fun <T> GetField(field: Field, o: Any): T
    {
        try
        {
            return field.get(o) as T
        }
        catch (e: Exception)
        {
            throw RuntimeException(e)
        }
    }

    fun IsImplemented(perform: Method) : Boolean
    {
        val anno : NotImplemented = perform.getAnnotation(NotImplemented::class.java) ?: return true
        return StringUtility.valueAssert(anno.message, fun(s: String): Boolean { return s.isEmpty() })
    }
    fun MethodFromClass(target: Class<*>, name: String, equalsIgnore : Boolean = true, onTargetOnly : Boolean = false): Method?
    {
        val met : Array<out Method> = if(onTargetOnly) target.methods else target.declaredMethods
        for(m in met) if(m.name.equals(name, equalsIgnore)) return m
        return null
    }

    fun <T> getInstance(clazz: Class<*>): T
    {
        return try {
            val get = GetMethod(clazz, "getInstance")
            get.invoke(null) as T ?: throw NullPointerException("The instance was null: $clazz")
        }
        catch (e : Exception) {
            val instance = clazz.constructors[0].newInstance()
            instance as T
        }
    }

    fun GetMethod(clazz: Class<*>, name: String, vararg parameterType: Class<*>): Method {
        try {
            val ret = clazz.getDeclaredMethod(name, *parameterType)
            MakeAccessible(ret)
            return ret
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    fun <T> InvokeMethod(method: Method, target: Any?, vararg arguments: Any): T {
        try {
            return method.invoke(target, *arguments) as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    fun <T> InvokeMethod(method: Method, target: Any, argument: Any): T {
        return InvokeMethod(method, target, argument)
    }

    fun <T> InvokeMethod(method: Method, target: Any?): T?
    {
        return InvokeMethod<Any>(method, target) as T
    }

    fun MakeAccessible(obj: Any) {
        try {
            when (obj) {
                is Method -> obj.isAccessible = true
                is Constructor<*> ->
                {
                    obj.setAccessible(true)

                }
                is Field -> {
                    val MODIFIER_FIELD = Field::class.java.getDeclaredField("modifiers")
                    MODIFIER_FIELD.isAccessible = true

                    MODIFIER_FIELD.setInt(obj, obj.modifiers and 0x00000010.inv())
                }
                else -> MakeAccessible(obj)
            }
        }
        catch (e: Exception)
        {
            throw RuntimeException(e)
        }

    }

    fun MakeAccessible(obj: AccessibleObject)
    {
        obj.isAccessible = true
    }

    inline fun <V, E> inlineNullCheck(value : V, entity : E, function : (V, E) -> Any?) : Boolean {
        val result : Any? = function(value, entity)
        return result != null
    }
}