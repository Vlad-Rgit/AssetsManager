package com.kazanneft.assetsmanager.utils

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream

fun readFileBytes(path: String): ByteArray {

    val file = File(path)
    val length = file.length()
    val bytes = ByteArray(length.toInt())

    try{
        val stream = BufferedInputStream(FileInputStream(file))
        stream.read(bytes, 0, bytes.size)
        stream.close()
        return bytes
    }
    catch (ex: Exception) {
        ex.printStackTrace()
        return byteArrayOf()
    }
}