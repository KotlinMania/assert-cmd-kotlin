// port-lint: tests assert.rs
package io.github.kotlinmania.assertcmd

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AssertTest {
    @Test
    fun testAssertSuccess() {
        val output =
            Output(
                status = ExitStatus(code = 0, success = true),
                stdout = "hello\n".encodeToByteArray(),
                stderr = ByteArray(0),
            )
        val assert =
            output
                .assert()
                .appendContext("test", "context")
                .success()
                .code(0)
                .stdout("hello\n")
                .stderr("")

        assertEquals(output, assert.output)
    }

    @Test
    fun testAssertFailure() {
        val output =
            Output(
                status = ExitStatus(code = 42, success = false),
                stdout = ByteArray(0),
                stderr = "failed\n".encodeToByteArray(),
            )
        val assert =
            output
                .assert()
                .failure()
                .code(42)
                .code(intArrayOf(1, 42, 100))
                .stderr("failed\n")

        assertEquals(output, assert.output)
    }

    @Test
    fun testAssertSuccessThrowsOnFailure() {
        val output =
            Output(
                status = ExitStatus(code = 1, success = false),
            )
        assertFailsWith<AssertError> {
            output.assert().success()
        }
    }

    @Test
    fun testAssertFailureThrowsOnSuccess() {
        val output =
            Output(
                status = ExitStatus(code = 0, success = true),
            )
        assertFailsWith<AssertError> {
            output.assert().failure()
        }
    }

    @Test
    fun testAssertCodeMismatchThrows() {
        val output =
            Output(
                status = ExitStatus(code = 1, success = false),
            )
        assertFailsWith<AssertError> {
            output.assert().code(42)
        }
    }

    @Test
    fun testAssertStdoutMismatchThrows() {
        val output =
            Output(
                status = ExitStatus(code = 0, success = true),
                stdout = "actual".encodeToByteArray(),
            )
        assertFailsWith<AssertError> {
            output.assert().stdout("expected")
        }
    }

    @Test
    fun testAssertStdoutPredicate() {
        val output =
            Output(
                status = ExitStatus(code = 0, success = true),
                stdout = "hello world".encodeToByteArray(),
            )
        output.assert().stdout { it.contains("world") }
    }

    @Test
    fun intoCodeFromPred() {
        val pred = convertCode(EqCodePredicate.new(10))
        assertTrue(pred.eval(10))
    }

    @Test
    fun intoCodeFromI32() {
        val pred = convertCode(EqCodePredicate(10))
        assertTrue(pred.eval(10))
    }

    @Test
    fun intoCodeFromVec() {
        val pred = convertCode(InCodePredicate(listOf(3, 10)))
        assertTrue(pred.eval(10))
    }

    @Test
    fun intoCodeFromArray() {
        val pred = convertCode(InCodePredicate(listOf(3, 10)))
        assertTrue(pred.eval(10))
    }

    @Test
    fun intoOutputFromPred() {
        val pred = convertOutput(BytesContentOutputPredicate.new("Hello".encodeToByteArray()))
        assertTrue(pred.eval("Hello".encodeToByteArray()))
    }

    @Test
    fun intoOutputFromBytes() {
        val pred = convertOutput(BytesContentOutputPredicate.new("Hello".encodeToByteArray()))
        assertTrue(pred.eval("Hello".encodeToByteArray()))
    }

    @Test
    fun intoOutputFromVec() {
        val bytes = listOf('H'.code.toByte(), 'e'.code.toByte(), 'l'.code.toByte(), 'l'.code.toByte(), 'o'.code.toByte())
        val pred = convertOutput(BytesContentOutputPredicate.fromVec(bytes))
        assertTrue(pred.eval("Hello".encodeToByteArray()))
    }

    @Test
    fun intoOutputFromStr() {
        val pred = convertOutput(StrContentOutputPredicate.fromString("Hello"))
        assertTrue(pred.eval("Hello".encodeToByteArray()))
    }
}
