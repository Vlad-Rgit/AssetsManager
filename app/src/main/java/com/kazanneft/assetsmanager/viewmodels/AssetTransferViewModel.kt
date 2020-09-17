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

class AssetTransferViewModel: ViewModel() {

    val assets = MutableLiveData<List<Asset>>()
    val departments = MutableLiveData<List<Department>>()
    val locations = MutableLiveData<List<Location>>()
    val departmentLocation = MutableLiveData<List<DepartmentLocation>>()
    val transfers = MutableLiveData<List<AssetTransfer>>()

    init {

        viewModelScope.launch(Dispatchers.IO) {

            val results = awaitAll(
                async { loadDepartments() },
                async { loadLocations() },
                async { loadDepartmentLocations() },
                async { loadAssets() },
                async { loadTransferLogs() }
            )

            transfers.postValue(results[4] as List<AssetTransfer>)
            assets.postValue(results[3] as List<Asset>)
            departmentLocation.postValue(results[2] as List<DepartmentLocation>)
            locations.postValue(results[1] as List<Location>)
            departments.postValue(results[0] as List<Department>)
        }
    }

    fun getDepartmentLocations(department: Department): List<Location> {

        val result = LinkedList<Location>()

        for(depLoc in departmentLocation.value!!) {
            if(depLoc.department?.id == department.id){
                result.add(depLoc.location!!)
            }
        }

        return result
    }

    fun submitTransfer(assetTransfer: AssetTransfer) {
        runBlocking(Dispatchers.IO) {
            val url = "$API_ADDRESS/asset/transfer"
            val json = Json.encodeToString(assetTransfer)
            Log.d("TransferAsset", postJson(url, json))
        }
    }

    fun findDepartmentLocation(department: Department, location: Location): DepartmentLocation? {

        for (dl in departmentLocation.value!!) {

            if(dl.department == null || dl.location == null)
                continue

            if(dl.department!!.id == department.id
                && dl.location!!.id == location.id)
                return dl
        }

        return null
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

            if(transfer.asset?.id == mAsset.id
                && transfer.fromDepartmentLocation!!.department?.id
                == mAsset.departmentLocation.department?.id) {

                val n = transfer.asset!!.assetSN.substring(
                    transfer.asset!!.assetSN.lastIndexOf('/')+1).toInt()

                return n
            }

            if(transfer.asset?.assetGroup!!.id == mAsset.assetGroup!!.id
                && transfer.fromDepartmentLocation?.department?.id
                    == mAsset.departmentLocation!!.department?.id) {

                val n = transfer.asset!!.assetSN.substring(
                    transfer.asset!!.assetSN.lastIndexOf('/')+1).toInt()

                if(n > maxNID) {
                    maxNID = n
                }
            }
        }

        return ++maxNID
    }

}