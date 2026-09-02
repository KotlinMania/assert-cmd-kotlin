# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 8/8 (100.0%)
- **Function parity:** 93/95 matched (target 199) — 97.9%
- **Class/type parity:** 26/27 matched (target 45) — 96.3%
- **Combined symbol parity:** 119/122 matched (target 244) — 97.5%
- **Average inline-code cosine:** 0.49 (function body across 8 matched files)
- **Average documentation cosine:** 0.41 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 6 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. assert

- **Target:** `assertcmd.Assert`
- **Similarity:** 0.50
- **Dependents:** 1
- **Priority Score:** 1035505.1
- **Functions:** 39/41 matched (target 103)
- **Missing functions:** `convert_code`, `convert_output`
- **Types:** 13/14 matched (target 24)
- **Missing types:** `Predicate`
- **Tests:** 8/10 matched

### 2. cmd

- **Target:** `assertcmd.Cmd`
- **Similarity:** 0.41
- **Dependents:** 0
- **Priority Score:** 2705.9
- **Functions:** 26/26 matched (target 38)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 3. output

- **Target:** `assertcmd.Output`
- **Similarity:** 0.47
- **Dependents:** 0
- **Priority Score:** 2005.3
- **Functions:** 13/13 matched (target 30)
- **Missing functions:** _none_
- **Types:** 7/7 matched (target 11)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 4. cargo

- **Target:** `assertcmd.Cargo`
- **Similarity:** 0.35
- **Dependents:** 0
- **Priority Score:** 1006.5
- **Functions:** 7/7 matched (target 15)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 4)
- **Missing types:** _none_

### 5. color

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

