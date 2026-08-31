# port-lint Proposed Changes

**Generated:** 2026-08-31
**Source:** tmp/assert_cmd/src
**Target:** src/commonMain/kotlin/io/github/kotlinmania/assertcmd

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/io/github/kotlinmania/assertcmd/Assert.kt` | `// port-lint: source assert_cmd/src/assert.rs` | `// port-lint: source assert.rs` | `assert.rs` | `port-lint provenance header matched only after fallback normalization: 'assert_cmd/src/assert.rs' vs expected 'assert.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/assertcmd/AssertTest.kt` | `// port-lint: tests assert_cmd/src/assert.rs` | `// port-lint: tests assert.rs` | `assert.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:assert_cmd/src/assert.rs' vs expected 'assert.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/assertcmd/Cmd.kt` | `// port-lint: source assert_cmd/src/cmd.rs` | `// port-lint: source cmd.rs` | `cmd.rs` | `port-lint provenance header matched only after fallback normalization: 'assert_cmd/src/cmd.rs' vs expected 'cmd.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/assertcmd/CmdTest.kt` | `// port-lint: tests assert_cmd/src/cmd.rs` | `// port-lint: tests cmd.rs` | `cmd.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:assert_cmd/src/cmd.rs' vs expected 'cmd.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/assertcmd/Output.kt` | `// port-lint: source assert_cmd/src/output.rs` | `// port-lint: source output.rs` | `output.rs` | `port-lint provenance header matched only after fallback normalization: 'assert_cmd/src/output.rs' vs expected 'output.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/assertcmd/OutputTest.kt` | `// port-lint: tests assert_cmd/src/output.rs` | `// port-lint: tests output.rs` | `output.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:assert_cmd/src/output.rs' vs expected 'output.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/assertcmd/Cargo.kt` | `// port-lint: source assert_cmd/src/cargo.rs` | `// port-lint: source cargo.rs` | `cargo.rs` | `port-lint provenance header matched only after fallback normalization: 'assert_cmd/src/cargo.rs' vs expected 'cargo.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/assertcmd/CargoTest.kt` | `// port-lint: tests assert_cmd/src/cargo.rs` | `// port-lint: tests cargo.rs` | `cargo.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:assert_cmd/src/cargo.rs' vs expected 'cargo.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/assertcmd/Color.kt` | `// port-lint: source assert_cmd/src/color.rs` | `// port-lint: source color.rs` | `color.rs` | `port-lint provenance header matched only after fallback normalization: 'assert_cmd/src/color.rs' vs expected 'color.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/assertcmd/ColorTest.kt` | `// port-lint: tests assert_cmd/src/color.rs` | `// port-lint: tests color.rs` | `color.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:assert_cmd/src/color.rs' vs expected 'color.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/assertcmd/bin/BinFixture.kt` | `// port-lint: source assert_cmd/src/bin/bin_fixture.rs` | `// port-lint: source bin/bin_fixture.rs` | `bin/bin_fixture.rs` | `port-lint provenance header matched only after fallback normalization: 'assert_cmd/src/bin/bin_fixture.rs' vs expected 'bin/bin_fixture.rs'` |
| `src/commonMain/kotlin/io/github/kotlinmania/assertcmd/Lib.kt` | `// port-lint: source assert_cmd/src/lib.rs` | `// port-lint: source lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'assert_cmd/src/lib.rs' vs expected 'lib.rs'` |
