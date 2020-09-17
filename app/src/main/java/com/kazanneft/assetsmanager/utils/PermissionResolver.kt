package com.kazanneft.assetsmanager.utils

import android.Manifest
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

fun hasReadStoragePermission(context: Context): Boolean {
   return ContextCompat
        .checkSelfPermission(context,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
}
