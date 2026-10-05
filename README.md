# afjava — Antifragile Java

Hands-on lab for the Java code-quality toolchain: tests, formatting, static analysis, null
safety, security scanning, architecture rules, mutation testing and secrets detection — learned
one tool at a time on a small identity-directory sample domain.

Goal: find defects as far left as possible (compiler → unit tests), so very few reach QA or
customers.

- **Plan:** [docs/00-plan.md](docs/00-plan.md)
- **Status / resume point:** [docs/TRACKER.md](docs/TRACKER.md)
- **Decisions:** [docs/DECISIONS.md](docs/DECISIONS.md)
- **Lessons:** [docs/lessons/](docs/lessons/)

## Toolchain covered

JUnit 5 · AssertJ · Mockito · JaCoCo · Spotless · Checkstyle · PMD/CPD · Error Prone ·
NullAway · SpotBugs · Find Security Bugs · ArchUnit · PIT · Testcontainers ·
OWASP Dependency-Check · gitleaks · TruffleHog · Checker Framework

## Quick start

Requires JDK 21 and Docker (for Testcontainers lessons).

```bash
./qa/check.sh          # fast gates (every change)
./qa/check.sh --full   # + mutation tests, component tests, Checker Framework
```

(`qa/check.sh` arrives in lesson 00.)
