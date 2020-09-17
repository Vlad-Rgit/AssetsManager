package com.kazanneft.assetsmanager.comparators

import com.kazanneft.assetsmanager.models.ImageItem

class ImageAlphComparator: Comparator<ImageItem> {

    override fun compare(p0: ImageItem?, p1: ImageItem?): Int {

        return if(p0 == null || p1 == null)
            0
        else
           p0.name.compareTo(p1.name)
    }
}