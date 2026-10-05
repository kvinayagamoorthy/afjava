# Lesson NN — <Tool>

| | |
|---|---|
| **Status** | ⬜ |
| **Version** | |
| **Phase** | |
| **Python analog** | |
| **Works on** | source / AST / bytecode / runtime / git history / dependency metadata |
| **Lab module** | `labs/lab-NN-<tool>` |

## 1. What it catches and why it matters

Defect class. Which real-world failure it prevents. How early in the loop it catches it.

## 2. How it works

Mechanism in a few lines (compiler plugin? bytecode analysis? instrumentation?). Implications:
what it *cannot* see.

## 3. Setup

```xml
<!-- Minimal pom.xml snippet -->
```

Config file(s) under `config/` if any.

## 4. Lab: make it fire

Flawed code in the lab module (one example per important rule):

```java
// bad
```

Real output captured from `./mvnw ...`:

```text
(paste real output — never invented)
```

## 5. Fix

```java
// good
```

Why the fix is correct, not just why the warning went away.

## 6. Tuning and false positives

- Useful config knobs:
- One false positive met, and the narrowest suppression for it:
- Rules disabled and why (also logged in `docs/DECISIONS.md`):

## 7. Cost and gate decision

| Metric | Value |
|---|---|
| Time added to `./qa/check.sh` | |
| Noise (findings on clean core / false positives) | |
| Gate | fail / warn / drop |
| Tier | T0 / T1 / T2 / T3 |

Reasoning:

## 8. Overlap with other tools

What this tool catches that others don't, and vice versa.

## 9. Gotchas

## 10. Cheat sheet

```bash
# run
# run on one module
# skip temporarily
# report location
```

## Done criteria

- [ ] Read: defect class, mechanism, Python analog written up
- [ ] Lab module created; tool fires; real output captured above
- [ ] Findings fixed; tool green on lab "good" code
- [ ] At least one knob tuned and one suppression demonstrated
- [ ] Wired into parent POM + `afjava-core` + `qa/check.sh`
- [ ] Runtime and noise measured
- [ ] Cheat sheet complete
- [ ] TRACKER.md + DECISIONS.md updated; committed
