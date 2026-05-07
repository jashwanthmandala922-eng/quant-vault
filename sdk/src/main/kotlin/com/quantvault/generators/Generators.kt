package com.quantvault.generators

enum class ForwarderServiceType {
    APPLE, FASTMAIL, Firefox, FLAGSHIP, GMAIL, PROTONMAIL, YAHOO, OTHER
}

enum class AppendType {
    WORD, NUMBER, SYMBOL
}

data class PasswordGeneratorRequest(
    val length: Int = 16,
    val includeNumbers: Boolean = true,
    val includeUppercase: Boolean = true,
    val includeLowercase: Boolean = true,
    val includeSymbols: Boolean = true,
    val ambiguousCharacters: Boolean = false,
    val avoidAmbiguous: Boolean = false,
    val customSymbols: String? = null,
    val customNumbers: String? = null,
    val customUppercase: String? = null,
    val customLowercase: String? = null
)

data class PassphraseGeneratorRequest(
    val wordCount: Int = 5,
    val separator: String = "-",
    val capitalize: Boolean = true,
    val includeNumber: Boolean = false
)

data class UsernameGeneratorRequest(
    val type: UsernameType = UsernameType.WORD,
    val length: Int = 10,
    val forwarderServiceType: ForwarderServiceType? = null,
    val forwarderEmail: String? = null,
    val includeNumbers: Boolean = true
)

enum class UsernameType {
    WORD, EMAIL, FORWARDED
}