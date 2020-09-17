package com.kazanneft.assetsmanager.viewmodels


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazanneft.assetsmanager.API_ADDRESS
import com.kazanneft.assetsmanager.models.*
import com.kazanneft.assetsmanager.utils.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class AssetDetailsViewModel: ViewModel() {

    val assets = MutableLiveData<List<Asset>>()
    val locations = MutableLiveData<List<Location>>()
    val departments = MutableLiveData<List<Department>>()
    val assetGroups = MutableLiveData<List<AssetGroup>>()
    val employees = MutableLiveData<List<Employee>>()
    val departmentLocations = MutableLiveData<List<DepartmentLocation>>()
    val transfers = MutableLiveData<List<AssetTransfer>>()

    init {

        viewModelScope.launch(Dispatchers.IO) {

            val assetsTask = async { loadAssets() }
            val locationsTask = async { loadLocations() }
            val departmentsTask = async { loadDepartments() }
            val assetGroupTask = async { loadAssetsGroup() }
            val employeesTask = async { loadEmployee() }
            val depLocationsTask = async { loadDepartmentLocations() }
            val transfersTask = async { loadTransferLogs() }

            val results = awaitAll(
                assetsTask, depLocationsTask, departmentsTask, locationsTask,
                assetGroupTask, employeesTask, transfersTask
            )

            withContext(Dispatchers.Main) {
                transfers.value = results[6] as List<AssetTransfer>
                assets.value = results[0] as List<Asset>
                departmentLocations.value = results[1] as List<DepartmentLocation>
                departments.value = results[2] as List<Department>
                locations.value = results[3] as List<Location>
                assetGroups.value = results[4] as List<AssetGroup>
                employees.value = results[5] as List<Employee>
            }
        }
    }


    fun findLocations(department: Department): List<Location> {

        if(departmentLocations.value == null)
            return emptyList()

        val result = LinkedList<Location>()

        for(depLoc in departmentLocations.value!!) {

            depLoc.department?.let {
                if(it.id == department.id &&
                        !result.contains(depLoc.location))
                {
                    depLoc.location?.let {
                        result.add(it)
                    }
                }
            }

        }

        return result
    }

    fun findAssetNID(mAsset: Asset): Int {

        var maxNID = 0

        for(asset in assets.value!!){

            if(asset.departmentLocation.department?.id
                == mAsset.departmentLocation.department?.id
                && asset.assetGroup?.id == mAsset.assetGroup!!.id){

                val n = asset.assetSN.substring(
                    asset.assetSN.lastIndexOf('/')+1
                ).toInt()

                if(n > maxNID) {
                    maxNID = n
                }
            }
        }


        for(transfer in transfers.value!!) {

            if(transfer.asset?.assetGroup!!.id == mAsset.assetGroup!!.id
                && transfer.fromDepartmentLocation?.department?.id
                     == mAsset.departmentLocation.department!!.id) {

                val n = transfer.asset!!.assetSN.substring(
                    transfer.asset!!.assetSN.lastIndexOf('/')+1).toInt()

                if(n > maxNID) {
                    maxNID = n
                }
            }
        }

        return ++maxNID
    }


    fun findDepartmentLocation(department: Department, location: Location): DepartmentLocation? {

        for (dl in departmentLocations.value!!) {

            if(dl.department == null || dl.location == null)
                continue

            if(dl.department!!.id == department.id
                && dl.location!!.id == location.id)
                return dl
        }

        return null
    }

    fun addAsset(asset: Asset, photos: List<ByteArray>) {

        runBlocking(Dispatchers.IO) {

            Log.d("Post Asset", "post starts")
            val url = "$API_ADDRESS/asset"

            val json = Json.encodeToString(asset)

            Log.d("Post Asset", json)

            asset.id = postJson(url, json).toLong()

            Log.d("Post Asset", asset.id.toString())

            uploadPhotos(asset, photos)
        }
    }

    private suspend fun uploadPhotos(
        asset: Asset,
        photos: List<ByteArray>
    ) {
        val photoUrl = "$API_ADDRESS/asset/photo/${asset.id}"

        for (photo in photos) {
            val response = postBytes(photoUrl, photo)
            Log.d("Post Asset", response)
        }
    }

    fun updateAsset(asset: Asset, photos: List<ByteArray>) {

        runBlocking(Dispatchers.IO) {

            val url = "$API_ADDRESS/asset"
            val json = Json.encodeToString(asset)

            Log.d("PutAsset", putJson(url, json))
            uploadPhotos(asset, photos)
        }
    }

    fun isValidAssetName(name: String, location: Location): Boolean {

        for(asset in assets.value!!) {
            if(asset.departmentLocation.location?.id == location.id
                && asset.assetName == name) {
                return false
            }
        }

        return true
    }

}