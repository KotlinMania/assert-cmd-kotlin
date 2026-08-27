// port-lint: tests assert_cmd/src/output.rs
package io.github.kotlinmania.assertcmd

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OutputTest {
    @Test
    fun formatBytes() {
        val s = StringBuilder()
        for (i in 0 until 80) {
            s.append("$i\n")
        }

        val buf = StringBuilder()
        formatBytes(s.toString().encodeToByteArray(), buf)

        val expected =
            """
            |<80 lines total>
            |```
            |0
            |1
            |2
            |3
            |4
            |5
            |6
            |7
            |8
            |9
            |10
            |11
            |12
            |13
            |14
            |15
            |16
            |17
            |18
            |19
            |```
            |<20 lines omitted>
            |```
            |40
            |41
            |42
            |43
            |44
            |45
            |46
            |47
            |48
            |49
            |50
            |51
            |52
            |53
            |54
            |55
            |56
            |57
            |58
            |59
            |60
            |61
            |62
            |63
            |64
            |65
            |66
            |67
            |68
            |69
            |70
            |71
            |72
            |73
            |74
            |75
            |76
            |77
            |78
            |79
            |```
            |
            """.trimMargin()

        assertEquals(expected, buf.toString())
    }

    @Test
    fun noTrailingNewline() {
        val s = "no\ntrailing\nnewline"

        val buf = StringBuilder()
        formatBytes(s.encodeToByteArray(), buf)

        val expected =
            """
            |```
            |no
            |trailing
            |newline```
            |
            """.trimMargin()

        assertEquals(expected, buf.toString())
    }

    @Test
    fun testOutputOkSuccess() {
        val output =
            Output(
                status = ExitStatus(code = 0, success = true),
                stdout = "hello\n".encodeToByteArray(),
                stderr = ByteArray(0),
            )
        val res = output.ok()
        assertTrue(res.isSuccess)
        assertEquals(output, output.unwrap())
    }

    @Test
    fun testOutputOkFailure() {
        val output =
            Output(
                status = ExitStatus(code = 1, success = false),
                stdout = ByteArray(0),
                stderr = "error\n".encodeToByteArray(),
            )
        val res = output.ok()
        assertTrue(res.isFailure)
        val err = output.unwrapErr()
        assertNotNull(err.asOutput())
    }

    @Test
    fun testOutputUnwrapErrOnSuccessThrows() {
        val output =
            Output(
                status = ExitStatus(code = 0, success = true),
                stdout = "ok\n".encodeToByteArray(),
                stderr = ByteArray(0),
            )
        assertFailsWith<IllegalStateException> {
            output.unwrapErr()
        }
    }
}
