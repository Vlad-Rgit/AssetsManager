package com.kazanneft.assetsmanager.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kazanneft.assetsmanager.API_ADDRESS
import com.kazanneft.assetsmanager.models.Asset
import com.kazanneft.assetsmanager.models.AssetGroup
import com.kazanneft.assetsmanager.models.Department
import com.kazanneft.assetsmanager.repos.BaseRepo
import com.kazanneft.assetsmanager.utils.*
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.time.LocalDate
import java.time.LocalDateTime

class AssetsViewModel: ViewModel() {

    /**
     * Change it to notify fragments about error
     */
    val responseErrorText = MutableLiveData<String>()

    /**
     * All assets from the database
     */
    val assets = MutableLiveData<List<Asset>>()

    /**
     * All departments from the database
     */
    val depratmetns = MutableLiveData<List<Department>>()


    /**
     * All assets group from the database
     */
    val assetsGroup = MutableLiveData<List<AssetGroup>>()

    /**
     * Here we store filtered assets from the assets collection
     */
    val filteredAssets = MutableLiveData<MutableList<Asset>>()


    val filter = Filter()


    init {
        viewModelScope.launch {

            val assetsTask = async { loadAssets() }
            val departmentsTask = async { loadDepartments() }
            val assetGroupsTask = async { loadAssetsGroup() }

            val assetsResult = assetsTask.await()
            assets.postValue(assetsResult)
            filteredAssets.postValue(assetsResult.toMutableList())
            depratmetns.postValue(departmentsTask.await())
            assetsGroup.postValue(assetGroupsTask.await())
        }
    }


    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val newAssets = async { loadAssets() }.await()
            assets.postValue(newAssets)
            filter.filter(newAssets)
        }
    }

    /**
     * This class holds current selections in filters and performs
     * filter with this selections on filteredAssets collection
     */
    inner class Filter {

        private var currentJob: Job? = null

        var assetSearch: String = ""
            set(value) {
                field = value
                filter()
            }

        var department: Department? = null
            set(value) {
                field = value
                filter()
            }

        var assetGroup: AssetGroup? = null
            set(value) {
                field = value
                filter()
            }

        var startDate: LocalDate? = null
            set(value) {
                field = value
                filter()
            }

        var endDate: LocalDate? = null
            set(value) {
                field = value
                filter()
            }

        /**
         * Filter assets with properties in this instance
         */
        fun filter(base: List<Asset> = assets.value!!) {

            currentJob?.cancel()

            currentJob = viewModelScope.launch(Dispatchers.Default) {

                filteredAssets.value?.clear()

                for(asset in base) {

                    var isValid = true

                    if(assetSearch.length >= 2 &&
                        !asset.assetName.contains(assetSearch) &&
                        !asset.assetSN.contains(assetSearch)) {
                        isValid = false
                    }

                    if(department != null &&
                        asset.departmentLocation.department != department) {
                        isValid = false
                    }

                    if(assetGroup != null &&
                        asset.assetGroup != assetGroup){
                        isValid = false
                    }


                        if(startDate != null && endDate != null) {

                            if(asset.warrantyDate == null)
                                isValid = false
                            else if(asset.warrantyDate!!.toLocalDate().isBefore(startDate))
                                isValid = false
                            else if(asset.warrantyDate!!.toLocalDate().isAfter(endDate))
                                isValid = false
                        }

                    if(isValid)
                        filteredAssets.value?.add(asset)
                }

                filteredAssets.postValue(filteredAssets.value)
            }
        }
    }
}