// port-lint: source assert_cmd/src/assert.rs
package io.github.kotlinmania.assertcmd

/**
 * Assert the state of an [Output].
 *
 * Example:
 * ```
 * val cmd = Command.cargoBin("binFixture")
 * cmd.assert().success()
 * ```
 */
public interface OutputAssertExt {
    /**
     * Wrap with an interface that provides assertions on the [Output].
     *
     * Example:
     * ```
     * val cmd = Command.cargoBin("binFixture")
     * cmd.assert().success()
     * ```
     */
    public fun assert(): Assert
}

/**
 * Extension to wrap [Output] in an [Assert] assertion interface.
 */
public fun Output.assert(): Assert = Assert.new(this)

/**
 * [Assert] represented as a [Result].
 *
 * Produced by the `try*` variants of the [Assert] methods.
 *
 * Example:
 * ```
 * val result = Command("echo").assert().trySuccess()
 * assertTrue(result.isSuccess)
 * ```
 */
public typealias AssertResult = Result<Assert>

/**
 * Assert the state of an [Output].
 *
 * Create an [Assert] through the [OutputAssertExt] interface.
 *
 * Example:
 * ```
 * val cmd = Command.cargoBin("binFixture")
 * cmd.assert().success()
 * ```
 */
public class Assert(
    /** Access the contained [Output]. */
    public val output: Output,
) {
    internal val context: MutableList<Pair<String, Any>> = mutableListOf()

    /**
     * Clarify failures with additional context.
     *
     * Example:
     * ```
     * Command.cargoBin("binFixture")
     *     .assert()
     *     .appendContext("main", "no args")
     *     .success()
     * ```
     */
    public fun appendContext(name: String, contextValue: Any): Assert {
        context.add(name to contextValue)
        return this
    }

    /**
     * Access the contained [Output].
     */
    @kotlin.jvm.JvmName("outputValue")
    public fun getOutput(): Output = output

    /**
     * Convert this assertion state and reason into an [AssertError].
     */
    public fun intoError(reason: AssertReason): AssertError = AssertError(this, reason)

    /**
     * Ensure the command succeeded.
     *
     * Example:
     * ```
     * Command.cargoBin("binFixture")
     *     .assert()
     *     .success()
     * ```
     */
    public fun success(): Assert = trySuccess().getOrThrow()

    /**
     * Variant of [Assert.success] that returns an [AssertResult].
     */
    public fun trySuccess(): AssertResult =
        if (!output.status.success) {
            Result.failure(intoError(AssertReason.UnexpectedFailure(output.status.code)))
        } else {
            Result.success(this)
        }

    /**
     * Ensure the command failed.
     *
     * Example:
     * ```
     * Command.cargoBin("binFixture")
     *     .env("exit", "1")
     *     .assert()
     *     .failure()
     * ```
     */
    public fun failure(): Assert = tryFailure().getOrThrow()

    /**
     * Variant of [Assert.failure] that returns an [AssertResult].
     */
    public fun tryFailure(): AssertResult =
        if (output.status.success) {
            Result.failure(intoError(AssertReason.UnexpectedSuccess))
        } else {
            Result.success(this)
        }

    /**
     * Ensure the command aborted before returning a code.
     */
    public fun interrupted(): Assert = tryInterrupted().getOrThrow()

    /**
     * Variant of [Assert.interrupted] that returns an [AssertResult].
     */
    public fun tryInterrupted(): AssertResult =
        if (output.status.code != null) {
            Result.failure(intoError(AssertReason.UnexpectedCompletion))
        } else {
            Result.success(this)
        }

    /**
     * Ensure the command returned the expected code.
     *
     * Example:
     * ```
     * Command.cargoBin("binFixture")
     *     .env("exit", "42")
     *     .assert()
     *     .code(42)
     * ```
     */
    public fun code(expectedCode: Int): Assert = tryCode(expectedCode).getOrThrow()

    /**
     * Ensure the command returned one of the expected codes.
     *
     * Example:
     * ```
     * Command.cargoBin("binFixture")
     *     .env("exit", "42")
     *     .assert()
     *     .code(intArrayOf(2, 42))
     * ```
     */
    public fun code(expectedCodes: IntArray): Assert = tryCode(expectedCodes).getOrThrow()

    /**
     * Ensure the command returned one of the expected codes in the list.
     */
    public fun code(expectedCodes: List<Int>): Assert = tryCode(expectedCodes).getOrThrow()

    /**
     * Ensure the command returned a code matching the given predicate.
     */
    public fun code(pred: IntoCodePredicate): Assert = tryCode(pred).getOrThrow()

    /**
     * Variant of [Assert.code] that returns an [AssertResult].
     */
    public fun tryCode(expectedCode: Int): AssertResult = tryCode(EqCodePredicate.new(expectedCode))

    /**
     * Variant of [Assert.code] with array that returns an [AssertResult].
     */
    public fun tryCode(expectedCodes: IntArray): AssertResult = tryCode(InCodePredicate.new(expectedCodes.toList()))

    /**
     * Variant of [Assert.code] with list that returns an [AssertResult].
     */
    public fun tryCode(expectedCodes: List<Int>): AssertResult = tryCode(InCodePredicate.new(expectedCodes))

    /**
     * Variant of [Assert.code] with predicate that returns an [AssertResult].
     */
    public fun tryCode(pred: IntoCodePredicate): AssertResult = codeImpl(pred.intoCode())

    /**
     * Implementation helper for exit code predicate evaluation.
     */
    public fun codeImpl(pred: Predicate<Int>): AssertResult {
        val actualCode = output.status.code ?: return Result.failure(intoError(AssertReason.CommandInterrupted))
        val case = pred.findCase(false, actualCode)
        return if (case != null) {
            Result.failure(intoError(AssertReason.UnexpectedReturnCode(case.tree())))
        } else {
            Result.success(this)
        }
    }

    /**
     * Ensure the command wrote the expected data to stdout as a string.
     *
     * Example:
     * ```
     * Command.cargoBin("binFixture")
     *     .env("stdout", "hello")
     *     .assert()
     *     .stdout("hello\n")
     * ```
     */
    public fun stdout(expected: String): Assert = tryStdout(expected).getOrThrow()

    /**
     * Ensure the command wrote the expected data to stdout as raw bytes.
     */
    public fun stdout(expected: ByteArray): Assert = tryStdout(expected).getOrThrow()

    /**
     * Ensure the command wrote data to stdout satisfying the given [IntoOutputPredicate].
     */
    public fun stdout(pred: IntoOutputPredicate): Assert = tryStdout(pred).getOrThrow()

    /**
     * Ensure the command wrote data to stdout satisfying the given string predicate.
     */
    public fun stdout(pred: Predicate<String>): Assert = tryStdout(StrOutputPredicate.new(pred)).getOrThrow()

    /**
     * Ensure the command wrote data to stdout satisfying the given predicate lambda.
     */
    public fun stdout(pred: (String) -> Boolean): Assert = stdout(Predicate { pred(it) })

    /**
     * Variant of [Assert.stdout] with string that returns an [AssertResult].
     */
    public fun tryStdout(expected: String): AssertResult = tryStdout(StrContentOutputPredicate.fromString(expected))

    /**
     * Variant of [Assert.stdout] with bytes that returns an [AssertResult].
     */
    public fun tryStdout(expected: ByteArray): AssertResult = tryStdout(BytesContentOutputPredicate.new(expected))

    /**
     * Variant of [Assert.stdout] with predicate that returns an [AssertResult].
     */
    public fun tryStdout(pred: IntoOutputPredicate): AssertResult = stdoutImpl(pred.intoOutput())

    /**
     * Variant of [Assert.stdout] with string predicate that returns an [AssertResult].
     */
    public fun tryStdout(pred: Predicate<String>): AssertResult = stdoutImpl(StrOutputPredicate.new(pred))

    /**
     * Implementation helper for stdout predicate evaluation.
     */
    public fun stdoutImpl(pred: Predicate<ByteArray>): AssertResult {
        val actual = output.stdout
        val case = pred.findCase(false, actual)
        return if (case != null) {
            Result.failure(intoError(AssertReason.UnexpectedStdout(case.tree())))
        } else {
            Result.success(this)
        }
    }

    /**
     * Ensure the command wrote the expected data to stderr as a string.
     *
     * Example:
     * ```
     * Command.cargoBin("binFixture")
     *     .env("stderr", "world")
     *     .assert()
     *     .stderr("world\n")
     * ```
     */
    public fun stderr(expected: String): Assert = tryStderr(expected).getOrThrow()

    /**
     * Ensure the command wrote the expected data to stderr as raw bytes.
     */
    public fun stderr(expected: ByteArray): Assert = tryStderr(expected).getOrThrow()

    /**
     * Ensure the command wrote data to stderr satisfying the given [IntoOutputPredicate].
     */
    public fun stderr(pred: IntoOutputPredicate): Assert = tryStderr(pred).getOrThrow()

    /**
     * Ensure the command wrote data to stderr satisfying the given string predicate.
     */
    public fun stderr(pred: Predicate<String>): Assert = tryStderr(StrOutputPredicate.new(pred)).getOrThrow()

    /**
     * Ensure the command wrote data to stderr satisfying the given predicate lambda.
     */
    public fun stderr(pred: (String) -> Boolean): Assert = stderr(Predicate { pred(it) })

    /**
     * Variant of [Assert.stderr] with string that returns an [AssertResult].
     */
    public fun tryStderr(expected: String): AssertResult = tryStderr(StrContentOutputPredicate.fromString(expected))

    /**
     * Variant of [Assert.stderr] with bytes that returns an [AssertResult].
     */
    public fun tryStderr(expected: ByteArray): AssertResult = tryStderr(BytesContentOutputPredicate.new(expected))

    /**
     * Variant of [Assert.stderr] with predicate that returns an [AssertResult].
     */
    public fun tryStderr(pred: IntoOutputPredicate): AssertResult = stderrImpl(pred.intoOutput())

    /**
     * Variant of [Assert.stderr] with string predicate that returns an [AssertResult].
     */
    public fun tryStderr(pred: Predicate<String>): AssertResult = stderrImpl(StrOutputPredicate.new(pred))

    /**
     * Implementation helper for stderr predicate evaluation.
     */
    public fun stderrImpl(pred: Predicate<ByteArray>): AssertResult {
        val actual = output.stderr
        val case = pred.findCase(false, actual)
        return if (case != null) {
            Result.failure(intoError(AssertReason.UnexpectedStderr(case.tree())))
        } else {
            Result.success(this)
        }
    }

    /**
     * Formats this assertion as a string.
     */
    public fun fmt(): String = toString()

    override fun toString(): String {
        val palette = Palette.color()
        return buildString {
            for ((name, contextVal) in context) {
                appendLine("${palette.key(name).renderStyled()}=`${palette.value(contextVal).renderStyled()}`")
            }
            outputFmt(output, this)
        }
    }

    public companion object {
        /**
         * Create an [Assert] for a given [Output].
         */
        public fun new(output: Output): Assert = Assert(output)
    }
}

/**
 * Convert an [IntoCodePredicate] into a [Predicate] of [Int].
 */
public fun convertCode(pred: IntoCodePredicate): Predicate<Int> = pred.intoCode()

/**
 * Convert an [IntoOutputPredicate] into a [Predicate] of [ByteArray].
 */
public fun convertOutput(pred: IntoOutputPredicate): Predicate<ByteArray> = pred.intoOutput()

/**
 * Generic predicate interface.
 */
public fun interface Predicate<in T> {
    /**
     * Evaluates whether the given item satisfies this predicate.
     */
    public fun eval(item: T): Boolean

    /**
     * Find a case that matches the expected boolean outcome.
     */
    public fun findCase(expected: Boolean, variable: T): Case? =
        if (eval(variable) == expected) Case(this, expected) else null

    /**
     * Parameters associated with this predicate.
     */
    public fun parameters(): Iterator<Parameter> = emptyList<Parameter>().iterator()

    /**
     * Nested children predicates of this predicate.
     */
    public fun children(): Iterator<Child> = emptyList<Child>().iterator()
}

/**
 * Parameter describing a predicate attribute.
 */
public class Parameter(
    public val name: String,
    public val value: Any?,
)

/**
 * Child predicate node in a predicate tree.
 */
public class Child(
    public val name: String,
    public val predicate: Predicate<*>,
)

/**
 * Evaluation case result for a predicate.
 */
public class Case(
    public val predicate: Predicate<*>,
    public val result: Boolean,
) {
    /**
     * Convert this case into a [CaseTree].
     */
    public fun tree(): CaseTree = CaseTree(this)
}

/**
 * Case tree representation for failure explanations.
 */
public class CaseTree(
    public val case: Case,
) {
    /**
     * Format the case tree as a string.
     */
    public fun fmt(): String = toString()

    override fun toString(): String = "${case.predicate}"
}

/**
 * Used by [Assert.code] to convert a value into a [Predicate] of [Int].
 *
 * Example:
 * ```
 * val cmd = Command.cargoBin("binFixture")
 * cmd.assert().code(42)
 * ```
 */
public interface IntoCodePredicate {
    /**
     * Convert to a predicate for testing a program's exit code.
     */
    public fun intoCode(): Predicate<Int>
}

/**
 * Predicate checking exact integer equality on exit codes.
 */
public class EqCodePredicate(
    public val expected: Int,
) : Predicate<Int>,
    IntoCodePredicate {
    override fun eval(item: Int): Boolean = item == expected

    override fun intoCode(): EqCodePredicate = this

    /**
     * Format this predicate as a string.
     */
    public fun fmt(): String = toString()

    override fun toString(): String = "var == $expected"

    public companion object {
        /**
         * Create a new [EqCodePredicate] for the given expected value.
         */
        public fun new(value: Int): EqCodePredicate = EqCodePredicate(value)
    }
}

/**
 * Predicate checking containment in a list of allowed integer exit codes.
 */
public class InCodePredicate(
    public val expected: List<Int>,
) : Predicate<Int>,
    IntoCodePredicate {
    override fun eval(item: Int): Boolean = item in expected

    override fun intoCode(): InCodePredicate = this

    /**
     * Format this predicate as a string.
     */
    public fun fmt(): String = toString()

    override fun toString(): String = "var in $expected"

    public companion object {
        /**
         * Create a new [InCodePredicate] for the given expected iterable.
         */
        public fun new(value: Iterable<Int>): InCodePredicate = InCodePredicate(value.toList())
    }
}

/**
 * Used by [Assert.stdout] and [Assert.stderr] to convert a value into a [Predicate] of [ByteArray].
 */
public interface IntoOutputPredicate {
    /**
     * Convert to a predicate for testing command output data.
     */
    public fun intoOutput(): Predicate<ByteArray>
}

/**
 * Predicate checking exact byte equality on output streams.
 */
public class BytesContentOutputPredicate(
    public val expected: ByteArray,
) : Predicate<ByteArray>,
    IntoOutputPredicate {
    override fun eval(item: ByteArray): Boolean = expected.contentEquals(item)

    override fun intoOutput(): BytesContentOutputPredicate = this

    /**
     * Format this predicate as a string.
     */
    public fun fmt(): String = toString()

    override fun toString(): String = "var == ${expected.decodeToString()}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BytesContentOutputPredicate) return false
        return expected.contentEquals(other.expected)
    }

    override fun hashCode(): Int = expected.contentHashCode()

    public companion object {
        /**
         * Create a new [BytesContentOutputPredicate] from a byte array.
         */
        public fun new(value: ByteArray): BytesContentOutputPredicate = BytesContentOutputPredicate(value)

        /**
         * Create a new [BytesContentOutputPredicate] from a list of bytes.
         */
        public fun fromVec(value: List<Byte>): BytesContentOutputPredicate = BytesContentOutputPredicate(value.toByteArray())
    }
}

/**
 * Predicate checking string content on output streams decoded as UTF-8.
 */
public class StrContentOutputPredicate(
    public val expected: String,
) : Predicate<ByteArray>,
    IntoOutputPredicate {
    override fun eval(item: ByteArray): Boolean = expected == item.decodeToString()

    override fun intoOutput(): StrContentOutputPredicate = this

    /**
     * Format this predicate as a string.
     */
    public fun fmt(): String = toString()

    override fun toString(): String = "var == \"$expected\""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StrContentOutputPredicate) return false
        return expected == other.expected
    }

    override fun hashCode(): Int = expected.hashCode()

    public companion object {
        /**
         * Create a new [StrContentOutputPredicate] from a string slice.
         */
        public fun fromStr(value: String): StrContentOutputPredicate = StrContentOutputPredicate(value)

        /**
         * Create a new [StrContentOutputPredicate] from an owned string.
         */
        public fun fromString(value: String): StrContentOutputPredicate = StrContentOutputPredicate(value)
    }
}

/**
 * Adapter converting a string predicate into a byte array predicate via UTF-8 decoding.
 */
public class StrOutputPredicate(
    public val predicate: Predicate<String>,
) : Predicate<ByteArray>,
    IntoOutputPredicate {
    override fun eval(item: ByteArray): Boolean = predicate.eval(item.decodeToString())

    override fun intoOutput(): StrOutputPredicate = this

    /**
     * Format this predicate as a string.
     */
    public fun fmt(): String = toString()

    override fun toString(): String = predicate.toString()

    public companion object {
        /**
         * Create a new [StrOutputPredicate] wrapping the given string predicate.
         */
        public fun new(pred: Predicate<String>): StrOutputPredicate = StrOutputPredicate(pred)
    }
}

/**
 * Error produced by assertion failures (see [AssertResult]).
 */
public class AssertError(
    public val assert: Assert,
    public val reason: AssertReason,
) : Exception(reason.formatMessage(assert)) {
    /**
     * Panic / throw this assertion failure exception.
     */
    public fun panic(): Nothing = throw this

    /**
     * Format this error as a string.
     */
    public fun fmt(): String = message ?: ""

    public companion object {
        /**
         * Panic / throw the given assertion error.
         */
        public fun panic(err: AssertError): Nothing = err.panic()
    }
}

/**
 * Detailed reason for an assertion failure.
 */
public sealed class AssertReason {
    /**
     * Format this failure reason as a string.
     */
    public fun fmt(): String = toString()

    /**
     * Unexpected non-zero or failure exit status.
     */
    public data class UnexpectedFailure(
        val actualCode: Int?,
    ) : AssertReason()

    /**
     * Unexpected successful exit status when failure was expected.
     */
    public object UnexpectedSuccess : AssertReason()

    /**
     * Unexpected normal completion when interruption was expected.
     */
    public object UnexpectedCompletion : AssertReason()

    /**
     * Command was interrupted and exited without a status code.
     */
    public object CommandInterrupted : AssertReason()

    /**
     * Return code did not match expectations.
     */
    public data class UnexpectedReturnCode(
        val caseTree: CaseTree,
    ) : AssertReason()

    /**
     * Standard output content did not match expectations.
     */
    public data class UnexpectedStdout(
        val caseTree: CaseTree,
    ) : AssertReason()

    /**
     * Standard error content did not match expectations.
     */
    public data class UnexpectedStderr(
        val caseTree: CaseTree,
    ) : AssertReason()

    internal fun formatMessage(assert: Assert): String =
        buildString {
            when (this@AssertReason) {
                is UnexpectedFailure -> {
                    appendLine("Unexpected failure.")
                    appendLine("code=${actualCode ?: "<interrupted>"}")
                    appendLine("stderr=```${DebugBytes(assert.output.stderr)}```")
                }
                is UnexpectedSuccess -> appendLine("Unexpected success")
                is UnexpectedCompletion -> appendLine("Unexpected completion")
                is CommandInterrupted -> appendLine("Command interrupted")
                is UnexpectedReturnCode -> appendLine("Unexpected return code, failed $caseTree")
                is UnexpectedStdout -> appendLine("Unexpected stdout, failed $caseTree")
                is UnexpectedStderr -> appendLine("Unexpected stderr, failed $caseTree")
            }
            append(assert.toString())
        }
}

