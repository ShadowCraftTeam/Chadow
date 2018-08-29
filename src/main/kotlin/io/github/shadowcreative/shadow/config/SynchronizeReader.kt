package io.github.shadowcreative.shadow.config

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import io.github.shadowcreative.shadow.engine.RuskitThread
import io.github.shadowcreative.shadow.event.config.SynchronizeReaderEvent
import io.github.shadowcreative.shadow.plugin.IntegratedPlugin
import io.github.shadowcreative.shadow.util.Algorithm
import java.io.*
import java.nio.charset.Charset
import java.security.NoSuchAlgorithmException

abstract class SynchronizeReader<E>(target: File) : RuskitThread()
{
    companion object
    {
        private val hREADER : Multimap<IntegratedPlugin, SynchronizeReader<*>> = ArrayListMultimap.create()
        @Synchronized fun RegisterHandledReader() : Multimap<IntegratedPlugin, SynchronizeReader<*>> = hREADER
    }

    open fun getEntity(element : Any) : E?
    {
        return null
    }

    @Transient private var file: File? = target
    @Transient private var serverFolder : File? = IntegratedPlugin.CorePlugin!!.dataFolder
    @Transient private var lastHash : String = ""

    @Transient private var refreshMode : Boolean = true
    fun enableRefreshMode() : Boolean = this.refreshMode
    fun setRefreshMode(b : Boolean) { this.refreshMode = b }

    abstract fun serialize() : String

    open fun toDataSerialize() : Boolean
    {
        try
        {
            if (this.hasActivePlugin())
                this.serverFolder = File(serverFolder, this.activePlugin!!.name)

            val absoluteFile = File(serverFolder, file!!.path)
            if (!absoluteFile.exists())
                absoluteFile.createNewFile()
            val osw = OutputStreamWriter(FileOutputStream(absoluteFile), Charset.forName("UTF-8"))
            osw.write(this.serialize())
            osw.close()
            return true
        }
        catch(e : IOException)
        {
            return false
        }
        catch(e : FileNotFoundException)
        {
            return false
        }
    }

    private external fun onInit0(current : String) : List<String>

    override fun onInit(handleInstance: Any?): Any?
    {
        try
        {
            if(this.lastHash != "")
            {
                if(file != null && file!!.exists()) {
                    val hash = Algorithm.getSHA256file(file!!.path)!!
                    if (lastHash != hash) {
                        val readerEvent = SynchronizeReaderEvent(this)
                        readerEvent.insertCustomData("lastHash", this.lastHash)
                        this.lastHash = hash
                        readerEvent.run()
                    }
                }
                else
                {

                }
            }
            else
                this.lastHash = Algorithm.getSHA256file(file!!.path)!!
        }
        catch (e : NullPointerException) { }
        catch (e : NoSuchAlgorithmException) { }
        return true
    }

    override fun isEnabled(): Boolean {
        val multiMap = hREADER
        if(multiMap.containsKey(this.activePlugin)) {
            for(values in multiMap.get(this.activePlugin)) {
                if(values == this) {
                    return true
                }
            }
            return false
        }
        else return false
    }

    override fun setEnabled(active: Boolean)
    {
        super.setEnabled(active)
        if(active)
            hREADER.put(this.activePlugin, this)
        else
            hREADER.remove(this.activePlugin, this)
    }
}