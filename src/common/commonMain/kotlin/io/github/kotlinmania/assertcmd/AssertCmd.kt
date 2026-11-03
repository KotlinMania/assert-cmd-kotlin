package io.github.kotlinmania.assertcmd

/**
 * Assert [Command] - Easy command initialization and assertions.
 *
 * `assert-cmd` aims to simplify the process for doing integration testing of CLIs, including:
 * - Finding a binary to test
 * - Assert on the result of program runs
 *
 * Example:
 * ```
 * val cmd = Command.cargoBin("binFixture").getOrThrow()
 * cmd.assert().success()
 * ```
 */
public object AssertCmd {
    /**
     * Library version string.
     */
    public const val VERSION: String = "2.1.2"
}

/**
 * Type representing README doctest presence.
 */
public class ReadmeDoctests
