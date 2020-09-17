package com.kazanneft.assetsmanager.comparators

import com.kazanneft.assetsmanager.models.AssetTransfer
import java.util.Comparator

class AssetTransferComparator: Comparator<AssetTransfer> {

    override fun compare(p0: AssetTransfer?, p1: AssetTransfer?): Int {

        if(p0!!.transferDate == null || p1!!.transferDate == null)
            return 0

        return p0.transferDate!!.compareTo(p1.transferDate)
    }
}