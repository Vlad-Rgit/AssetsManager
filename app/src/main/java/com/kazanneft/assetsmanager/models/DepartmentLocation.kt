package com.kazanneft.assetsmanager.models

import android.os.Parcel
import android.os.Parcelable
import com.kazanneft.assetsmanager.serializers.DateJsonSerializer
import com.kazanneft.assetsmanager.utils.readLocalDateTime
import com.kazanneft.assetsmanager.utils.writeLocalDateTime
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Serializable
data class DepartmentLocation(
    val id: Long = 0,
    var department: Department? = null,
    var location: Location? = null,
    @Serializable(with = DateJsonSerializer::class)
    var startDate: LocalDateTime? = null,
    @Serializable(with = DateJsonSerializer::class)
    var endDate: LocalDateTime? = null
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(Department::class.java.classLoader),
        parcel.readParcelable(Location::class.java.classLoader),
        parcel.readLocalDateTime(),
        parcel.readLocalDateTime())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(department, flags)
        parcel.writeParcelable(location, flags)
        parcel.writeLocalDateTime(startDate)
        parcel.writeLocalDateTime(endDate)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DepartmentLocation> {
        override fun createFromParcel(parcel: Parcel): DepartmentLocation {
            return DepartmentLocation(parcel)
        }

        override fun newArray(size: Int): Array<DepartmentLocation?> {
            return arrayOfNulls(size)
        }
    }

}