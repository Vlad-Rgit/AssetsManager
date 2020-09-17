package com.kazanneft.assetsmanager.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
class Employee (val id: Long,
                val firstName: String,
                val lastName: String,
                val phone: String): Parcelable {

    constructor(parcel: Parcel): this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun toString(): String {
        return "$lastName $firstName"
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(phone)
    }

    companion object CREATOR: Parcelable.Creator<Employee> {

        override fun createFromParcel(parcel: Parcel): Employee {
            return Employee(parcel)
        }

        override fun newArray(p0: Int): Array<Employee> {
            return newArray(p0)
        }

    }

}