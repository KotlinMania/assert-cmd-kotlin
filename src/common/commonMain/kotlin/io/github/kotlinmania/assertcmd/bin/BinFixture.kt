// port-lint: source bin/bin_fixture.rs
package io.github.kotlinmania.assertcmd.bin

public object BinFixture {
    public fun run(
        env: Map<String, String> = emptyMap(),
        stdoutWriter: (String) -> Unit = {},
        stderrWriter: (String) -> Unit = {},
    ): Int {
        env["stdout"]?.let { stdoutWriter(it) }
        env["stderr"]?.let { stderrWriter(it) }
        val code = env["exit"]?.toIntOrNull() ?: 0
        return code
    }

    public fun main() {
        val code = run()
        if (code != 0) {
            // non-zero exit simulation
        }
    }
}
