package io.github.shadowcreative.chadow.config

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import io.github.shadowcreative.chadow.engine.RuskitThread
import io.github.shadowcreative.chadow.event.config.SynchronizeReaderEvent
import io.github.shadowcreative.chadow.plugin.IntegratedPlugin
import io.github.shadowcreative.chadow.util.Algorithm
import java.io.*
import java.nio.charset.Charset
import java.security.NoSuchAlgorithmException

abstract class SynchronizeReader<E> : RuskitThread
{
    companion object
    {
        private val hREADER : Multimap<IntegratedPlugin, SynchronizeReader<*>> = ArrayListMultimap.create()
        @Synchronized fun RegisterHandledReader() : Multimap<IntegratedPlugin, SynchronizeReader<*>> = hREADER
    }

    constructor(file : File) : super()
    {
        this.file = file
    }

    constructor(filename : String) : this(File(filename))

    @Transient private var file: File?
    @Transient private var serverFolder : File? = IntegratedPlugin.CorePlugin!!.dataFolder
    @Transient private var lastHash : String = ""

    @Transient private var refreshMode : Boolean = true
    fun enableRefreshMode() : Boolean = this.refreshMode
    fun setRefreshMode(b : Boolean) { this.refreshMode = b }

    protected abstract fun serialize() : String

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