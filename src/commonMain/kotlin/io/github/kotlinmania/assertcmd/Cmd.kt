// port-lint: source cmd.rs
package io.github.kotlinmania.assertcmd

import kotlin.time.Duration

/**
 * Command customized for testing.
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

    public fun writeStdin(buffer: ByteArray): Command {
        this.stdinBuffer = buffer.copyOf()
        return this
    }

    public fun writeStdin(buffer: String): Command {
        this.stdinBuffer = buffer.encodeToByteArray()
        return this
    }

    public fun timeout(timeout: Duration): Command {
        this.timeoutDuration = timeout
        return this
    }

    public fun pipeStdin(content: ByteArray): Command = writeStdin(content)

    public fun pipeStdin(fileContent: String): Command = writeStdin(fileContent)

    public fun arg(arg: String): Command {
        argsList.add(arg)
        return this
    }

    public fun args(args: Iterable<String>): Command {
        for (a in args) {
            argsList.add(a)
        }
        return this
    }

    public fun args(vararg args: String): Command {
        for (a in args) {
            argsList.add(a)
        }
        return this
    }

    public fun env(key: String, value: String): Command {
        envMap[key] = value
        return this
    }

    public fun envs(vars: Iterable<Pair<String, String>>): Command {
        for ((k, v) in vars) {
            envMap[k] = v
        }
        return this
    }

    public fun envs(vars: Map<String, String>): Command {
        envMap.putAll(vars)
        return this
    }

    public fun envRemove(key: String): Command {
        envMap[key] = null
        return this
    }

    public fun envClear(): Command {
        envMap.clear()
        return this
    }

    public fun currentDir(dir: String): Command {
        this.currentDirectory = dir
        return this
    }

    public fun getProgram(): String = program

    public fun getArgs(): List<String> = argsList.toList()

    public fun getEnvs(): List<Pair<String, String?>> = envMap.map { it.key to it.value }

    public fun getCurrentDir(): String? = currentDirectory

    public fun setRunner(runner: (Command) -> Output): Command {
        this.runner = runner
        return this
    }

    public fun output(): Result<Output> {
        val customRunner = runner
        if (customRunner != null) {
            return try {
                Result.success(customRunner(this))
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
        // Default execution simulation for testing / multiplatform
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

    override fun unwrap(): Output = ok().getOrThrow()

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
        public fun new(program: String): Command = Command(program)

        public fun from(program: String): Command = fromStd(program)

        public fun fromStd(program: String): Command = Command(program)

        @Suppress("DEPRECATION")
        public fun cargoBin(name: String): Result<Command> = cargoBinCmd(name)
    }
}
