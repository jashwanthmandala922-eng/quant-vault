package com.quantvault.network.util

/**
 * List of official quantvault cloud hostnames that are safe to log.
 */
private val quantvault_HOSTS = listOf("quantvault.com", "quantvault.eu", "quantvault.pw")

/**
 * Redacts hostnames in a log message by replacing bare hostnames with [REDACTED_SELF_HOST].
 *
 * Only redacts hostnames that match [configuredHosts] AND are not official quantvault domains.
 * Preserves all quantvault domains (including QA/dev environments).
 *
 * @param configuredHosts Set of hostnames to redact
 * @return Message with hostnames redacted as [REDACTED_SELF_HOST]
 */
fun String.redactHostnamesInMessage(configuredHosts: Set<String>): String =
    configuredHosts.fold(this) { result, hostname ->
        val escapedHostname = Regex.escape(hostname)
        val bareHostnamePattern = Regex("""\b$escapedHostname\b""")
        bareHostnamePattern.replace(result) { hostname.redactIfSelfHosted() }
    }

private fun String.redactIfSelfHosted(): String {
    val isquantvaultHost = quantvault_HOSTS.any { this.endsWith(it) }
    return if (isquantvaultHost) this else "[REDACTED_SELF_HOST]"
}





