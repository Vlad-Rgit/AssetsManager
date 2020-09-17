package com.kazanneft.assetsmanager.repos

interface Repo {
    suspend fun <T> getAll(): MutableList<T>
}