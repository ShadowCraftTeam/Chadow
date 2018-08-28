/*
Copyright (c) 2018 ruskonert
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package io.github.shadowcreative.shadow.platform.code

import io.github.shadowcreative.shadow.platform.GenericInstance
import io.github.shadowcreative.shadow.platform.ProgramComponent
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.*

abstract class TargetBuilder<E> protected constructor() : GenericInstance<TargetBuilder<E>>()
{
    @Suppress("UNCHECKED_CAST")
    val genericType: Class<E> = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<E>

    private val referenceFields = ArrayList<Field>()
    fun getReferenceFields(): List<Field> = this.referenceFields

    init
    {
        this.onInit(null)
    }

    override fun onInit(handleInstance: Any?): Any
    {
        super.onInit(handleInstance)
        for (field in this.genericInstance!!.javaClass.declaredFields)
        {
            field.isAccessible = true
            if (field.getAnnotation(Reference::class.java) == null) continue
            val reference = field.getAnnotation(Reference::class.java)

            val defaultPackageName = getAnnotationDefaultValue(Reference::class.java, "target") as String
            var packageName = reference.target

            if (!packageName.equals(defaultPackageName, ignoreCase = true))
                packageName = defaultPackageName + "." + reference.target

            val fieldName = reference.target
            try
            {

                if (fieldName.split("#".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray().size > 1)
                {
                    val referenceClazz = Class.forName(packageName)
                    val methodToField = referenceClazz.getDeclaredField(fieldName.split("#".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0])
                    val targetClazz = this.getFromComponents(methodToField)!!.javaClass
                    val referenceMethod = targetClazz.getMethod(fieldName.split("#".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1])
                    referenceMethod.toString()
                    field.set(this.genericInstance, referenceMethod.invoke(this.getFromComponents(methodToField)))
                }
                else
                {
                    val referenceClazz = Class.forName(packageName)
                    val targetField = referenceClazz.getDeclaredField(fieldName)
                    targetField.isAccessible = true
                    val value = this.getFromComponents(targetField)
                    field.set(this.genericInstance, value)
                }
                this.referenceFields.add(field)
            } catch (e: InvocationTargetException) { /* error */ } catch(e: NoSuchFieldException) { /* error */    }
              catch (e: ClassNotFoundException) { /* error */    } catch (e: IllegalAccessException) { /* error */ }
              catch (e: NoSuchMethodException) { /* error */     }
        }
        return this
    }

    private fun getFromComponents(field: Field): Any? {
        field.isAccessible = true
        try {
            return field.get(ProgramComponent.getProgramComponents())
        }
        catch (e: IllegalArgumentException) {
            try {
                return field.get(ProgramComponent.getProgramComponents())
            }
            catch (e2: IllegalArgumentException) {
                //error
                return null
            }
            catch (e3: IllegalAccessException) {
                //error
            }
        }
        catch (e: IllegalAccessException) {
            //error
        }
        return null
    }

    companion object
    {
        fun getAnnotationDefaultValue(anno: Class<out Annotation>, fieldName: String): Any
        {
            var method: Method? = null
            try
            {
                method = anno.getDeclaredMethod(fieldName)
            }
            catch (e: NoSuchMethodException)
            {
                // error
            }
            assert(method != null)
            return method!!.defaultValue
        }
    }

}