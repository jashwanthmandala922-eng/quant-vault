package com.quantvault.core.data.repository.error

/**
 * An exception indicating that a required property was missing.
 */
class MissingPropertyException(
    propertyName: String,
) : IllegalStateException("Missing the required $propertyName property")




