package com.kazanneft.assetsmanager.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Serializer for LocalDateTime with format ISO_LOCAL_DATE_TIME
 */
class DateJsonSerializer : KSerializer<LocalDateTime> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)


    override fun serialize(encoder: Encoder, value: LocalDateTime) {

        encoder.encodeString(
            value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))

    }

    override fun deserialize(decoder: Decoder): LocalDateTime {

        val dateText = decoder.decodeString()

        return LocalDateTime.parse(dateText,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

}