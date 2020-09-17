package com.kazanneft.assetsmanager.models

interface AreItemsSameComparator<T> {
    fun areItemsSame(item: T): Boolean
}