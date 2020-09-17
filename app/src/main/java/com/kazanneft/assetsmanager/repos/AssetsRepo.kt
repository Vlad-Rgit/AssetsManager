package com.kazanneft.assetsmanager.repos

import com.kazanneft.assetsmanager.API_ADDRESS
import com.kazanneft.assetsmanager.models.Asset
import com.kazanneft.assetsmanager.utils.makeStringRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

open class BaseRepo: AbstractRepo() {

    override suspend fun <T> getAll(): MutableList<T> {

        return withContext(Dispatchers.IO) {

            val json = makeStringRequest("$API_ADDRESS/assets")

            if (json == null) {
                mutableListOf()
            } else {
                parseAsJson(json)
            }
        }
    }

    protected suspend inline fun <reified T> parseAsJson(json: String)
        = Json.decodeFromString<T>(json)

}