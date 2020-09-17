package com.kazanneft.assetsmanager.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
data class AssetGroup(
    val id: Long = 0,
    val name: String = ""
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!
    )


    override fun toString(): String {
        return name
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AssetGroup> {
        override fun createFromParcel(parcel: Parcel): AssetGroup {
            return AssetGroup(parcel)
        }

        override fun newArray(size: Int): Array<AssetGroup?> {
            return arrayOfNulls(size)
        }
    }
}