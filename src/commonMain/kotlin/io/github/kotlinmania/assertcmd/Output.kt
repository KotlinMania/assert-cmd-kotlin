// port-lint: source output.rs
package io.github.kotlinmania.assertcmd

/**
 * Exit status of a process.
 */
data class ExitStatus(
    val code: Int? = null,
    val success: Boolean = code == 0,
)

/**
 * Process output representation.
 */
data class Output(
    val status: ExitStatus,
    val stdout: ByteArray = ByteArray(0),
    val stderr: ByteArray = ByteArray(0),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Output) return false
        return status == other.status &&
            stdout.contentEquals(other.stdout) &&
            stderr.contentEquals(other.stderr)
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + stdout.contentHashCode()
        result = 31 * result + stderr.contentHashCode()
        return result
    }
}

/**
 * Converts an [Output] to a [Result] or unwraps it with descriptive messages.
 */
fun Output.ok(): Result<Output> =
    if (status.success) {
        Result.success(this)
    } else {
        Result.failure(OutputError(this))
    }

/**
 * Unwrap an [Output] or throw with a detailed diagnostic message.
 */
fun Output.unwrap(): Output =
    ok().getOrThrow()

/**
 * Unwrap an [Output] expecting failure, or throw with standard output diagnostics.
 */
fun Output.unwrapErr(): OutputError =
    if (status.success) {
        error(
            "Command completed successfully\nstdout=```${DebugBytes(stdout)}```",
        )
    } else {
        OutputError(this)
    }

/**
 * Detailed error produced by command execution or assertion failure.
 */
class OutputError internal constructor(
    private var cmd: String? = null,
    private var stdin: ByteArray? = null,
    private val errorCause: OutputCause,
) : Exception((errorCause as? OutputCause.Unexpected)?.throwable) {
    constructor(output: Output) : this(
        cmd = null,
        stdin = null,
        errorCause = OutputCause.Expected(output),
    )

    constructor(cause: Throwable) : this(
        cmd = null,
        stdin = null,
        errorCause = OutputCause.Unexpected(cause),
    )

    fun setCmd(cmd: String): OutputError {
        this.cmd = cmd
        return this
    }

    fun setStdin(stdin: ByteArray): OutputError {
        this.stdin = stdin.copyOf()
        return this
    }

    fun asOutput(): Output? =
        when (errorCause) {
            is OutputCause.Expected -> errorCause.output
            is OutputCause.Unexpected -> null
        }

    override val message: String
        get() {
            val palette = Palette.color()
            return buildString {
                if (cmd != null) {
                    appendLine("${palette.key("command").renderStyled()}=${palette.value(cmd!!).renderStyled()}")
                }
                if (stdin != null) {
                    appendLine("${palette.key("stdin").renderStyled()}=${palette.value(DebugBytes(stdin!!)).renderStyled()}")
                }
                when (errorCause) {
                    is OutputCause.Expected -> outputFmt(errorCause.output, this)
                    is OutputCause.Unexpected -> append(errorCause.throwable.message ?: errorCause.throwable.toString())
                }
            }
        }

    override fun toString(): String = message
}

internal sealed class OutputCause {
    data class Expected(
        val output: Output,
    ) : OutputCause()

    data class Unexpected(
        val throwable: Throwable,
    ) : OutputCause()
}

internal fun outputFmt(output: Output, builder: StringBuilder) {
    val palette = Palette.color()
    val codeVal = output.status.code?.toString() ?: "<interrupted>"
    builder.appendLine("${palette.key("code").renderStyled()}=${palette.value(codeVal).renderStyled()}")
    builder.appendLine("${palette.key("stdout").renderStyled()}=${palette.value(DebugBytes(output.stdout)).renderStyled()}")
    builder.appendLine("${palette.key("stderr").renderStyled()}=${palette.value(DebugBytes(output.stderr)).renderStyled()}")
}

internal class DebugBytes(
    private val bytes: ByteArray,
) {
    override fun toString(): String =
        buildString {
            formatBytes(bytes, this)
        }
}

internal class DebugBuffer(
    private val buffer: ByteArray,
) {
    override fun toString(): String =
        buildString {
            formatBytes(buffer, this)
        }
}

private const val LINES_MIN_OVERFLOW: Int = 80
private const val LINES_MAX_START: Int = 20
private const val LINES_MAX_END: Int = 40
private const val LINES_MAX_PRINTED: Int = LINES_MAX_START + LINES_MAX_END

private const val BYTES_MIN_OVERFLOW: Int = 8192
private const val BYTES_MAX_START: Int = 2048
private const val BYTES_MAX_END: Int = 2048
private const val BYTES_MAX_PRINTED: Int = BYTES_MAX_START + BYTES_MAX_END

internal fun formatBytes(data: ByteArray, builder: StringBuilder) {
    val lines = splitLinesWithTerminator(data)
    val linesTotal = lines.size
    val multiline = linesTotal > 1

    if (linesTotal >= LINES_MIN_OVERFLOW) {
        val linesOmitted = linesTotal - LINES_MAX_PRINTED
        val startLines = lines.take(LINES_MAX_START)
        val endLines = lines.drop(LINES_MAX_START + linesOmitted)
        builder.appendLine("<$linesTotal lines total>")
        writeDebugBstrs(builder, true, startLines)
        builder.appendLine("<$linesOmitted lines omitted>")
        writeDebugBstrs(builder, true, endLines)
    } else if (data.size >= BYTES_MIN_OVERFLOW) {
        builder.append("<${data.size} bytes total>${if (multiline) "\n" else ""}")
        writeDebugBstrs(
            builder,
            multiline,
            splitLinesWithTerminator(data.copyOfRange(0, BYTES_MAX_START)),
        )
        builder.append("<${data.size - BYTES_MAX_PRINTED} bytes omitted>${if (multiline) "\n" else ""}")
        writeDebugBstrs(
            builder,
            multiline,
            splitLinesWithTerminator(data.copyOfRange(data.size - BYTES_MAX_END, data.size)),
        )
    } else {
        writeDebugBstrs(builder, multiline, lines)
    }
}

private fun splitLinesWithTerminator(data: ByteArray): List<ByteArray> {
    if (data.isEmpty()) return emptyList()
    val result = mutableListOf<ByteArray>()
    var start = 0
    for (i in data.indices) {
        if (data[i] == '\n'.code.toByte()) {
            result.add(data.copyOfRange(start, i + 1))
            start = i + 1
        }
    }
    if (start < data.size) {
        result.add(data.copyOfRange(start, data.size))
    }
    return result
}

private fun writeDebugBstrs(
    builder: StringBuilder,
    multiline: Boolean,
    lines: List<ByteArray>,
) {
    if (multiline) {
        builder.appendLine("```")
        for (rawLine in lines) {
            val hasNewline = rawLine.isNotEmpty() && rawLine.last() == '\n'.code.toByte()
            val line = if (hasNewline) rawLine.copyOfRange(0, rawLine.size - 1) else rawLine
            val s = escapeBstr(line)
            builder.append(s)
            if (hasNewline) {
                builder.append("\n")
            }
        }
        builder.appendLine("```")
    } else {
        val line = lines.firstOrNull() ?: ByteArray(0)
        builder.append("\"${escapeBstr(line)}\"")
    }
}

private fun escapeBstr(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (b in bytes) {
        when (val c = (b.toInt() and 0xFF).toChar()) {
            '\\' -> sb.append("\\\\")
            '"' -> sb.append("\\\"")
            '\t' -> sb.append("\\t")
            '\r' -> sb.append("\\r")
            '\n' -> sb.append("\\n")
            in ' '..'~' -> sb.append(c)
            else -> {
                val hex = (b.toInt() and 0xFF).toString(16).padStart(2, '0')
                sb.append("\\x$hex")
            }
        }
    }
    return sb.toString()
}
