// port-lint: tests cargo.rs
package io.github.kotlinmania.assertcmd

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("DEPRECATION")
class CargoTest {
    @Test
    fun testTargetDir() {
        assertEquals("target", targetDir())
    }

    @Test
    fun testCargoBin() {
        assertEquals("mybin", cargoBin("mybin"))
    }

    @Test
    fun testCargoBinCmd() {
        val result = cargoBinCmd("mybin")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testCargoRunnerDefault() {
        assertNull(cargoRunner())
    }

    @Test
    fun testCargoError() {
        val err = CargoError(IllegalArgumentException("bad path"))
        assertTrue(err.toString().contains("bad path"))
    }

    @Test
    fun testNotFoundError() {
        val err = NotFoundError("/bin/unknown")
        assertTrue(err.fmt().contains("/bin/unknown"))
    }
}
