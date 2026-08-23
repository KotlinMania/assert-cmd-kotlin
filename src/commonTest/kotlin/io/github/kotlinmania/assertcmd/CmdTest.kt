// port-lint: tests cmd.rs
package io.github.kotlinmania.assertcmd

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class CmdTest {
    @Test
    fun testCommandBuilder() {
        val cmd =
            Command
                .new("test-app")
                .arg("-v")
                .args("file1.txt", "file2.txt")
                .env("KEY", "VALUE")
                .envs(mapOf("A" to "1", "B" to "2"))
                .currentDir("/tmp")
                .writeStdin("sample input")
                .timeout(5.seconds)

        assertEquals("test-app", cmd.getProgram())
        assertEquals(listOf("-v", "file1.txt", "file2.txt"), cmd.getArgs())
        assertEquals("/tmp", cmd.getCurrentDir())
    }

    @Test
    fun testCommandExecutionSuccess() {
        val cmd =
            Command
                .new("echo")
                .env("stdout", "hello world")
                .env("exit", "0")

        val output = cmd.unwrap()
        assertEquals(0, output.status.code)
        assertEquals("hello world\n", output.stdout.decodeToString())
    }

    @Test
    fun testCommandExecutionFailure() {
        val cmd =
            Command
                .new("bin_fixture")
                .env("stderr", "error message")
                .env("exit", "42")

        val err = cmd.unwrapErr()
        val out = err.asOutput()
        assertTrue(out != null)
        assertEquals(42, out.status.code)
    }

    @Test
    fun testCommandAssertSuccess() {
        val assert =
            Command
                .new("echo")
                .env("stdout", "all good")
                .env("exit", "0")
                .assert()
                .success()
                .code(0)
                .stdout("all good\n")

        assertEquals(0, assert.output.status.code)
    }

    @Test
    fun testCommandAssertFailure() {
        val assert =
            Command
                .new("test")
                .env("stderr", "failure happened")
                .env("exit", "1")
                .assert()
                .failure()
                .code(1)
                .stderr("failure happened\n")

        assertEquals(1, assert.output.status.code)
    }

    @Test
    fun testCommandAssertThrowsOnUnexpected() {
        assertFailsWith<AssertError> {
            Command
                .new("test")
                .env("exit", "1")
                .assert()
                .success()
        }
    }
}
