package com.kazanneft.assetsmanager.models

import com.kazanneft.assetsmanager.serializers.DateJsonSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class AssetTransfer(
    var id: Long,
    var assetId: Long,
    var fromAssetSN: String,
    var toAssetSN: String,
    var fromDepartmentLocationId: Long,
    var toDepartmentLocationId: Long,
    @Serializable(with = DateJsonSerializer::class)
    var transferDate: LocalDateTime? = null,
    var fromDepartmentLocation: DepartmentLocation? = null,
    var toDepartmentLocation: DepartmentLocation? = null,
    var asset: Asset? = null
): AreItemsSameComparator<AssetTransfer> {
    override fun areItemsSame(item: AssetTransfer): Boolean {
        return id == item.id
    }
}