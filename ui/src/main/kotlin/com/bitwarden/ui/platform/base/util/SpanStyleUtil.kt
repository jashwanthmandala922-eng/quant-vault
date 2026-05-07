package com.quantvault.ui.platform.base.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import com.quantvault.ui.platform.theme.QuantVaultTheme
import com.quantvault.ui.platform.theme.color.QuantVaultColorScheme

/**
 * Creates a new [SpanStyle] from the specified [color] and [textStyle].
 */
fun spanStyleOf(
    color: Color,
    textStyle: TextStyle,
): SpanStyle =
    SpanStyle(
        color = color,
        fontSize = textStyle.fontSize,
        fontWeight = textStyle.fontWeight,
        fontStyle = textStyle.fontStyle,
        fontSynthesis = textStyle.fontSynthesis,
        fontFamily = textStyle.fontFamily,
        fontFeatureSettings = textStyle.fontFeatureSettings,
        letterSpacing = textStyle.letterSpacing,
        baselineShift = textStyle.baselineShift,
        textGeometricTransform = textStyle.textGeometricTransform,
        localeList = textStyle.localeList,
        background = textStyle.background,
        textDecoration = textStyle.textDecoration,
        shadow = textStyle.shadow,
        platformStyle = textStyle.platformStyle?.spanStyle,
        drawStyle = textStyle.drawStyle,
    )

/**
 * A [SpanStyle] representing "regular" text using the text primary color and label medium font.
 */
val quantvaultDefaultSpanStyle: SpanStyle
    @Composable
    @ReadOnlyComposable
    get() = spanStyleOf(
        color = QuantVaultTheme.colorScheme.text.primary,
        textStyle = QuantVaultTheme.typography.labelMedium,
    )

/**
 * A [SpanStyle] representing "bold" text using the text primary color and body medium emphasis
 * font.
 */
val quantvaultBoldSpanStyle: SpanStyle
    @Composable
    @ReadOnlyComposable
    get() = spanStyleOf(
        color = QuantVaultTheme.colorScheme.text.primary,
        textStyle = QuantVaultTheme.typography.bodyMediumEmphasis,
    )

/**
 * A [SpanStyle] representing "clickable" text using the text interaction color and label medium
 * font.
 */
val quantvaultClickableTextSpanStyle: SpanStyle
    @Composable
    @ReadOnlyComposable
    get() = spanStyleOf(
        color = QuantVaultTheme.colorScheme.text.interaction,
        textStyle = QuantVaultTheme.typography.labelMedium,
    )






