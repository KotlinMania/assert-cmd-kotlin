// port-lint: source cargo.rs
package io.github.kotlinmania.assertcmd

/**
 * Extension trait for [Command] to easily launch a crate's binaries.
 */
public interface CommandCargoExt {
    /**
     * Create a [Command] to run a specific binary of the current crate.
     */
    @Deprecated(
        message = "incompatible with a custom cargo build-dir, see instead cargo_bin!",
    )
    public fun cargoBin(name: String): Result<Command>
}

/**
 * Error when finding crate binary.
 */
public class CargoError(
    override val cause: Throwable? = null,
) : Exception(cause?.message, cause) {
    public companion object {
        public fun withCause(cause: Throwable): CargoError = CargoError(cause)
    }

    public fun fmt(): String = toString()

    override fun toString(): String =
        if (cause != null) "Cause: $cause\n" else ""
}

/**
 * Error when cargo binary path is not found.
 */
internal class NotFoundError(
    val path: String,
) : Exception("Cargo command not found: $path\n") {
    internal fun fmt(): String = message ?: ""
}

internal fun targetDir(): String = "target"

/**
 * Look up the path to a cargo-built binary within an integration test.
 */
@Deprecated(
    message = "incompatible with a custom cargo build-dir, see instead cargo_bin!",
)
public fun cargoBin(name: String): String = cargoBinStr(name)

internal fun cargoBinStr(name: String): String = name

@Suppress("DEPRECATION")
public fun cargoBinCmd(name: String): Result<Command> {
    val path = cargoBin(name)
    val runner = cargoRunner()
    return if (runner != null && runner.isNotEmpty()) {
        val cmd = Command.new(runner[0])
        cmd.args(runner.drop(1))
        cmd.arg(path)
        Result.success(cmd)
    } else {
        Result.success(Command.new(path))
    }
}

public fun cargoRunner(): List<String>? = null
