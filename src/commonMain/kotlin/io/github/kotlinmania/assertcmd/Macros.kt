// port-lint: source assert_cmd/src/macros.rs
package io.github.kotlinmania.assertcmd

/**
 * Returns the package name at compile time.
 */
public fun pkgName(): String = "assert-cmd"

/**
 * Deprecated, replaced with [pkgName].
 */
@Deprecated(
    message = "replaced with pkgName",
    replaceWith = ReplaceWith("pkgName()"),
)
public fun crateName(): String = pkgName()

/**
 * The path to a binary target's executable.
 */
public fun cargoBinMacro(binTargetName: String = pkgName()): String = cargoBinStr(binTargetName)

/**
 * A [Command] for the binary target's executable.
 */
public fun cargoBinCmdMacro(binTargetName: String = pkgName()): Command =
    cargoBinCmd(binTargetName).getOrThrow()
