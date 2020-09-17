package com.kazanneft.assetsmanager.utils

import com.kazanneft.assetsmanager.API_ADDRESS
import com.kazanneft.assetsmanager.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.collections.ArrayList

/**
 * Load all departments from the api
 */
suspend fun loadDepartments(): List<Department> {

    return withContext(Dispatchers.IO) {

        val url = "$API_ADDRESS/departments"
        val json = makeStringRequest(url)

        if (json == null) {
            return@withContext emptyList()
        }

        Json.decodeFromString(json)
    }
}

/**
 * Load asset groups from the server
 */
suspend fun loadAssetsGroup(): List<AssetGroup> {
    return withContext(Dispatchers.IO) {
        val url = "$API_ADDRESS/assetGroups"
        val json = makeStringRequest(url)

        if (json == null) {
            return@withContext emptyList()
        }

        Json.decodeFromString(json)
    }
}

/**
 * Load locations from the server
 */
suspend fun loadLocations(): List<Location> {
    return withContext(Dispatchers.IO) {
        val url = "$API_ADDRESS/locations"
        val json = makeStringRequest(url)

        if (json == null) {
            return@withContext emptyList()
        }

        Json.decodeFromString(json)
    }
}


/**
 * Load employees from the server
 */
suspend fun loadEmployee(): List<Employee> {
    return withContext(Dispatchers.IO) {
        val url = "$API_ADDRESS/employees"
        val json = makeStringRequest(url)

        if (json == null) {
            return@withContext emptyList()
        }

        Json.decodeFromString(json)
    }
}

/**
 * Load all assets from the server
 */
suspend fun loadAssets(): List<Asset> {

    return withContext(Dispatchers.IO) {

        val url = "$API_ADDRESS/assets"

        val json = makeStringRequest(url)

        if (json == null) {
            return@withContext emptyList<Asset>()
        }

        Json.decodeFromString<MutableList<Asset>>(json)
    }
}

/**
 * Load all department locations
 */
suspend fun loadDepartmentLocations(): List<DepartmentLocation> {

    return withContext(Dispatchers.IO) {

        val url = "$API_ADDRESS/departmentLocations"

        val json = makeStringRequest(url)

        if (json == null) {
            return@withContext emptyList()
        }

        Json.decodeFromString(json)
    }
}

/**
 * Get amount of photos of asset
 */
suspend fun assetPhotosCount(assetId: Long): Int {
    return withContext(Dispatchers.IO) {
        val url = "$API_ADDRESS/asset/photos/$assetId"
        makeStringRequest(url)?.toInt() ?: 0
    }
}


/**
 * Load all photos from asset
 */

suspend fun loadPhotos(assetId: Long): List<ByteArray> {

    return withContext(Dispatchers.IO) {

        val count = assetPhotosCount(assetId)

        if(count == 0) {
            return@withContext emptyList()
        }

        val result = ArrayList<ByteArray>(count)

        for (i in 0 until count) {
            val url = "$API_ADDRESS/asset/photo/$assetId/$i"
            result.add(getBytes(url))
        }

        result
    }
}


/**
 * Load transfer for asset
 */
suspend fun loadTransferLogs(assetId: Long): List<AssetTransfer> {

    return withContext(Dispatchers.IO) {

        val url = "$API_ADDRESS/transfers/$assetId"

        val json = makeStringRequest(url)
            ?: return@withContext emptyList()

        Json.decodeFromString(json)
    }

}


/**
 * Load all transfer logs
 */
suspend fun loadTransferLogs(): List<AssetTransfer> {

    return withContext(Dispatchers.IO) {

        val url = "$API_ADDRESS/transfers"

        val json = makeStringRequest(url)
            ?: return@withContext emptyList()

        Json.decodeFromString(json)
    }

}


