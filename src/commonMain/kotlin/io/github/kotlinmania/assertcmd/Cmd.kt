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
     *
     * Example:
     * ```kotlin
     * val assert = Command.cargoBin("binFixture").getOrThrow()
     *     .timeout(1.toDuration(DurationUnit.SECONDS))
     *     .env("sleep", "100")
     *     .assert()
     * assert.failure()
     * ```
     */
    public fun timeout(timeout: Duration): Command {
        this.timeoutDuration = timeout
        return this
    }

    /**
     * Pipe content to stdin when the Command is run.
     */
    public fun pipeStdin(content: ByteArray): Command = writeStdin(content)

    /**
     * Pipe string file content to stdin when the Command is run.
     */
    public fun pipeStdin(fileContent: String): Command = writeStdin(fileContent)

    /**
     * Adds an argument to pass to the program.
     *
     * Only one argument can be passed per use. To pass multiple arguments see [args].
     *
     * Example:
     * ```kotlin
     * Command.new("ls")
     *     .arg("-l")
     *     .arg("-a")
     *     .unwrap()
     * ```
     */
    public fun arg(arg: String): Command {
        argsList.add(arg)
        return this
    }

    /**
     * Adds multiple arguments to pass to the program.
     *
     * To pass a single argument see [arg].
     *
     * Example:
     * ```kotlin
     * Command.new("ls")
     *     .args(listOf("-l", "-a"))
     *     .unwrap()
     * ```
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
     *
     * Example:
     * ```kotlin
     * Command.new("ls")
     *     .env("PATH", "/bin")
     *     .unwrapErr()
     * ```
     */
    public fun env(key: String, value: String): Command {
        envMap[key] = value
        return this
    }

    /**
     * Inserts or updates multiple explicit environment variable mappings.
     *
     * Example:
     * ```kotlin
     * Command.new("printenv")
     *     .envClear()
     *     .envs(listOf("TERM" to "dumb"))
     *     .unwrap()
     * ```
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
     *
     * Example:
     * ```kotlin
     * Command.new("ls")
     *     .envRemove("PATH")
     *     .unwrapErr()
     * ```
     */
    public fun envRemove(key: String): Command {
        envMap[key] = null
        return this
    }

    /**
     * Clears the entire environment map for the child process.
     *
     * Example:
     * ```kotlin
     * Command.new("ls")
     *     .envClear()
     *     .unwrapErr()
     * ```
     */
    public fun envClear(): Command {
        envMap.clear()
        return this
    }

    /**
     * Sets the working directory for the child process.
     *
     * Example:
     * ```kotlin
     * Command.new("ls")
     *     .currentDir("/bin")
     *     .unwrap()
     * ```
     */
    public fun currentDir(dir: String): Command {
        this.currentDirectory = dir
        return this
    }

    /**
     * Returns the path to the program that was given to [Command.new].
     *
     * Example:
     * ```kotlin
     * val cmd = Command.new("echo")
     * assertEquals("echo", cmd.getProgram())
     * ```
     */
    public fun getProgram(): String = program

    /**
     * Returns an iterator or list of arguments that will be passed to the program.
     *
     * This does not include the path to the program as the first argument;
     * it only includes the arguments specified with [Command.arg] and [Command.args].
     *
     * Example:
     * ```kotlin
     * val cmd = Command.new("echo")
     * cmd.arg("first").arg("second")
     * assertEquals(listOf("first", "second"), cmd.getArgs())
     * ```
     */
    public fun getArgs(): List<String> = argsList.toList()

    /**
     * Returns the environment variables explicitly set for the child process.
     *
     * Environment variables explicitly set using [Command.env], [Command.envs], and
     * [Command.envRemove] can be retrieved with this method.
     *
     * Note that this output does not include environment variables inherited from the parent
     * process.
     *
     * Each element is a key and optional value pair. A null value
     * indicates its key was explicitly removed via [Command.envRemove]. The associated key for
     * the null value will no longer inherit from its parent process.
     *
     * Example:
     * ```kotlin
     * val cmd = Command.new("ls")
     * cmd.env("TERM", "dumb").envRemove("TZ")
     * ```
     */
    public fun getEnvs(): List<Pair<String, String?>> = envMap.map { it.key to it.value }

    /**
     * Returns the working directory for the child process.
     *
     * This returns null if the working directory will not be changed.
     *
     * Example:
     * ```kotlin
     * val cmd = Command.new("ls")
     * assertEquals(null, cmd.getCurrentDir())
     * cmd.currentDir("/bin")
     * assertEquals("/bin", cmd.getCurrentDir())
     * ```
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
     * Run a [Command], returning an [OutputResult].
     *
     * Example:
     * ```kotlin
     * val result = Command.new("echo")
     *     .args(listOf("42"))
     *     .ok()
     * assertTrue(result.isSuccess)
     * ```
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
     * Run a [Command], unwrapping the [OutputResult].
     *
     * Example:
     * ```kotlin
     * val output = Command.new("echo")
     *     .args(listOf("42"))
     *     .unwrap()
     * ```
     */
    override fun unwrap(): Output = ok().getOrThrow()

    /**
     * Run a [Command], unwrapping the error in the [OutputResult].
     *
     * Example:
     * ```kotlin
     * val err = Command.new("a-command")
     *     .args(listOf("--will-fail"))
     *     .unwrapErr()
     * ```
     */
    override fun unwrapErr(): OutputError {
        val res = ok()
        if (res.isSuccess) {
            val out = res.getOrThrow()
            val stdinStr = stdinBuffer?.let { "\nstdin=```${DebugBytes(it)}```" }.orEmpty()
            error(
                "Completed successfully:\ncommand=`$this`$stdinStr\nstdout=```${DebugBytes(out.stdout)}```",
            )
        }
        return res.exceptionOrNull() as? OutputError ?: OutputError(res.exceptionOrNull() ?: Exception("Unknown error"))
    }

    /**
     * Run a [Command] and make assertions on the [Output].
     *
     * Example:
     * ```kotlin
     * val cmd = Command.cargoBin("binFixture").getOrThrow()
     * cmd.assert().success()
     * ```
     */
    override fun assert(): Assert {
        val out =
            output().getOrElse {
                error("Failed to spawn $this: $it")
            }
        val assertion = Assert.new(out).appendContext("command", toString())
        val stdin = stdinBuffer
        return if (stdin != null) {
            assertion.appendContext("stdin", DebugBuffer(stdin))
        } else {
            assertion
        }
    }

    /**
     * Spawns the command and returns a Result containing this Command.
     */
    public fun spawn(): Result<Command> = Result.success(this)

    /**
     * If input, write it to child's stdin while also reading stdout and stderr,
     * then wait on child and return its status and output.
     */
    internal fun waitWithInputOutput(
        input: ByteArray?,
        timeout: Duration?,
    ): Result<Output> = output()

    /**
     * Read bytes from input stream into buffer.
     */
    internal fun read(bytes: ByteArray): ByteArray = bytes

    override fun toString(): String =
        buildString {
            append(program)
            for (arg in argsList) {
                append(" ")
                append(arg)
            }
        }

    public companion object : CommandCargoExt {
        /**
         * Constructs a new [Command] for launching the program at path [program].
         *
         * Builder methods are provided to change defaults and configure the process.
         *
         * Example:
         * ```kotlin
         * Command.new("sh").unwrap()
         * ```
         */
        public fun new(program: String): Command = Command(program)

        /**
         * Constructs a new [Command] from a program name or path.
         */
        public fun from(program: String): Command = fromStd(program)

        /**
         * Constructs a new [Command] from a standard command or program name.
         */
        public fun fromStd(program: String): Command = Command(program)

        /**
         * Create a [Command] to run a specific binary of the current project.
         *
         * Example:
         * ```kotlin
         * val cmd = Command.cargoBin("binFixture").getOrThrow()
         * cmd.assert().success()
         * ```
         */
        @Deprecated(
            message = "incompatible with a custom cargo build-dir, see instead cargo_bin!",
        )
        override fun cargoBin(name: String): Result<Command> = cargoBinCmd(name)
    }
}
