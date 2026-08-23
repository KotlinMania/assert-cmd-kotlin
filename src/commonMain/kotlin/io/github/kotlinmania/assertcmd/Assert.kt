// port-lint: source assert.rs
package io.github.kotlinmania.assertcmd

/**
 * Extension to wrap [Output] in an [Assert] assertion interface.
 */
fun Output.assert(): Assert = Assert(this)

/**
 * Assertion interface over [Output].
 */
class Assert(
    val output: Output,
    val context: MutableList<Pair<String, Any>> = mutableListOf(),
) {
    fun appendContext(name: String, contextValue: Any): Assert {
        context.add(name to contextValue)
        return this
    }

    fun success(): Assert =
        trySuccess().getOrThrow()

    fun trySuccess(): Result<Assert> =
        if (!output.status.success) {
            Result.failure(AssertError(this, AssertReason.UnexpectedFailure(output.status.code)))
        } else {
            Result.success(this)
        }

    fun failure(): Assert =
        tryFailure().getOrThrow()

    fun tryFailure(): Result<Assert> =
        if (output.status.success) {
            Result.failure(AssertError(this, AssertReason.UnexpectedSuccess))
        } else {
            Result.success(this)
        }

    fun interrupted(): Assert =
        tryInterrupted().getOrThrow()

    fun tryInterrupted(): Result<Assert> =
        if (output.status.code != null) {
            Result.failure(AssertError(this, AssertReason.UnexpectedCompletion))
        } else {
            Result.success(this)
        }

    fun code(expectedCode: Int): Assert =
        tryCode(expectedCode).getOrThrow()

    fun tryCode(expectedCode: Int): Result<Assert> {
        val actual =
            output.status.code
                ?: return Result.failure(AssertError(this, AssertReason.CommandInterrupted))
        return if (actual == expectedCode) {
            Result.success(this)
        } else {
            Result.failure(
                AssertError(
                    this,
                    AssertReason.UnexpectedReturnCode("expected $expectedCode, found $actual"),
                ),
            )
        }
    }

    fun code(expectedCodes: IntArray): Assert =
        tryCode(expectedCodes).getOrThrow()

    fun tryCode(expectedCodes: IntArray): Result<Assert> {
        val actual =
            output.status.code
                ?: return Result.failure(AssertError(this, AssertReason.CommandInterrupted))
        return if (actual in expectedCodes) {
            Result.success(this)
        } else {
            Result.failure(
                AssertError(
                    this,
                    AssertReason.UnexpectedReturnCode(
                        "expected one of ${expectedCodes.contentToString()}, found $actual",
                    ),
                ),
            )
        }
    }

    fun stdout(expected: String): Assert =
        tryStdout(expected).getOrThrow()

    fun tryStdout(expected: String): Result<Assert> {
        val actual = output.stdout.decodeToString()
        return if (actual == expected) {
            Result.success(this)
        } else {
            Result.failure(
                AssertError(
                    this,
                    AssertReason.UnexpectedStdout("expected \"$expected\", found \"$actual\""),
                ),
            )
        }
    }

    fun stdout(expected: ByteArray): Assert =
        tryStdout(expected).getOrThrow()

    fun tryStdout(expected: ByteArray): Result<Assert> =
        if (output.stdout.contentEquals(expected)) {
            Result.success(this)
        } else {
            Result.failure(
                AssertError(
                    this,
                    AssertReason.UnexpectedStdout("byte content mismatch"),
                ),
            )
        }

    fun stdout(predicate: (String) -> Boolean): Assert =
        tryStdoutPredicate(predicate).getOrThrow()

    fun tryStdoutPredicate(predicate: (String) -> Boolean): Result<Assert> {
        val actual = output.stdout.decodeToString()
        return if (predicate(actual)) {
            Result.success(this)
        } else {
            Result.failure(
                AssertError(
                    this,
                    AssertReason.UnexpectedStdout("predicate failed for \"$actual\""),
                ),
            )
        }
    }

    fun stderr(expected: String): Assert =
        tryStderr(expected).getOrThrow()

    fun tryStderr(expected: String): Result<Assert> {
        val actual = output.stderr.decodeToString()
        return if (actual == expected) {
            Result.success(this)
        } else {
            Result.failure(
                AssertError(
                    this,
                    AssertReason.UnexpectedStderr("expected \"$expected\", found \"$actual\""),
                ),
            )
        }
    }

    fun stderr(expected: ByteArray): Assert =
        tryStderr(expected).getOrThrow()

    fun tryStderr(expected: ByteArray): Result<Assert> =
        if (output.stderr.contentEquals(expected)) {
            Result.success(this)
        } else {
            Result.failure(
                AssertError(
                    this,
                    AssertReason.UnexpectedStderr("byte content mismatch"),
                ),
            )
        }

    fun stderr(predicate: (String) -> Boolean): Assert =
        tryStderrPredicate(predicate).getOrThrow()

    fun tryStderrPredicate(predicate: (String) -> Boolean): Result<Assert> {
        val actual = output.stderr.decodeToString()
        return if (predicate(actual)) {
            Result.success(this)
        } else {
            Result.failure(
                AssertError(
                    this,
                    AssertReason.UnexpectedStderr("predicate failed for \"$actual\""),
                ),
            )
        }
    }
}

/**
 * Reasons why an [Assert] check failed.
 */
sealed class AssertReason {
    data class UnexpectedFailure(
        val actualCode: Int?,
    ) : AssertReason()

    data object UnexpectedSuccess : AssertReason()

    data object UnexpectedCompletion : AssertReason()

    data object CommandInterrupted : AssertReason()

    data class UnexpectedReturnCode(
        val message: String,
    ) : AssertReason()

    data class UnexpectedStdout(
        val message: String,
    ) : AssertReason()

    data class UnexpectedStderr(
        val message: String,
    ) : AssertReason()
}

/**
 * Exception thrown when an [Assert] assertion fails.
 */
class AssertError(
    val assert: Assert,
    val reason: AssertReason,
) : AssertionError(formatMessage(assert, reason)) {
    companion object {
        private fun formatMessage(assert: Assert, reason: AssertReason): String {
            val palette = Palette.color()
            return buildString {
                for ((name, context) in assert.context) {
                    appendLine("${palette.key(name).renderStyled()}=`${palette.value(context).renderStyled()}`")
                }
                outputFmt(assert.output, this)
                when (reason) {
                    is AssertReason.UnexpectedFailure -> {
                        val code = reason.actualCode?.toString() ?: "<interrupted>"
                        appendLine("Unexpected failure: exit code $code")
                    }
                    is AssertReason.UnexpectedSuccess -> {
                        appendLine("Unexpected success")
                    }
                    is AssertReason.UnexpectedCompletion -> {
                        appendLine("Unexpected completion (command was expected to be interrupted)")
                    }
                    is AssertReason.CommandInterrupted -> {
                        appendLine("Command was interrupted")
                    }
                    is AssertReason.UnexpectedReturnCode -> {
                        appendLine("Unexpected return code: ${reason.message}")
                    }
                    is AssertReason.UnexpectedStdout -> {
                        appendLine("Unexpected stdout: ${reason.message}")
                    }
                    is AssertReason.UnexpectedStderr -> {
                        appendLine("Unexpected stderr: ${reason.message}")
                    }
                }
            }
        }
    }
}
