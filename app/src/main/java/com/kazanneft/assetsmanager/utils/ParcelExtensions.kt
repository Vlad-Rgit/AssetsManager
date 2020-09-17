package com.kazanneft.assetsmanager.utils

import android.os.Parcel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun Parcel.readLocalDateTime(): LocalDateTime {
    val epochSecond = this.readLong()
    return Instant.ofEpochSecond(epochSecond)
        .atOffset(ZoneOffset.UTC).toLocalDateTime()
}

fun Parcel.writeLocalDateTime(dateTime: LocalDateTime?) {

    if(dateTime == null)
        return

    val epochSecond = dateTime.toEpochSecond(ZoneOffset.UTC)
}