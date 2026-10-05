# afjava — Tracker

Legend: ⬜ not started · 🟡 in progress · ✅ done · ⏸ deferred · ❌ dropped

## Current focus

- **Lesson:** 00 — Foundation
- **Status:** ⬜ not started
- **Next step:** Add Maven Wrapper, parent `pom.xml`, empty `afjava-core` module with one
  domain class + one test, and `qa/check.sh` skeleton printing PASS/FAIL + timings.
- **Blockers:** none

> Update this block at the end of every session. It is the single resume point.

## Lesson status

| # | Tool | Status | Version | Lesson doc | Lab module | Gate | Tier | Time added | Done on |
|---|---|---|---|---|---|---|---|---|---|
| 00 | Foundation (wrapper, parent POM, check.sh) | ⬜ | | — | — | — | — | | |
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
