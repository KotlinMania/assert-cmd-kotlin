# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 8/8 (100.0%)
- **Function parity:** 128/147 matched (target 217) — 87.1%
- **Class/type parity:** 27/27 matched (target 46) — 100.0%
- **Combined symbol parity:** 155/174 matched (target 263) — 89.1%
- **Average inline-code cosine:** 0.21 (function body across 8 matched files)
- **Average documentation cosine:** 0.41 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 7 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 1 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. assert

- **Target:** `assertcmd.Assert`
- **Similarity:** 0.15
- **Dependents:** 1
- **Priority Score:** 1109208.5
- **Functions:** 68/78 matched (target 120)
- **Missing functions:** `assert`, `into_code`, `into_code`, `into_output`, `into_output`, `into_output`, `assert`, `fmt`, `convert_code`, `convert_output`
- **Types:** 14/14 matched (target 25)
- **Missing types:** _none_
- **Tests:** 8/10 matched

### 2. output

- **Target:** `assertcmd.Output`
- **Similarity:** 0.09
- **Dependents:** 0
- **Priority Score:** 42909.1
- **Functions:** 18/22 matched (target 31)
- **Missing functions:** `ok`, `unwrap_err`, `new`, `new`
- **Types:** 7/7 matched (target 11)
- **Missing types:** _none_
- **Tests:** 2/2 matched

### 3. cmd

- **Target:** `assertcmd.Cmd`
- **Similarity:** 0.07
- **Dependents:** 0
- **Priority Score:** 33009.3
- **Functions:** 26/29 matched (target 38)
- **Missing functions:** `ok`, `unwrap_err`, `assert`
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 4. cargo

- **Target:** `assertcmd.Cargo`
- **Similarity:** 0.08
- **Dependents:** 0
- **Priority Score:** 21309.2
- **Functions:** 8/10 matched (target 15)
- **Missing functions:** `cargo_bin`, `cargo_bin`
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_

### 5. color

- **Target:** `assertcmd.Color`
- **Similarity:** 0.23
- **Dependents:** 0
- **Priority Score:** 807.7
- **Functions:** 6/6 matched (target 11)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_

### 6. bin.bin_fixture

- **Target:** `bin.BinFixture`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 209.4
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Matched

| Source | Target | Path |
|--------|--------|------|
| `lib` | `assertcmd.Lib` | `lib` |
| `macros` | `assertcmd.Macros` | `macros` |

