package com.kazanneft.assetsmanager.models

data class ImageItem(
    val name: String,
    val bytes: ByteArray,
    val isNew: Boolean = true
): AreItemsSameComparator<ImageItem> {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageItem

        if (name != other.name) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }

    override fun areItemsSame(item: ImageItem): Boolean {
        return name == item.name
    }
}