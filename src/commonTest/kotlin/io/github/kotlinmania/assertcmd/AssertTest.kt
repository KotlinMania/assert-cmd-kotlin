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

    fun convertCode(pred: IntoCodePredicate): Predicate<Int> = pred.intoCode()

    fun convertOutput(pred: IntoOutputPredicate): Predicate<ByteArray> = pred.intoOutput()

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

    @Test
    fun testAssertErrorAssertMethod() {
        val output = Output(status = ExitStatus(code = 1, success = false))
        val err =
            assertFailsWith<AssertError> {
                output.assert().success()
            }
        assertEquals(output, err.assert().output)
    }

    @Test
    fun testPredicateReflectionAndFindCase() {
        val eqPred = EqCodePredicate(42)
        assertEquals(42, eqPred.parameters().next().value)
        assertTrue(!eqPred.children().hasNext())
        assertTrue(eqPred.findCase(true, 42) != null)
        assertTrue(eqPred.findCase(true, 0) == null)

        val inPred = InCodePredicate(listOf(1, 2, 3))
        assertEquals(listOf(1, 2, 3), inPred.parameters().next().value)
        assertTrue(!inPred.children().hasNext())
        assertTrue(inPred.findCase(true, 2) != null)

        val bytesPred = BytesContentOutputPredicate("test".encodeToByteArray())
        assertTrue(bytesPred.parameters().hasNext())
        assertTrue(!bytesPred.children().hasNext())
        assertTrue(bytesPred.findCase(true, "test".encodeToByteArray()) != null)

        val strPred = StrContentOutputPredicate("test")
        assertTrue(strPred.parameters().hasNext())
        assertTrue(!strPred.children().hasNext())
        assertTrue(strPred.findCase(true, "test".encodeToByteArray()) != null)

        val strOutPred = StrOutputPredicate { it.startsWith("te") }
        assertTrue(strOutPred.parameters().hasNext())
        assertTrue(strOutPred.children().hasNext())
        assertTrue(strOutPred.findCase(true, "test".encodeToByteArray()) != null)
    }
}
