package com.quantvault.data.manager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.quantvault.core.util.isBuildVersionAtLeast

/**
 * Primary implementation of [QuantVaultPackageManager].
 */
class QuantVaultPackageManagerImpl(
    context: Context,
) : QuantVaultPackageManager {

    private val nativePackageManager = context.packageManager

    override fun getPackageInstallationSourceOrNull(packageName: String): String? =
        try {
            if (isBuildVersionAtLeast(Build.VERSION_CODES.R)) {
                nativePackageManager
                    .getInstallSourceInfo(packageName)
                    .installingPackageName
            } else {
                @Suppress("DEPRECATION")
                nativePackageManager
                    .getInstallerPackageName(packageName)
            }
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }

    override fun isPackageInstalled(packageName: String): Boolean {
        return try {
            if (isBuildVersionAtLeast(Build.VERSION_CODES.TIRAMISU)) {
                nativePackageManager.getApplicationInfo(
                    packageName,
                    PackageManager.ApplicationInfoFlags.of(0L),
                )
            } else {
                nativePackageManager.getApplicationInfo(packageName, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun getAppLabelForPackageOrNull(packageName: String): String? {
        return try {
            val appInfo = nativePackageManager.getApplicationInfo(packageName, 0)
            nativePackageManager
                .getApplicationLabel(appInfo)
                .toString()
        } catch (_: PackageManager.NameNotFoundException) {
            null
        }
    }
}





