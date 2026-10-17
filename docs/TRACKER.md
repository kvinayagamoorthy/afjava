# afjava — Tracker

Legend: ⬜ not started · 🟡 in progress · ✅ done · ⏸ deferred · ❌ dropped

## Current focus

- **Lesson:** 02 — AssertJ
- **Status:** ⬜ not started
- **Next step:** Add AssertJ to the BOM-managed test deps; lab-02 rewrites the failing
  assertion demos of lab-01 in AssertJ and compares messages side by side (collections, DN
  lists, exceptions, soft assertions). Then migrate core tests and compare readability.
- **Blockers:** none

> Update this block at the end of every session. It is the single resume point.

## Lesson status

| # | Tool | Status | Version | Lesson doc | Lab module | Gate | Tier | Time added | Done on |
|---|---|---|---|---|---|---|---|---|---|
| 00 | Foundation (wrapper, parent POM, javac lint, check.sh) | ✅ | Maven 3.9.16, compiler 3.16.0 | [00](lessons/00-foundation.md) | lab-00-javac-lint | fail | T1 | 0 (≈1.5 s total) | 2026-10-05 |
| 01 | JUnit (Jupiter; JUnit 6) | ✅ | JUnit 6.1.3, Surefire 3.6.0, tree-reporter 2.0.0 | [01](lessons/01-junit.md) | lab-01-junit5 | fail | T1 | +2.7 s (build step 4.2 s) | 2026-10-17 |
| 02 | AssertJ | ⬜ | | | | | | | |
| 03 | Mockito | ⬜ | | | | | | | |
| 04 | JaCoCo | ⬜ | | | | | | | |
| 05 | Spotless | ⬜ | | | | | | | |
| 06 | Checkstyle | ⬜ | | | | | | | |
| 07 | PMD + CPD | ⬜ | | | | | | | |
| 08 | Error Prone | ⬜ | | | | | | | |
| 09 | NullAway + JSpecify | ⬜ | | | | | | | |
| 10 | SpotBugs | ⬜ | | | | | | | |
| 11 | Find Security Bugs | ⬜ | | | | | | | |
| 12 | ArchUnit | ⬜ | | | | | | | |
| 13 | PIT (pitest) | ⬜ | | | | | | | |
| 14 | Testcontainers | ⬜ | | | | | | | |
| 15 | OWASP Dependency-Check | ⬜ | | | | | | | |
| 16 | gitleaks | ⬜ | | | | | | | |
| 17 | TruffleHog | ⬜ | | | | | | | |
| 18 | Checker Framework | ⬜ | | | | | | | |
| 19 | Integration (hooks, CI, tiering) | ⬜ | | | | | | | |
| 20 | Capstone + template | ⬜ | | | | | | | |

Column meaning:
- **Gate** — `fail` (breaks build), `warn` (reported only), `drop` (not adopted).
- **Tier** — T0 pre-commit · T1 fast check · T2 full check · T3 scheduled/CI (see plan §6).
- **Time added** — wall-clock added to `./qa/check.sh` (measured, not guessed).

## Open items and ideas

Things noticed along the way, not yet acted on. Move to DECISIONS.md once decided.

| Raised | Item | Notes |
|---|---|---|
| 2026-10-17 (L01) | Labs step takes ~6.3 s, 2 s of it the deliberate `component` sleep in `TaggedTest` | Could exclude the `component` tag in the labs step; kept for now since labs are opt-in (`--labs`) |
| 2026-10-17 (L01) | Third-party Surefire tree reporter (`me.fabriciorby`) | Check it in Dependency-Check (lesson 15); drop if it ever blocks a Surefire upgrade |
| 2026-10-17 (L01) | One Maven invocation per `check.sh` step costs ~1 s JVM start each | Revisit (single `verify`, or mvnd) once T1 nears its 90 s budget (D006) |
| 2026-10-17 (L01) | Value equality in `Rdn` is exact (`cn=Admin` ≠ `cn=admin`) | Deliberate boundary (THEORY.md). Schema-aware matching could be a later domain step if a lesson needs it |
| 2026-10-17 (L01) | Random order applies to labs too | Labs tagged `order-dependent` are excluded by default, so labs stay green |
| 2026-10-17 (L01) | Plan for lesson 02 (AssertJ) | Rewrite lab-01's failing assertions (`AssertionMessagesTest`, exception tests) in AssertJ, compare messages side by side; DN list/collection assertions; soft assertions vs `assertAll`; then migrate core tests and compare readability |

## Backlog (optional)

| Tool | Status | Notes |
|---|---|---|
| jqwik | ⬜ | Property-based tests for DN / filter parsers |
| Jazzer | ⬜ | Fuzz DN / filter parsers |
| Maven Enforcer | ⬜ | |
| dependency:analyze | ⬜ | |
| OpenRewrite | ⬜ | |
| CycloneDX SBOM | ⬜ | |
| Modernizer / jdeps / JPMS | ⬜ | |
| japicmp / Revapi | ⬜ | |
| Renovate / Dependabot | ⬜ | |
| JMH | ⬜ | |

## Session log

Append-only, newest at bottom. One entry per working session.

| Date | Lesson | What was done | Next |
|---|---|---|---|
| 2026-10-05 | — | Created plan, tracker, lesson template, CLAUDE.md, README, .gitignore | Start lesson 00 |
| 2026-10-05 | 00 | Wrapper, project-scoped settings, parent POM (pinned plugins), core `Rdn`, javac `-Xlint:all` + `failOnWarning` gate, lab-00, `qa/check.sh` | Lesson 01 |
| 2026-10-17 | 00 | Added LEARNING-PRINCIPLES.md (Victor, Senge); experiment-first template; lesson 00 "Try it yourself" (fail/pass commands, `-Werror` hides fall-through, Maven flow, jshell) | Lesson 01 |
| 2026-10-17 | 01 | Naur theory building (THEORY.md, tool-theory section); DN parser + 67 tests; lab-01 (lifecycle flow, shared state vs seeds, assertion messages, exception traps, tables, tags); check.sh random order with seed, test totals, `[XX]` rows | Lesson 02 |
| 2026-10-17 | — | Saved session knowledge before context compaction: open items in TRACKER, agent pitfalls in CLAUDE.md, jshell tip in lesson 00 | Lesson 02 |
