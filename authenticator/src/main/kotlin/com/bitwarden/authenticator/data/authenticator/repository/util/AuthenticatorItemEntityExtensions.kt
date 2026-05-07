package com.quantvault.authenticator.data.authenticator.repository.util

import com.quantvault.authenticator.data.authenticator.datasource.disk.entity.AuthenticatorItemEntity
import com.quantvault.core.data.repository.util.SpecialCharWithPrecedenceComparator

/**
 * Sorts the data in alphabetical order by name. Using lexicographical sorting but giving
 * precedence to special characters over letters and digits.
 */
@JvmName("toAlphabeticallySortedCipherList")
fun List<AuthenticatorItemEntity>.sortAlphabetically(): List<AuthenticatorItemEntity> {
    return this.sortedWith(
        comparator = { cipher1, cipher2 ->
            SpecialCharWithPrecedenceComparator.compare(cipher1.issuer, cipher2.issuer)
        },
    )
}




