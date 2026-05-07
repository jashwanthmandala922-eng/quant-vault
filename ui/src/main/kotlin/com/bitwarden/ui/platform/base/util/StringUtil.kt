@file:OmitFromCoverage

package com.quantvault.ui.platform.base.util

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.quantvault.annotation.OmitFromCoverage

/**
 * Creates an [AnnotatedString] from a string from a resource and allows for optional arguments
 * to be applied.
 *
 * @see Int.toAnnotatedString
 */
@Composable
fun annotatedStringResource(
    @StringRes id: Int,
    vararg args: String,
    style: SpanStyle = quantvaultDefaultSpanStyle,
    emphasisHighlightStyle: SpanStyle = quantvaultBoldSpanStyle,
    linkHighlightStyle: SpanStyle = quantvaultClickableTextSpanStyle,
    resources: Resources = LocalResources.current,
    onAnnotationClick: ((annotationKey: String) -> Unit)? = null,
): AnnotatedString =
    id.toAnnotatedString(
        args = args,
        style = style,
        emphasisHighlightStyle = emphasisHighlightStyle,
        linkHighlightStyle = linkHighlightStyle,
        resources = resources,
        onAnnotationClick = onAnnotationClick,
    )






