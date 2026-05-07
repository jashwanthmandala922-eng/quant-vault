package com.quantvault.ui.platform.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.quantvault.ui.platform.components.field.interceptor.IncognitoInput
import com.quantvault.ui.platform.feature.settings.appearance.model.AppTheme
import com.quantvault.ui.platform.theme.color.QuantVaultColorScheme
import com.quantvault.ui.platform.theme.color.lightQuantVaultColorScheme
import com.quantvault.ui.platform.theme.color.darkQuantVaultColorScheme
import com.quantvault.ui.platform.theme.color.dynamicQuantVaultColorScheme
import com.quantvault.ui.platform.theme.color.toMaterialColorScheme
import com.quantvault.ui.platform.theme.shape.QuantVaultShapes
import com.quantvault.ui.platform.theme.shape.defaultQuantVaultShapes
import com.quantvault.ui.platform.theme.type.QuantVaultTypography
import com.quantvault.ui.platform.theme.type.defaultQuantVaultTypography
import com.quantvault.ui.platform.theme.type.toMaterialTypography
import com.quantvault.ui.platform.util.isDarkMode

/**
 * Static wrapper to make accessing the theme components easier.
 */
object QuantVaultTheme {
    /**
     * Retrieves the current [QuantVaultColorScheme].
     */
    val colorScheme: QuantVaultColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalQuantVaultColorScheme.current

    /**
     * Retrieves the current [QuantVaultShapes].
     */
    val shapes: QuantVaultShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalQuantVaultShapes.current

    /**
     * Retrieves the current [QuantVaultTypography].
     */
    val typography: QuantVaultTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalQuantVaultTypography.current
}

/**
 * The overall application theme. This can be configured to support a [theme] and [dynamicColor].
 */
@Composable
fun QuantVaultTheme(
    theme: AppTheme = AppTheme.DEFAULT,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = theme.isDarkMode(isSystemDarkMode = isSystemInDarkTheme())
    // Get the current scheme
    val materialColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context = context)
            } else {
                dynamicLightColorScheme(context = context)
            }
        }

        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }
    val QuantVaultColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicQuantVaultColorScheme(
                materialColorScheme = materialColorScheme,
                isDarkTheme = darkTheme,
            )
        }

        darkTheme -> darkQuantVaultColorScheme
        else -> lightQuantVaultColorScheme
    }

    CompositionLocalProvider(
        LocalQuantVaultColorScheme provides QuantVaultColorScheme,
        LocalQuantVaultShapes provides defaultQuantVaultShapes,
        LocalQuantVaultTypography provides defaultQuantVaultTypography,
    ) {
        MaterialTheme(
            colorScheme = QuantVaultColorScheme.toMaterialColorScheme(
                defaultColorScheme = materialColorScheme,
            ),
            typography = defaultQuantVaultTypography.toMaterialTypography(),
        ) { IncognitoInput(content = content) }
    }
}

/**
 * Provides access to the Quant Vault colors throughout the app.
 */
val LocalQuantVaultColorScheme: ProvidableCompositionLocal<QuantVaultColorScheme> =
    compositionLocalOf { lightQuantVaultColorScheme }

/**
 * Provides access to the Quant Vault shapes throughout the app.
 */
val LocalQuantVaultShapes: ProvidableCompositionLocal<QuantVaultShapes> =
    compositionLocalOf { defaultQuantVaultShapes }

/**
 * Provides access to the Quant Vault typography throughout the app.
 */
val LocalQuantVaultTypography: ProvidableCompositionLocal<QuantVaultTypography> =
    compositionLocalOf { defaultQuantVaultTypography }








