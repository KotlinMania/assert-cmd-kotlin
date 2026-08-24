=== Deep Analysis: tmp/assert_cmd/src (rust) -> src/commonMain/kotlin (kotlin) ===

Scanning source codebase (rust)...
Codebase: tmp/assert_cmd/src (rust)
  Files: 8
  Total imports: 46
  Most depended: assert (1 dependents)

Scanning target codebase (kotlin)...
Codebase: src/commonMain/kotlin (kotlin)
  Files: 11
  Total imports: 19

Comparing codebases...
Computing AST similarities...

=== Codebase Comparison Report ===

Source: tmp/assert_cmd/src (8 files)
Target: src/commonMain/kotlin (11 files)
Scoring invariant: FnSim is required function body/parameter similarity. Class/type and symbol parity are reported beside it; whole-file shape is diagnostic only.

Matched:   8 files
Unmatched: 0 source, 0 target

=== Matched Files (by porting priority) ===

Source                        Target                        FnSim     Dependents FunctionParityTypeParity  Priority
 --------------------------------------------------------------------------------------------------------------------------
assert                        assertcmd.Assert              0.50      1          39/41         13/14       1035505.1 
cmd                           assertcmd.Cmd                 0.41      0          26/26         1/1         2705.9    
output                        assertcmd.Output              0.47      0          13/13         7/7         2005.3    
cargo                         assertcmd.Cargo               0.35      0          7/7           3/3         1006.5    
color                         assertcmd.Color               0.66      0          6/6           2/2         803.4     
bin.bin_fixture               bin.BinFixture                0.51      0          2/2           0/0         204.9     
lib                           assertcmd.Lib                 1.00      0          0/0           1/1         100.0     
macros                        assertcmd.Macros [ZERO]       0.00      0          0/0           0/0         10.0      

=== Function and Symbol Details ===

assert -> assertcmd.Assert
  similarity: 0.50, priority: 1035505.1, dependents: 1
  functions: 39/41 matched (target total: 101, required body score: 0.50)
  missing functions: convert_code, convert_output
  types: 13/14 matched (target total: 24)
  missing types: Predicate
  tests: 8/10 matched

cmd -> assertcmd.Cmd
  similarity: 0.41, priority: 2705.9, dependents: 0
  functions: 26/26 matched (target total: 38, required body score: 0.41)
  missing functions: none
  types: 1/1 matched (target total: 2)
  missing types: none

output -> assertcmd.Output
  similarity: 0.47, priority: 2005.3, dependents: 0
  functions: 13/13 matched (target total: 30, required body score: 0.47)
  missing functions: none
  types: 7/7 matched (target total: 11)
  missing types: none
  tests: 1/1 matched

cargo -> assertcmd.Cargo
  similarity: 0.35, priority: 1006.5, dependents: 0
  functions: 7/7 matched (target total: 9, required body score: 0.35)
  missing functions: none
  types: 3/3 matched (target total: 3)
  missing types: none

color -> assertcmd.Color
  similarity: 0.66, priority: 803.4, dependents: 0
  functions: 6/6 matched (target total: 9, required body score: 0.66)
  missing functions: none
  types: 2/2 matched (target total: 2)
  missing types: none

bin.bin_fixture -> bin.BinFixture
  similarity: 0.51, priority: 204.9, dependents: 0
  functions: 2/2 matched (target total: 2, required body score: 0.51)
  missing functions: none
  types: 0/0 matched (target total: 1)
  missing types: none

lib -> assertcmd.Lib
  similarity: 1.00, priority: 100.0, dependents: 0
  functions: 0/0 matched (target total: 0, required body score: 1.00)
  missing functions: none
  types: 1/1 matched (target total: 6)
  missing types: none

macros -> assertcmd.Macros [ZERO]
  similarity: 0.00, priority: 10.0, dependents: 0
  functions: 0/0 matched (target total: 4, required body score: 0.00)
  missing functions: none
  types: 0/0 matched (target total: 0)
  missing types: none
  *** CHEAT DETECTION / SCORING FAILURE ***
  function-by-function score forced to 0: no source functions found; target defines functions; report scoring is function-by-function only


=== Scores Forced To 0 ===

  - macros -> assertcmd.Macros: no source functions found; target defines functions; report scoring is function-by-function only

=== Porting Quality Summary ===

Matched by exact header:          8 / 8
Matched by provenance fallback:   0 / 8
Matched by name:                  0 / 8
Total TODOs in target: 0
Total lint errors:    1
Stub files:           0

=== Big Picture ===

- Missing files: 0
- Incomplete ports (similarity < 60%): 6
- Stub files: 0
- Files missing functions: 1 (total deficit: 2 functions)
- Type definitions missing: 1
- Files missing tests: 1 (total deficit: 2 unported `#[test]` functions)
- Documentation coverage: 88 / 2546 lines (3%)

Primary focus: port missing functions/tests to reach per-file parity (2 functions, 2 tests)

=== Files with Issues ===

File                          Similarity LineRatio  FunctionParityTests     TODOs Lint  Status
----------------------------------------------------------------------------------------------------
assertcmd.Assert              0.50       0.00       39/41         8/10      0     0     MISSING_FUNCS
  missing functions: `convert_code`, `convert_output`
  missing types: `Predicate`
assertcmd.Cmd                 0.41       0.00       26/26         -         0     0     
assertcmd.Output              0.47       0.00       13/13         1/1       0     0     
assertcmd.Cargo               0.35       0.00       7/7           -         0     0     LOW_SIM
bin.BinFixture                0.51       0.00       2/2           -         0     1     LINT
assertcmd.Macros [ZERO]       0.00       0.00       -             -         0     0     LOW_SIM

=== Porting Recommendations ===

Incomplete ports (similarity < 60%): 6
Missing files: 0

Incomplete ports to complete:
  assert                         similarity=0.50 function_parity=39/41 dependents=1
    missing functions: `convert_code`, `convert_output`
    missing types: `Predicate`
  cmd                            similarity=0.41 function_parity=26/26 dependents=0
  output                         similarity=0.47 function_parity=13/13 dependents=0
  cargo                          similarity=0.35 function_parity=7/7 dependents=0
  bin.bin_fixture                similarity=0.51 function_parity=2/2 dependents=0
  macros                         similarity=0.00 function_parity=- dependents=0

=== Documentation Gaps ===

There is missing documentation that is hurting overall scoring.
Documentation coverage: 88 / 2546 lines (3%)
Files with >20% doc gap: 6

File                          Src Docs    Tgt Docs    Gap %     DocSim    DocAmt    DocEq     
----------------------------------------------------------------------------------------------
assert                        948         18          98%       0.21      0.02      0.11      
cmd                           780         3           99%       0.35      0.00      0.18      
output                        256         24          90%       0.56      0.09      0.33      
cargo                         246         15          93%       0.43      0.06      0.25      
lib                           204         3           98%       0.38      0.01      0.20      
macros                        112         12          89%       0.33      0.11      0.22      

=== Generating Reports ===

✅ Generated: port_status_report.md
✅ Generated: high_priority_ports.md
✅ Generated: NEXT_ACTIONS.md
✅ Generated: port_lint_proposed_changes.md

📁 All reports generated successfully!
