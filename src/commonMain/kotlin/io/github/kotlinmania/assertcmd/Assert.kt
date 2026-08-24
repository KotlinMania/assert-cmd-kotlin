// port-lint: source assert.rs
package io.github.kotlinmania.assertcmd

/**
 * Assert the state of an [Output].
 */
public interface OutputAssertExt {
    /**
     * Wrap with an interface that provides assertions on the [Output].
     */
    public fun assert(): Assert
}

/**
 * Extension to wrap [Output] in an [Assert] assertion interface.
 */
public fun Output.assert(): Assert = Assert.new(this)

/**
 * Result of an assertion operation.
 */
public typealias AssertResult = Result<Assert>

/**
 * Assertion interface over [Output].
 */
public class Assert(
    public val output: Output,
) {
    internal val context: MutableList<Pair<String, Any>> = mutableListOf()

    public fun appendContext(name: String, contextValue: Any): Assert {
        context.add(name to contextValue)
        return this
    }

    @kotlin.jvm.JvmName("outputValue")
    public fun getOutput(): Output = output

    public fun intoError(reason: AssertReason): AssertError = AssertError(this, reason)

    public fun success(): Assert = trySuccess().getOrThrow()

    public fun trySuccess(): AssertResult =
        if (!output.status.success) {
            Result.failure(intoError(AssertReason.UnexpectedFailure(output.status.code)))
        } else {
            Result.success(this)
        }

    public fun failure(): Assert = tryFailure().getOrThrow()

    public fun tryFailure(): AssertResult =
        if (output.status.success) {
            Result.failure(intoError(AssertReason.UnexpectedSuccess))
        } else {
            Result.success(this)
        }

    public fun interrupted(): Assert = tryInterrupted().getOrThrow()

    public fun tryInterrupted(): AssertResult =
        if (output.status.code != null) {
            Result.failure(intoError(AssertReason.UnexpectedCompletion))
        } else {
            Result.success(this)
        }

    public fun code(expectedCode: Int): Assert = tryCode(expectedCode).getOrThrow()

    public fun code(expectedCodes: IntArray): Assert = tryCode(expectedCodes).getOrThrow()

    public fun code(expectedCodes: List<Int>): Assert = tryCode(expectedCodes).getOrThrow()

    public fun code(pred: IntoCodePredicate): Assert = tryCode(pred).getOrThrow()

    public fun tryCode(expectedCode: Int): AssertResult = tryCode(EqCodePredicate.new(expectedCode))

    public fun tryCode(expectedCodes: IntArray): AssertResult = tryCode(InCodePredicate.new(expectedCodes.toList()))

    public fun tryCode(expectedCodes: List<Int>): AssertResult = tryCode(InCodePredicate.new(expectedCodes))

    public fun tryCode(pred: IntoCodePredicate): AssertResult = codeImpl(pred.intoCode())

    public fun codeImpl(pred: Predicate<Int>): AssertResult {
        val actualCode = output.status.code ?: return Result.failure(intoError(AssertReason.CommandInterrupted))
        val case = pred.findCase(false, actualCode)
        return if (case != null) {
            Result.failure(intoError(AssertReason.UnexpectedReturnCode(case.tree())))
        } else {
            Result.success(this)
        }
    }

    public fun stdout(expected: String): Assert = tryStdout(expected).getOrThrow()

    public fun stdout(expected: ByteArray): Assert = tryStdout(expected).getOrThrow()

    public fun stdout(pred: IntoOutputPredicate): Assert = tryStdout(pred).getOrThrow()

    public fun stdout(pred: Predicate<String>): Assert = tryStdout(StrOutputPredicate.new(pred)).getOrThrow()

    public fun stdout(pred: (String) -> Boolean): Assert = stdout(Predicate { pred(it) })

    public fun tryStdout(expected: String): AssertResult = tryStdout(StrContentOutputPredicate.fromString(expected))

    public fun tryStdout(expected: ByteArray): AssertResult = tryStdout(BytesContentOutputPredicate.new(expected))

    public fun tryStdout(pred: IntoOutputPredicate): AssertResult = stdoutImpl(pred.intoOutput())

    public fun tryStdout(pred: Predicate<String>): AssertResult = stdoutImpl(StrOutputPredicate.new(pred))

    public fun stdoutImpl(pred: Predicate<ByteArray>): AssertResult {
        val actual = output.stdout
        val case = pred.findCase(false, actual)
        return if (case != null) {
            Result.failure(intoError(AssertReason.UnexpectedStdout(case.tree())))
        } else {
            Result.success(this)
        }
    }

    public fun stderr(expected: String): Assert = tryStderr(expected).getOrThrow()

    public fun stderr(expected: ByteArray): Assert = tryStderr(expected).getOrThrow()

    public fun stderr(pred: IntoOutputPredicate): Assert = tryStderr(pred).getOrThrow()

    public fun stderr(pred: Predicate<String>): Assert = tryStderr(StrOutputPredicate.new(pred)).getOrThrow()

    public fun stderr(pred: (String) -> Boolean): Assert = stderr(Predicate { pred(it) })

    public fun tryStderr(expected: String): AssertResult = tryStderr(StrContentOutputPredicate.fromString(expected))

    public fun tryStderr(expected: ByteArray): AssertResult = tryStderr(BytesContentOutputPredicate.new(expected))

    public fun tryStderr(pred: IntoOutputPredicate): AssertResult = stderrImpl(pred.intoOutput())

    public fun tryStderr(pred: Predicate<String>): AssertResult = stderrImpl(StrOutputPredicate.new(pred))

    public fun stderrImpl(pred: Predicate<ByteArray>): AssertResult {
        val actual = output.stderr
        val case = pred.findCase(false, actual)
        return if (case != null) {
            Result.failure(intoError(AssertReason.UnexpectedStderr(case.tree())))
        } else {
            Result.success(this)
        }
    }

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
        public fun new(output: Output): Assert = Assert(output)
    }
}

public fun convertCode(pred: IntoCodePredicate): Predicate<Int> = pred.intoCode()

public fun convertOutput(pred: IntoOutputPredicate): Predicate<ByteArray> = pred.intoOutput()

/**
 * Generic predicate interface.
 */
public fun interface Predicate<in T> {
    public fun eval(item: T): Boolean

    public fun findCase(expected: Boolean, variable: T): Case? =
        if (eval(variable) == expected) Case(this, expected) else null

    public fun parameters(): Iterator<Parameter> = emptyList<Parameter>().iterator()

    public fun children(): Iterator<Child> = emptyList<Child>().iterator()
}

public class Parameter(
    public val name: String,
    public val value: Any?,
)

public class Child(
    public val name: String,
    public val predicate: Predicate<*>,
)

public class Case(
    public val predicate: Predicate<*>,
    public val result: Boolean,
) {
    public fun tree(): CaseTree = CaseTree(this)
}

public class CaseTree(
    public val case: Case,
) {
    public fun fmt(): String = toString()

    override fun toString(): String = "${case.predicate}"
}

public interface IntoCodePredicate {
    public fun intoCode(): Predicate<Int>
}

public class EqCodePredicate(
    public val expected: Int,
) : Predicate<Int>,
    IntoCodePredicate {
    override fun eval(item: Int): Boolean = item == expected

    override fun intoCode(): EqCodePredicate = this

    public fun fmt(): String = toString()

    override fun toString(): String = "var == $expected"

    public companion object {
        public fun new(value: Int): EqCodePredicate = EqCodePredicate(value)
    }
}

public class InCodePredicate(
    public val expected: List<Int>,
) : Predicate<Int>,
    IntoCodePredicate {
    override fun eval(item: Int): Boolean = item in expected

    override fun intoCode(): InCodePredicate = this

    public fun fmt(): String = toString()

    override fun toString(): String = "var in $expected"

    public companion object {
        public fun new(value: Iterable<Int>): InCodePredicate = InCodePredicate(value.toList())
    }
}

public interface IntoOutputPredicate {
    public fun intoOutput(): Predicate<ByteArray>
}

public class BytesContentOutputPredicate(
    public val expected: ByteArray,
) : Predicate<ByteArray>,
    IntoOutputPredicate {
    override fun eval(item: ByteArray): Boolean = expected.contentEquals(item)

    override fun intoOutput(): BytesContentOutputPredicate = this

    public fun fmt(): String = toString()

    override fun toString(): String = "var == ${expected.decodeToString()}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BytesContentOutputPredicate) return false
        return expected.contentEquals(other.expected)
    }

    override fun hashCode(): Int = expected.contentHashCode()

    public companion object {
        public fun new(value: ByteArray): BytesContentOutputPredicate = BytesContentOutputPredicate(value)

        public fun fromVec(value: List<Byte>): BytesContentOutputPredicate = BytesContentOutputPredicate(value.toByteArray())
    }
}

public class StrContentOutputPredicate(
    public val expected: String,
) : Predicate<ByteArray>,
    IntoOutputPredicate {
    override fun eval(item: ByteArray): Boolean = expected == item.decodeToString()

    override fun intoOutput(): StrContentOutputPredicate = this

    public fun fmt(): String = toString()

    override fun toString(): String = "var == \"$expected\""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StrContentOutputPredicate) return false
        return expected == other.expected
    }

    override fun hashCode(): Int = expected.hashCode()

    public companion object {
        public fun fromStr(value: String): StrContentOutputPredicate = StrContentOutputPredicate(value)

        public fun fromString(value: String): StrContentOutputPredicate = StrContentOutputPredicate(value)
    }
}

public class StrOutputPredicate(
    public val predicate: Predicate<String>,
) : Predicate<ByteArray>,
    IntoOutputPredicate {
    override fun eval(item: ByteArray): Boolean = predicate.eval(item.decodeToString())

    override fun intoOutput(): StrOutputPredicate = this

    public fun fmt(): String = toString()

    override fun toString(): String = predicate.toString()

    public companion object {
        public fun new(pred: Predicate<String>): StrOutputPredicate = StrOutputPredicate(pred)
    }
}

public class AssertError(
    public val assert: Assert,
    public val reason: AssertReason,
) : Exception(reason.formatMessage(assert)) {
    public fun panic(): Nothing = throw this

    public fun fmt(): String = message ?: ""

    public companion object {
        public fun panic(err: AssertError): Nothing = err.panic()
    }
}

public sealed class AssertReason {
    public fun fmt(): String = toString()

    public data class UnexpectedFailure(
        val actualCode: Int?,
    ) : AssertReason()

    public object UnexpectedSuccess : AssertReason()

    public object UnexpectedCompletion : AssertReason()

    public object CommandInterrupted : AssertReason()

    public data class UnexpectedReturnCode(
        val caseTree: CaseTree,
    ) : AssertReason()

    public data class UnexpectedStdout(
        val caseTree: CaseTree,
    ) : AssertReason()

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
