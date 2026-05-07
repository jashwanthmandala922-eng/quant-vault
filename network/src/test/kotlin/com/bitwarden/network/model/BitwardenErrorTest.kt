package com.quantvault.network.model

import com.quantvault.network.exception.CookieRedirectException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class quantvaultErrorTest {

    @Test
    fun `toquantvaultError with CookieRedirectException should return Http with status 400`() {
        val exception = CookieRedirectException(hostname = "example.com")

        val result = exception.toquantvaultError()

        assertTrue(result is quantvaultError.Http)
        val httpError = result as quantvaultError.Http
        assertEquals(400, httpError.code)
    }

    @Test
    fun `toquantvaultError with CookieRedirectException should include message in body`() {
        val exception = CookieRedirectException(hostname = "example.com")

        val result = exception.toquantvaultError()

        val httpError = result as quantvaultError.Http
        val body = httpError.responseBodyString
        assertTrue(body?.contains(exception.message.orEmpty()) == true)
    }

    @Test
    fun `toquantvaultError with IOException should return Network`() {
        val exception = IOException("network failure")

        val result = exception.toquantvaultError()

        assertTrue(result is quantvaultError.Network)
        assertEquals(exception, result.throwable)
    }

    @Test
    fun `toquantvaultError with HttpException should return Http`() {
        val exception = HttpException(
            Response.error<Unit>(400, "error".toResponseBody()),
        )

        val result = exception.toquantvaultError()

        assertTrue(result is quantvaultError.Http)
        assertEquals(exception, result.throwable)
    }

    @Test
    fun `toquantvaultError with RuntimeException should return Other`() {
        val exception = RuntimeException("unexpected")

        val result = exception.toquantvaultError()

        assertTrue(result is quantvaultError.Other)
        assertEquals(exception, result.throwable)
    }
}





