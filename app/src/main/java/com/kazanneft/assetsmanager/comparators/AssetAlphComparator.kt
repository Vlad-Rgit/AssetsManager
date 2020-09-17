package com.kazanneft.assetsmanager.comparators

import com.kazanneft.assetsmanager.models.Asset

class AssetAlphComparator: Comparator<Asset> {

    override fun compare(p0: Asset?, p1: Asset?): Int {

        if(p0 == null ||
            p1 == null){
            return 0
        }

        return p0.assetName.compareTo(p1.assetName)
    }

}