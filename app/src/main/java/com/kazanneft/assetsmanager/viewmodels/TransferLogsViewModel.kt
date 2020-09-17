package com.kazanneft.assetsmanager.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kazanneft.assetsmanager.models.AssetTransfer
import com.kazanneft.assetsmanager.utils.loadTransferLogs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TransferLogsViewModel(assetId: Long) : ViewModel() {

    val transfers = MutableLiveData<List<AssetTransfer>>()

    init {

        viewModelScope.launch(Dispatchers.IO) {

            transfers.postValue(
                async { loadTransferLogs(assetId) }.await()
            )

        }
    }

}