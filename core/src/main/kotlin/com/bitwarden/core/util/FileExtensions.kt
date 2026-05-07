package com.quantvault.core.util

import com.quantvault.annotation.OmitFromCoverage
import java.io.File

/**
 * A helper function for creating a file from a path.
 */
@OmitFromCoverage
fun fileOf(path: String): File = File(path)




