# assert-cmd-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fassert--cmd--kotlin-blue.svg)](https://github.com/KotlinMania/assert-cmd-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/assert-cmd-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/assert-cmd-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/assert-cmd-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/assert-cmd-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`assert-rs/assert_cmd`](https://github.com/assert-rs/assert_cmd.git).

**Original Project:** This port is based on [`assert-rs/assert_cmd`](https://github.com/assert-rs/assert_cmd.git). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `assert-rs/assert_cmd`

> The text below is reproduced and lightly edited from [`https://github.com/assert-rs/assert_cmd.git`](https://github.com/assert-rs/assert_cmd.git). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## assert_cmd

> **Assert `process::Command`** - Easy command initialization and assertions.

[![Documentation](https://img.shields.io/badge/docs-master-blue.svg)][Documentation]
![License](https://img.shields.io/crates/l/assert_cmd.svg)
[![Crates Status](https://img.shields.io/crates/v/assert_cmd.svg)][Crates.io]

`assert_cmd` aims to simplify the process for doing integration testing of CLIs, including:
- Finding your crate's binary to test
- Assert on the result of your program's run.

## Example

Here's a trivial example:

```rust,no_run
use assert_cmd::Command;

let mut cmd = Command::cargo_bin("bin_fixture").unwrap();
cmd.assert().success();
```

See the [docs](http://docs.rs/assert_cmd) for more.

## Relevant crates

Other crates that might be useful in testing command line programs.
* [escargot][escargot] for more control over configuring the crate's binary.
* [duct][duct] for orchestrating multiple processes.
  * or [commandspec] for easier writing of commands
* [rexpect][rexpect] for testing interactive programs.
* [`assert_fs`][assert_fs] for filesystem fixtures and assertions.
  * or [tempfile][tempfile] for scratchpad directories.
* [dir-diff][dir-diff] for testing file side-effects.
* [cross][cross] for cross-platform testing.

[escargot]: http://docs.rs/escargot
[rexpect]: https://crates.io/crates/rexpect
[dir-diff]: https://crates.io/crates/dir-diff
[tempfile]: https://crates.io/crates/tempfile
[duct]: https://crates.io/crates/duct
[assert_fs]: https://crates.io/crates/assert_fs
[commandspec]: https://crates.io/crates/commandspec
[cross]: https://github.com/cross-rs/cross

## License

Licensed under either of

* Apache License, Version 2.0, ([LICENSE-APACHE](https://github.com/assert-rs/assert_cmd/blob/HEAD/LICENSE-APACHE) or <https://www.apache.org/licenses/LICENSE-2.0>)
* MIT license ([LICENSE-MIT](https://github.com/assert-rs/assert_cmd/blob/HEAD/LICENSE-MIT) or <https://opensource.org/license/mit>)

at your option.

## Testimonials

fitzgen
> assert_cmd is just such a pleasure to use every single time, I fall in love all over again
>
> bravo bravo WG-cli

passcod
> Running commands and dealing with output can be complex in many many ways, so assert_cmd smoothing that is excellent, very much welcome, and improves ergonomics significantly.

volks73
>  I have used [assert_cmd] in other projects and I am extremely pleased with it

coreyja
> [assert_cmd] pretty much IS my testing strategy so far, though my app under test is pretty small.
>
> This library has made it really easy to add some test coverage to my project, even when I am just learning how to write Rust!

## Contribution

Unless you explicitly state otherwise, any contribution intentionally
submitted for inclusion in the work by you, as defined in the Apache-2.0
license, shall be dual-licensed as above, without any additional terms or
conditions.

[Crates.io]: https://crates.io/crates/assert_cmd
[Documentation]: https://docs.rs/assert_cmd

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:assert-cmd-kotlin:0.1.0-SNAPSHOT")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`assert-rs/assert_cmd`](https://github.com/assert-rs/assert_cmd.git). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the assert_cmd authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`assert-rs/assert_cmd`](https://github.com/assert-rs/assert_cmd.git) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
