package io.github.shadowcreative.chadow.config

import io.github.shadowcreative.chadow.engine.RuntimeTaskScheduler
import io.github.shadowcreative.chadow.util.Algorithm
import java.io.*
import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger

/**
 * SynchronizeReader keeps track of String data and enables efficient data access through file I/O.
 */
abstract class SynchronizeReader : RuntimeTaskScheduler
{
    constructor(file : File) : super() { this.file = file }
    constructor(filename : String) : this(File(filename))

    // Determines the filename (or file type) to use when serializing the class to a file.
    // The location where this is stored is the data folder of the handled plugin.
    private var file: File
    fun getFile() : File = this.file

    fun getSubstantialPath() : File? {
        if(this.activePlugin == null) return null
        val dataFolder = File(this.activePlugin!!.dataFolder, "storedata")
        return File(dataFolder, activePlugin!!.name + "@" + this::class.java.name)
    }

    // Stores the hash value of the last read file.
    private var lastHash : String = "0"

    // Decide if yo u want to enable refresh mode.
    // Enabling this value is highly recommended for correct data access.
    private var refreshMode : Boolean = true
    fun enabledRefreshMode() : Boolean = this.refreshMode
    fun setRefreshMode(b : Boolean) { this.refreshMode = b }

    // Returns the serialized result value.
    protected abstract fun serialize() : String

    fun onDisk() : Boolean {
        if(this.getSubstantialPath() == null) return false
        return File(this.getSubstantialPath(), this.getFile().name).exists()
    }

    // Writes the serialized data to a file.
    protected fun serializeToFile(charset: String = "UTF-8") : Boolean
    {
        try {
            if(!this.file.name.endsWith(".json")) this.file = File(this.file.path + ".json")
            return if(this.hasActivePlugin()) {
                val dataFolder = getSubstantialPath()!!
                if(!dataFolder.exists()) dataFolder.mkdirs()
                val objectFile = File(dataFolder, this.file.path)
                if (!objectFile.exists()) objectFile.createNewFile()

                val outputStreamWriter = OutputStreamWriter(FileOutputStream(objectFile), Charset.forName(charset))
                outputStreamWriter.write(this.serialize())
                outputStreamWriter.close()
                this.lastHash = Algorithm.getSHA256file(objectFile.path)!!
                true
            }
            else {
                Logger.getGlobal().log(Level.WARNING, "The handled plugin could not be found")
                false
            }
        }
        catch(e : IOException) {
            e.printStackTrace()
            return false
        }
        catch(e : FileNotFoundException) {
            e.printStackTrace()
            return false
        }
    }
}