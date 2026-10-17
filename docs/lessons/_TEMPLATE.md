# Lesson NN — <Tool>

<!--
  Follows docs/LEARNING-PRINCIPLES.md: concrete first, see it fail then pass, real output only,
  state/flow made visible, short measured feedback loops. Sections 1-2 are hands-on and come
  before any explanation of mechanism.
-->

| | |
|---|---|
| **Status** | ⬜ |
| **Version** | |
| **Phase** | |
| **Python analog** | |
| **Works on** | source / AST / bytecode / runtime / git history / dependency metadata |
| **Lab module** | `labs/lab-NN-<tool>` |

## 1. Try it yourself

Run from the repo root, in order. Failing runs come first.

| # | Command | Expect | Look at |
|---|---|---|---|
| 1 | `./mvnw ...` (see it fail) | **FAIL** — `<key line>` | file:line, report path |
| 2 | `./mvnw ...` (see it pass) | **PASS** | |
| 3 | Break-it: change one thing, rerun | **FAIL** — `<key line>` | |
| 4 | Poke interactively (`jshell`, single-file `java`, open HTML report) | | |

Undo for any experiment that edits tracked files: `git checkout -- <path>`.

## 2. What you saw

Real output from the experiments above, annotated. Flawed code and its fixed version side by
side (one example per important rule):

```java
// bad
```

```text
(real output — never invented)
```

```java
// good
```

Why the fix is correct, not just why the warning went away. Any surprise met during the
experiments goes here as its own observation.

## 3. What it catches and why it matters

Defect class. Which real-world failure it prevents. How early in the loop it catches it. How
this lesson pulled that normally-late consequence inside the feedback loop.

## 4. How it works

Mechanism in a few lines (compiler plugin? bytecode analysis? instrumentation?). Implications:
what it *cannot* see. Make hidden state/flow visible where possible (lifecycle prints, reports,
traces).

## 5. Setup

```xml
<!-- Minimal pom.xml snippet -->
```

Config file(s) under `config/` if any.

## 6. Tuning and false positives

- Useful config knobs (each with a command to see its effect):
- One false positive met, and the narrowest suppression for it:
- Rules disabled and why (also logged in `docs/DECISIONS.md`):

## 7. Cost and gate decision

| Metric | Value |
|---|---|
| Time added to `./qa/check.sh` | |
| Fastest feedback command and its time | |
| Noise (findings on clean core / false positives) | |
| Gate | fail / warn / drop |
| Tier | T0 / T1 / T2 / T3 |

Reasoning:

## 8. Overlap with other tools

What this tool catches that others don't, and vice versa.

## 9. Gotchas

## 10. Cheat sheet

```bash
# see it fail
# see it pass
# run on one module
# skip temporarily
# report location
```

## Done criteria

- [ ] "Try it yourself" experiments written, each run here, fail-first, with expected output
- [ ] Lab module created; tool fires; real output captured
- [ ] Findings fixed; tool green on lab "good" code
- [ ] State/flow of the tool made visible (report, trace, lifecycle print, jshell)
- [ ] Defect class, mechanism, Python analog written up
- [ ] At least one knob tuned and one suppression demonstrated
- [ ] Wired into parent POM + `afjava-core` + `qa/check.sh`
- [ ] Runtime and noise measured
- [ ] Cheat sheet complete
- [ ] TRACKER.md + DECISIONS.md updated; committed
