# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 8/11 (72.7%)
- **Function parity:** 93/99 matched (target 199) — 93.9%
- **Class/type parity:** 27/28 matched (target 51) — 96.4%
- **Combined symbol parity:** 120/127 matched (target 250) — 94.5%
- **Average inline-code cosine:** 0.42 (function body across 7 matched files)
- **Average documentation cosine:** 0.39 (doc text across 7 matched files)
- **Cheat-zeroed Files:** 1
- **Critical Issues:** 7 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. assert_cmd.assert

- **Target:** `assertcmd.Assert`
- **Similarity:** 0.50
- **Dependents:** 1
- **Priority Score:** 1035505.1
- **Functions:** 39/41 matched (target 103)
- **Missing functions:** `convert_code`, `convert_output`
- **Types:** 13/14 matched (target 24)
- **Missing types:** `Predicate`
- **Tests:** 8/10 matched

### 2. assert_cmd.cmd

- **Target:** `assertcmd.Cmd`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 2705.9
- **Functions:** 26/26 matched (target 38)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 3. assert_cmd.output

- **Target:** `assertcmd.Output`
- **Similarity:** 0.47
- **Dependents:** 0
- **Priority Score:** 2005.3
- **Functions:** 13/13 matched (target 30)
- **Missing functions:** _none_
- **Types:** 7/7 matched (target 11)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 4. assert_cmd.cargo

- **Target:** `assertcmd.Cargo`
- **Similarity:** 0.35
- **Dependents:** 0
- **Priority Score:** 1006.5
- **Functions:** 7/7 matched (target 15)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_

### 5. assert_cmd.color

- **Target:** `assertcmd.Color`
- **Similarity:** 0.66
- **Dependents:** 0
- **Priority Score:** 803.4
- **Functions:** 6/6 matched (target 11)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 3)
- **Missing types:** _none_

### 6. bin.bin_fixture

- **Target:** `bin.BinFixture`
- **Similarity:** 0.55
- **Dependents:** 0
- **Priority Score:** 204.5
- **Functions:** 2/2 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

### 7. assert_cmd.lib

- **Target:** `assertcmd.Lib [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 6)
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
| `assert_cmd.macros` | `assertcmd.Macros` | `assert_cmd/src/macros` |

