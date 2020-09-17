package com.kazanneft.assetsmanager.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL


fun createHttpRequest(url: String): HttpURLConnection {
    return URL(url).openConnection()
            as HttpURLConnection
}

suspend fun makeStringRequest(url: String): String? {
    val request = createHttpRequest(url)
    val responseCode = request.responseCode
    if(isResponceSuccesful(responseCode)){
        return readStringResponse(request)
    }
    else{
        return null
    }
}

fun readStringResponse(request: HttpURLConnection): String {

    val streamReader =
        if(isResponceSuccesful(request.responseCode))
            request.inputStream.bufferedReader()
        else
            request.errorStream.bufferedReader()


    val responseBuilder = StringBuilder()
    var line: String? = streamReader.readLine()

    while(line != null){
        responseBuilder.append(line)
        line = streamReader.readLine()
    }

    return responseBuilder.toString()
}

fun isResponceSuccesful(responseCode: Int)
        = responseCode in 200..299


suspend fun postJson(url: String, json: String): String {

    return withContext(Dispatchers.IO) {

        val httpRequest = createHttpRequest(url)
        httpRequest.requestMethod = "POST"
        httpRequest.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        httpRequest.doOutput = true

        val writer = httpRequest.outputStream.bufferedWriter()
        writer.append(json)
        writer.close()

        return@withContext readStringResponse(httpRequest)
    }
}


suspend fun postBytes(url: String, bytes: ByteArray): String {

    return withContext(Dispatchers.IO) {

        val request = createHttpRequest(url)

        request.apply {
            requestMethod = "POST"
            doOutput = true
            doInput = true
            setRequestProperty("Content-Type", "octet-stream")
            setRequestProperty("Content-Length", "${bytes.size}")
            outputStream.write(bytes)
            outputStream.flush()
            outputStream.close()
        }

        return@withContext readStringResponse(request)
    }
}

suspend fun readResponseBytes(request: HttpURLConnection): ByteArray{

    val stream = request.inputStream
    val outputStream = ByteArrayOutputStream()

    var byte = stream.read()

    while(byte != -1){
        outputStream.write(byte)
        byte = stream.read()
    }

    return outputStream.toByteArray()
}

suspend fun getBytes(url: String): ByteArray {

    val request = createHttpRequest(url)

    request.apply {
        requestMethod = "GET"
        doInput = true
    }

    if(!isResponceSuccesful(request.responseCode)) {
        return byteArrayOf()
    }

    return readResponseBytes(request)
}


suspend fun loadMainPhoto(url: String): ByteArray {

    val request = createHttpRequest(url)
    request.requestMethod = "GET"

    request.doInput = true

    if(isResponceSuccesful(request.responseCode)) {
        return readResponseBytes(request)
    }
    else{
        return byteArrayOf()
    }
}


suspend fun putJson(url: String, json: String): String {

    val request = createHttpRequest(url)

    request.apply {
        requestMethod = "PUT"
        setRequestProperty("Content-Type", "application/json; charset=utf-8")
        doOutput = true
        val writer = outputStream.bufferedWriter(Charsets.UTF_8)
        writer.append(json)
        writer.flush()
        writer.close()
    }

    return readStringResponse(request)
}