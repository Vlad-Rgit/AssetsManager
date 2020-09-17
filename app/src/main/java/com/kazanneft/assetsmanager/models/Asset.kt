package com.kazanneft.assetsmanager.models

import android.os.Parcel
import android.os.Parcelable
import com.kazanneft.assetsmanager.serializers.DateJsonSerializer
import com.kazanneft.assetsmanager.utils.readLocalDateTime
import com.kazanneft.assetsmanager.utils.writeLocalDateTime
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Asset(
    var id: Long = 0,
    var assetSN: String = "",
    var assetName: String = "",
    var description: String = "",
    var assetGroup: AssetGroup? = null,
    var employee: Employee? = null,
    var departmentLocation: DepartmentLocation = DepartmentLocation(),
    @Serializable(with = DateJsonSerializer::class)
    var warrantyDate: LocalDateTime? = null
): AreItemsSameComparator<Asset>, Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(AssetGroup::class.java.classLoader),
        parcel.readParcelable(Employee::class.java.classLoader),
        parcel.readParcelable(DepartmentLocation::class.java.classLoader)!!,
        parcel.readLocalDateTime(),
    )

    override fun areItemsSame(item: Asset): Boolean {
        return this.id == item.id
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(assetSN)
        parcel.writeString(assetName)
        parcel.writeString(description)
        parcel.writeParcelable(assetGroup, flags)
        parcel.writeParcelable(employee, flags)
        parcel.writeParcelable(departmentLocation, flags)
        parcel.writeLocalDateTime(warrantyDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Asset> {
        override fun createFromParcel(parcel: Parcel): Asset {
            return Asset(parcel)
        }

        override fun newArray(size: Int): Array<Asset?> {
            return arrayOfNulls(size)
        }
    }
}