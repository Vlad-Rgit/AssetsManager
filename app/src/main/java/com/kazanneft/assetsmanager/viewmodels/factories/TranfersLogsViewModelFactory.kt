package com.kazanneft.assetsmanager.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kazanneft.assetsmanager.viewmodels.TransferLogsViewModel

class TranfersLogsViewModelFactory(val assetId: Long)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TransferLogsViewModel(assetId) as T
    }

}