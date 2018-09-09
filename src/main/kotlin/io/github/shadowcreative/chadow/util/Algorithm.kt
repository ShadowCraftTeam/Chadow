package io.github.shadowcreative.chadow.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and
import java.io.FileInputStream
import java.io.IOException

object Algorithm
{
    @Throws(NoSuchAlgorithmException::class)
    fun getSHA256(str : String) : String?
    {
        val sha : String?
        val md = MessageDigest.getInstance("SHA-256")
        md.update(str.toByteArray())

        val byteData = md.digest()
        val sb = StringBuilder()
        val padding = 0xFF
        for(i in byteData.indices)
            sb.append(((byteData[i] and padding.toByte()) + 0x100).toString(16).substring(1))
        sha = sb.toString()
        return sha
    }

    @Throws(NoSuchAlgorithmException::class, IOException::class)
    fun getSHA256file(file : String) : String?
    {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val fis = FileInputStream(file)
        val data = ByteArray(1024)
        var read = fis.read(data)
        while(read != -1)
        {
            sha256.update(data, 0, read)
            read = fis.read(data)
        }
        val hashBytes = sha256.digest()
        val sb = StringBuffer()
        val padding = 0xFF
        for (i in hashBytes.indices) {
            sb.append(Integer.toString((hashBytes[i] and padding.toByte()) + 0x100, 16).substring(1))
        }
        return sb.toString()
    }
}