// port-lint: source assert_cmd/src/lib.rs
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
public object Lib {
    /**
     * Library version string.
     */
    public const val VERSION: String = "2.1.2"
}

/**
 * Extension traits that are useful to have available.
 */
public object Prelude {
    /** Re-export of [OutputAssertExt]. */
    public typealias OutputAssertExt = io.github.kotlinmania.assertcmd.OutputAssertExt

    /** Re-export of [CommandCargoExt]. */
    public typealias CommandCargoExt = io.github.kotlinmania.assertcmd.CommandCargoExt

    /** Re-export of [OutputOkExt]. */
    public typealias OutputOkExt = io.github.kotlinmania.assertcmd.OutputOkExt
}

/**
 * Type representing README doctest presence.
 */
public class ReadmeDoctests
