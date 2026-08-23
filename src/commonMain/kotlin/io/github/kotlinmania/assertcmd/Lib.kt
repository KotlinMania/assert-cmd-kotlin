// port-lint: source lib.rs
package io.github.kotlinmania.assertcmd

/**
 * assert-cmd library namespace and prelude.
 */
public object Lib {
    public const val VERSION: String = "2.1.2"
}

public object Prelude {
    public typealias OutputAssertExt = io.github.kotlinmania.assertcmd.OutputAssertExt
    public typealias CommandCargoExt = io.github.kotlinmania.assertcmd.CommandCargoExt
    public typealias OutputOkExt = io.github.kotlinmania.assertcmd.OutputOkExt
}

public class ReadmeDoctests
