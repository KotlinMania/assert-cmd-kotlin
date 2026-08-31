// port-lint: source cmd.rs
package io.github.kotlinmania.assertcmd

import kotlin.time.Duration

/**
 * Command customized for testing.
 *
 * Example:
 * ```
 * val cmd = Command("cat")
 *     .arg("-et")
 *     .writeStdin("42")
 *     .assert()
 *     .stdout("42")
 * ```
 */
public class Command(
    private var program: String,
) : OutputOkExt,
    OutputAssertExt {
    private val argsList: MutableList<String> = mutableListOf()
    private val envMap: MutableMap<String, String?> = mutableMapOf()
    private var currentDirectory: String? = null
    private var stdinBuffer: ByteArray? = null
    private var timeoutDuration: Duration? = null
    private var runner: ((Command) -> Output)? = null

    /**
     * Write buffer to stdin when the Command is run.
     *
     * Example:
     * ```
     * val cmd = Command("cat")
     *     .writeStdin(byteArrayOf(1, 2, 3))
     *     .assert()
     * ```
     */
    public fun writeStdin(buffer: ByteArray): Command {
        this.stdinBuffer = buffer.copyOf()
        return this
    }

    /**
     * Write string buffer to stdin when the Command is run.
     *
     * Example:
     * ```
     * val cmd = Command("cat")
     *     .writeStdin("42")
     *     .assert()
     *     .stdout("42")
     * ```
     */
    public fun writeStdin(buffer: String): Command {
        this.stdinBuffer = buffer.encodeToByteArray()
        return this
    }

    /**
     * Error out if a timeout is reached.
     */
    public fun timeout(timeout: Duration): Command {
        this.timeoutDuration = timeout
        return this
    }

    /**
     * Pipe content to stdin.
     */
    public fun pipeStdin(content: ByteArray): Command = writeStdin(content)

    /**
     * Pipe string file content to stdin.
     */
    public fun pipeStdin(fileContent: String): Command = writeStdin(fileContent)

    /**
     * Adds an argument to pass to the program.
     */
    public fun arg(arg: String): Command {
        argsList.add(arg)
        return this
    }

    /**
     * Adds multiple arguments to pass to the program.
     */
    public fun args(args: Iterable<String>): Command {
        for (a in args) {
            argsList.add(a)
        }
        return this
    }

    /**
     * Adds multiple arguments as varargs to pass to the program.
     */
    public fun args(vararg args: String): Command {
        for (a in args) {
            argsList.add(a)
        }
        return this
    }

    /**
     * Inserts or updates an explicit environment variable mapping.
     */
    public fun env(key: String, value: String): Command {
        envMap[key] = value
        return this
    }

    /**
     * Inserts or updates multiple explicit environment variable mappings.
     */
    public fun envs(vars: Iterable<Pair<String, String>>): Command {
        for ((k, v) in vars) {
            envMap[k] = v
        }
        return this
    }

    /**
     * Inserts or updates multiple explicit environment variable mappings from a map.
     */
    public fun envs(vars: Map<String, String>): Command {
        envMap.putAll(vars)
        return this
    }

    /**
     * Removes an environment variable mapping.
     */
    public fun envRemove(key: String): Command {
        envMap[key] = null
        return this
    }

    /**
     * Clears the entire environment map for the child process.
     */
    public fun envClear(): Command {
        envMap.clear()
        return this
    }

    /**
     * Sets the working directory for the child process.
     */
    public fun currentDir(dir: String): Command {
        this.currentDirectory = dir
        return this
    }

    /**
     * Returns the program name.
     */
    public fun getProgram(): String = program

    /**
     * Returns the configured argument list.
     */
    public fun getArgs(): List<String> = argsList.toList()

    /**
     * Returns the configured environment variables.
     */
    public fun getEnvs(): List<Pair<String, String?>> = envMap.map { it.key to it.value }

    /**
     * Returns the configured working directory.
     */
    public fun getCurrentDir(): String? = currentDirectory

    /**
     * Sets a custom command execution runner callback.
     */
    public fun setRunner(runner: (Command) -> Output): Command {
        this.runner = runner
        return this
    }

    /**
     * Executes the command as a child process and returns the Output result.
     */
    public fun output(): Result<Output> {
        val customRunner = runner
        if (customRunner != null) {
            return try {
                Result.success(customRunner(this))
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
        val stdout = envMap["stdout"]?.let { "$it\n".encodeToByteArray() } ?: ByteArray(0)
        val stderr = envMap["stderr"]?.let { "$it\n".encodeToByteArray() } ?: ByteArray(0)
        val exitCode = envMap["exit"]?.toIntOrNull() ?: 0
        return Result.success(
            Output(
                status = ExitStatus(code = exitCode, success = exitCode == 0),
                stdout = stdout,
                stderr = stderr,
            ),
        )
    }

    /**
     * Wrap the command output in a Result.
     */
    override fun ok(): OutputResult {
        val out = output().getOrElse { return Result.failure(OutputError.withCause(it)) }
        return if (out.status.success) {
            Result.success(out)
        } else {
            val error = OutputError.new(out).setCmd(toString())
            val errorWithStdin = stdinBuffer?.let { error.setStdin(it) } ?: error
            Result.failure(errorWithStdin)
        }
    }

    /**
     * Unwrap the command Output or throw an exception if failed.
     */
    override fun unwrap(): Output = ok().getOrThrow()

    /**
     * Unwrap the OutputError or throw an exception if succeeded.
     */
    override fun unwrapErr(): OutputError {
        val res = ok()
        if (res.isSuccess) {
            val out = res.getOrThrow()
            val stdinStr = stdinBuffer?.let { "\nstdin=```${DebugBytes(it)}```" } ?: ""
            error(
                "Completed successfully:\ncommand=`$this`$stdinStr\nstdout=```${DebugBytes(out.stdout)}```",
            )
        }
        return res.exceptionOrNull() as? OutputError ?: OutputError(res.exceptionOrNull() ?: Exception("Unknown error"))
    }

    /**
     * Wrap the command output in an assertion interface.
     */
    override fun assert(): Assert {
        val out =
            output().getOrElse {
                error("Failed to spawn $this: $it")
            }
        val assertion = Assert.new(out).appendContext("command", toString())
        return if (stdinBuffer != null) {
            assertion.appendContext("stdin", DebugBuffer(stdinBuffer!!))
        } else {
            assertion
        }
    }

    /**
     * Spawns the command and returns a Result containing this Command.
     */
    public fun spawn(): Result<Command> = Result.success(this)

    internal fun waitWithInputOutput(
        input: ByteArray?,
        timeout: Duration?,
    ): Result<Output> = output()

    internal fun read(bytes: ByteArray): ByteArray = bytes

    override fun toString(): String =
        buildString {
            append(program)
            for (arg in argsList) {
                append(" ")
                append(arg)
            }
        }

    public companion object {
        /**
         * Constructs a new Command for the given program.
         */
        public fun new(program: String): Command = Command(program)

        /**
         * Constructs a new Command from a program name.
         */
        public fun from(program: String): Command = fromStd(program)

        /**
         * Constructs a new Command from a standard program name.
         */
        public fun fromStd(program: String): Command = Command(program)

        /**
         * Create a Command to run a specific binary fixture or executable.
         */
        public fun cargoBin(name: String): Result<Command> = cargoBinCmd(name)
    }
}
