# afjava — Tracker

Legend: ⬜ not started · 🟡 in progress · ✅ done · ⏸ deferred · ❌ dropped

## Current focus

- **Lesson:** 01 — JUnit 5
- **Status:** ⬜ not started
- **Next step:** Add JUnit 5 BOM to parent POM; write `RdnTest`; grow domain with a
  `DistinguishedName` parser and test it with `@ParameterizedTest`, `@Nested`, `assertThrows`.
  Create `labs/lab-01-junit5` (lifecycle, dynamic tests, tags, unit vs component split).
- **Blockers:** none

> Update this block at the end of every session. It is the single resume point.

## Lesson status

| # | Tool | Status | Version | Lesson doc | Lab module | Gate | Tier | Time added | Done on |
|---|---|---|---|---|---|---|---|---|---|
| 00 | Foundation (wrapper, parent POM, javac lint, check.sh) | ✅ | Maven 3.9.16, compiler 3.16.0 | [00](lessons/00-foundation.md) | lab-00-javac-lint | fail | T1 | 0 (≈1.5 s total) | 2026-10-05 |
| 01 | JUnit 5 | ⬜ | | | | | | | |
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
